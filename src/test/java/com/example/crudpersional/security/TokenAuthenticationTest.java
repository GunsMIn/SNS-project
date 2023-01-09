package com.example.crudpersional.security;

import com.example.crudpersional.config.jwt.JwtTokenUtil;
import com.example.crudpersional.domain.dto.user.UserJoinRequest;
import com.example.crudpersional.domain.entity.UserRole;
import com.example.crudpersional.service.UserService;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import com.example.crudpersional.config.jwt.JwtTokenUtil;
import com.example.crudpersional.domain.dto.user.UserJoinRequest;
import com.example.crudpersional.domain.dto.user.UserLoginRequest;
import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.exceptionManager.ErrorCode;
import com.example.crudpersional.exceptionManager.UserException;
import com.example.crudpersional.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(value = {
        "jwt.token.secret=hello"
})
@AutoConfigureMockMvc
public class TokenAuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserService userService;

    private String secretKey="hello";
    //token 생성하는 메서드
    private String generateToken_1hour(String userName, UserRole role){
        //claims 생성
        return JwtTokenUtil.generateToken(userName, secretKey, 1000 * 60 * 60);
    }
    private String generateToken_1msec(String userName, UserRole role){
        return JwtTokenUtil.generateToken(userName, secretKey, 1);
    }

    @Test
    @Transactional
    @DisplayName("jwt 인증 성공")
    void check_authenticatedUser() throws Exception {

        UserJoinRequest request = new UserJoinRequest("김건우", "1234");
        // dummy user생성
        userService.join(request);

        // token 생성
        String token = generateToken_1hour("김건우", UserRole.USER);

        mockMvc.perform(get("/api/v1/hello/api-auth-test")
                .with(csrf())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("ok"))
                .andDo(print());
    }




}
