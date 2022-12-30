package com.example.crudpersional.service;

import com.example.crudpersional.config.jwt.JwtTokenUtil;
import com.example.crudpersional.domain.dto.user.*;
import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.domain.entity.UserRole;
import com.example.crudpersional.exceptionManager.ErrorCode;
import com.example.crudpersional.exceptionManager.UserException;
import com.example.crudpersional.mvc.dto.MemberForm;
import com.example.crudpersional.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.token.secret}")
    private String secretKey;

    private long expireTimeMs = 1000 * 60 * 60; //1시간

    /**회원가입**/
    public User join(UserJoinRequest userJoinRequest) {
        List<User> userList = userRepository.findByUserName(userJoinRequest.getUserName());
        //userList가 비어있지 않다면 이미 존재하는 회원 -> 409 Exception
        if (!userList.isEmpty()) {
            throw new UserException(ErrorCode.DUPLICATED_USER_NAME,String.format("%s은 이미 가입된 이름 입니다.", userJoinRequest.getUserName()));
        }
        //암호화 된 password db save
        String encodePassword = encoder.encode(userJoinRequest.getPassword());
        User user = userJoinRequest.toEntity(encodePassword);
        User savedUser = userRepository.save(user);
        log.info("저장된 회원 : {}",savedUser);
        return savedUser;
    }

    /**로그인**/
    //(1.아이디 존재 여부 2.비밀번호 일치 여부) -> 성공 시 토큰 응답
    public String login(String userName,String password) {
        log.info("로그인 아이디 : {} , 비밀번호 : {}" , userName,password);
        //1.아이디 존재 여부 체크
        User user = userRepository.findUserByUserName(userName)
                .orElseThrow(() -> new UserException(ErrorCode.USERNAME_NOT_FOUND,String.format("%s은 등록되어있지 않은 이름 입니다.", userName)));
        //2.비밀번호 유효성 검사
        if (!encoder.matches(password, user.getPassword())) {
            throw new UserException(ErrorCode.INVALID_PASSWORD,"해당 userName의 password가 잘못됐습니다");
        }
        //두 가지 확인중 예외 안났으면 Token발행
        String token = JwtTokenUtil.generateToken(userName, secretKey, expireTimeMs);
        return token;
    }

    public User getUserByUserName(String userName) {
        return userRepository.findUserByUserName(userName)
                .orElseThrow(() -> new UserException(ErrorCode.USERNAME_NOT_FOUND,String.format("%s은 등록되어있지 않은 이름 입니다.", userName)));
    }

    /**회원 조회**/
    @Transactional(readOnly = true)
    public UserSelectResponse getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ErrorCode.USERNAME_NOT_FOUND, String.format("%d번의 회원을 찾을 수 없습니다.", userId)));
        //엔티티 응답 객체로 변환
        UserSelectResponse userSelectResponse = new UserSelectResponse(user.getId(), user.getUsername(), user.getRole());
        return userSelectResponse;
    }

    /**회원 전체 조회**/
    @Transactional(readOnly = true)
    public List<UserListResponse> getUsers() {
        List<User> users = userRepository.findAll();
        List<UserListResponse> userListResponses = users.stream()
                .map(u -> new UserListResponse(u.getId(), u.getUsername()))
                .collect(toList());
        return userListResponses;
    }

    /**ADMIN 회원만 사용자의 권한을 바꿀 수 있는 서비스 로직**/
    @Transactional
    public UserAdminResponse changeRole(String name, Long id, UserRoleDto userRoleDto) {
        log.info("name : {}",name);
        log.info("userRoleDto : {}",userRoleDto);
        //회원 검증 + UserRole 검증 메서드
        User user = checkUserRole(name, id, userRoleDto);
        UserAdminResponse userAdminResponse = UserAdminResponse.of(user);
        return userAdminResponse;
    }

    /**1.해당 회원이 ADMIN인지 검사 / 2.{ID} 바뀔 대상 조회 / 3.RequsetBody의 값 검사 **/
    private User checkUserRole(String name, Long id, UserRoleDto userRoleDto) {
        //주의! findUser와 changedUser 변수 혼동 No
        //findUser는 토큰을 통해 인증 된 회원 -> 로그인된 회원
        User findUser = userRepository.findOptionalByUserName(name)
                .orElseThrow(() -> new UserException(ErrorCode.USERNAME_NOT_FOUND, "해당 회원은 존재하지 않습니다"));
        //Admin회원만 UserRole 전환 가능
        if (findUser.getRole().equals(UserRole.USER)) {
            throw new UserException(ErrorCode.INVALID_PERMISSION, "관리자(ADMIN)만 권한 변경을 할 수 있습니다.");
        }
        //@PathVariable로 들어온 id로 조회 -> role 변환 될 대상
        User changedUser = userRepository.findById(id)
                .orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND, String.format("%d번 회원은 존재하지 않습니다", id)));

        // requestBody에 들어올 값과 UserRole 비교
        if(userRoleDto.getRole().equals(UserRole.USER.name())) {
            changedUser.changeRole(UserRole.USER);
        }else if(userRoleDto.getRole().equals(UserRole.ADMIN.name())){
            changedUser.changeRole(UserRole.ADMIN);
        }else {
            throw new UserException(ErrorCode.USER_ROLE_NOT_FOUND, "권한 설정은 일반회원(USER),관리자(ADMIN)로 진행해야합니다.");
        }
        return changedUser;
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findOptionalByUserName(username).orElseThrow(()-> new UserException(ErrorCode.USER_NOT_FOUND,""));
    }
}
