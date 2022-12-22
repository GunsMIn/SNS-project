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


}
