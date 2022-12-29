package com.example.crudpersional.controller;

import com.example.crudpersional.config.jwt.JwtTokenUtil;
import com.example.crudpersional.domain.dto.user.UserJoinRequest;
import com.example.crudpersional.domain.dto.user.UserLoginRequest;
import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.exceptionManager.ErrorCode;
import com.example.crudpersional.exceptionManager.UserException;
import com.example.crudpersional.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.apache.tomcat.util.descriptor.web.FragmentJarScannerCallback;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@MockBean(JpaMetamodelMappingContext.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${jwt.secret}")
    private String secretKey;

    private String token;

    @BeforeEach()
    public void getToken() {
        long expireTimeMs = 1000 * 60 * 60;
        token = JwtTokenUtil.generateToken("gunwoo", secretKey, System.currentTimeMillis() + expireTimeMs);
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    @WithMockUser
    void 회원가입_성공() throws Exception {

        UserJoinRequest request
                = UserJoinRequest
                .builder()
                .userName("김건우")
                .password("1234")
                .build();


        User userEntity = User.builder()
                .id(100L)
                .userName(request.getUserName())
                .userName(request.getPassword())
                .build();

        when(userService.join(any()))
                .thenReturn(userEntity);

        String url = "/api/v1/users/join";
        mockMvc.perform(post(url).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").exists())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.userId").exists())
                .andExpect(jsonPath("$.result.userId").value(100l))
                .andExpect(jsonPath("$.result.userName").exists())
                .andExpect(jsonPath("$.result.userName").value("1234"))
                .andDo(print());

    }

    //userId가 중복 됐을 때 테스트
    @Test
    @DisplayName("회원가입 실패 : username 중복")
    @WithMockUser
    void 회원가입_실패() throws Exception {
        UserJoinRequest userJoinRequest = UserJoinRequest.builder()
                .userName("김건우")
                .password("1234")
                .build();

        when(userService.join(any()))
                .thenThrow(new UserException(ErrorCode.DUPLICATED_USER_NAME,"에러 메세지"));

        mockMvc.perform(post("/api/v1/users/join")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userJoinRequest)))
                .andDo(print())
                .andExpect(status().is(ErrorCode.DUPLICATED_USER_NAME.getStatus().value()));
    }

    //로그인 성공했을 때 테스트
    @Test
    @DisplayName("로그인 성공")
    @WithMockUser
    void 로그인_성공() throws Exception {
        UserLoginRequest userLoginRequest = UserLoginRequest.builder()
                .userName("김건우")
                .password("1234")
                .build();

        when(userService.login(any(), any()))
                .thenReturn("token");

        mockMvc.perform(post("/api/v1/users/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userLoginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").exists())
                .andExpect(jsonPath("$.result.jwt").exists())
        ;
    }

    //userName이 존재하지 않을 때 테스트
    @Test
    @DisplayName("로그인 실패 - username 없음")
    @WithMockUser
    void 로그인실패_username_없음() throws Exception {
        UserLoginRequest userLoginRequest = UserLoginRequest.builder()
                .userName("김건우")
                .password("1234")
                .build();

        when(userService.login(any(), any()))
                .thenThrow(new UserException(ErrorCode.USERNAME_NOT_FOUND, ""));

        mockMvc.perform(post("/api/v1/users/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userLoginRequest)))
                .andDo(print())
                .andExpect(status().is(ErrorCode.USERNAME_NOT_FOUND.getStatus().value()));
    }

    //password틀렸을 때 테스트
    @Test
    @DisplayName("로그인 실패 - password틀림")
    @WithMockUser
    void 로그인실패_password틀림() throws Exception {
        UserLoginRequest userLoginRequest = UserLoginRequest.builder()
                .userName("김건우")
                .password("1234")
                .build();

        when(userService.login(any(), any()))
                .thenThrow(new UserException(ErrorCode.INVALID_PASSWORD, ""));

        mockMvc.perform(post("/api/v1/users/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userLoginRequest)))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PASSWORD.getStatus().value()));
    }

}