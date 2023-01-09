package com.example.crudpersional.controller;

import com.example.crudpersional.domain.dto.*;
import com.example.crudpersional.domain.dto.user.*;
import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Api(tags = "User(회원 API)")
@RequestMapping("/api/v1/users")
public class UserController {


    private final UserService userService;

    //회원가입 컨트롤러
    @ApiOperation(value = "회원가입", notes = "회원가입 API")
    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest userJoinRequest) {
        log.info("userJoinRequest :{} ", userJoinRequest);
        User user = userService.join(userJoinRequest);
        log.info("user :{} ", user);
        UserJoinResponse userJoinResponse = new UserJoinResponse(user.getId(),user.getUsername());
        return Response.success(userJoinResponse);
    }

    //로그인 컨트롤러
    @ApiOperation(value = "로그인", notes = "로그인 성공 후 JWT토큰 발급")
    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest userLoginRequest) {
        log.info("userLoginRequest : {} ",userLoginRequest);
        String token = userService.login(userLoginRequest.getUserName(), userLoginRequest.getPassword());
        return Response.success(new UserLoginResponse(token)); // 로그인 성공 시 토큰만 반환
    }



}
