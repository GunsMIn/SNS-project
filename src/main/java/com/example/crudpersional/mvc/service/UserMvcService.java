package com.example.crudpersional.mvc.service;

import com.example.crudpersional.config.jwt.JwtTokenUtil;
import com.example.crudpersional.domain.dto.user.UserJoinRequest;
import com.example.crudpersional.domain.dto.user.UserListResponse;
import com.example.crudpersional.domain.dto.user.UserSelectResponse;
import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.exceptionManager.ErrorCode;
import com.example.crudpersional.exceptionManager.UserException;
import com.example.crudpersional.mvc.dto.MemberForm;
import com.example.crudpersional.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor @Slf4j
public class UserMvcService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.token.secret}") // yml의 값을 가져올 수 있다.
    private String secretKey;

    private long expireTimeMs = 1000 * 60 * 60; // 1시간

    public User join(MemberForm memberForm) {
        log.info("서비스 단 유저:{}",memberForm);
        List<User> userList = userRepository.findByUserName(memberForm.getUserName());

        if (!userList.isEmpty()) {
            throw new UserException(ErrorCode.DUPLICATED_USER_NAME,String.format("%s은 이미 가입된 이름 입니다.", memberForm.getUserName()));
        }

        String encodePassword = encoder.encode(memberForm.getPassword());

        User user = memberForm.toEntity(encodePassword);

        User savedUser = userRepository.save(user);
        log.info("저장된 회원 : {}",savedUser);

        return savedUser;
    }

    //로그인 -> (1.아이디 존재 여부 2.비밀번호 일치 여부)
    public String login(String userName,String password) {
        log.info("서비스 아이디 비밀번호 :{} / {}" , userName,password);
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


}
