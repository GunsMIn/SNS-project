package com.example.crudpersional.controller;

import com.example.crudpersional.domain.dto.*;
import com.example.crudpersional.domain.dto.user.UserJoinRequest;
import com.example.crudpersional.domain.dto.user.UserJoinResponse;
import com.example.crudpersional.domain.dto.user.UserLoginRequest;
import com.example.crudpersional.domain.dto.user.UserLoginResponse;
import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    //회원가입 컨트롤러
    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest userJoinRequest) {
        log.info("userJoinRequest :{} ", userJoinRequest);
        User user = userService.join(userJoinRequest);
        log.info("user :{} ", user);
        UserJoinResponse userJoinResponse = new UserJoinResponse(user.getId(),user.getUserName());
        return Response.success(userJoinResponse);
    }

    //로그인 컨트롤러
    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest userLoginRequest) {
        log.info("userLoginRequest : {} ",userLoginRequest);
        String token = userService.login(userLoginRequest.getUserName(), userLoginRequest.getPassword());
        return Response.success(new UserLoginResponse(token)); // 로그인 성공 시 토큰만 반환
    }
}
