package com.example.crudpersional.controller;

import com.example.crudpersional.config.encrypt.EncrypterConfig;
import com.example.crudpersional.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.example.crudpersional.config.encrypt.EncrypterConfig;
import com.example.crudpersional.domain.dto.post.*;
import com.example.crudpersional.domain.entity.Post;
import com.example.crudpersional.exceptionManager.ErrorCode;
import com.example.crudpersional.exceptionManager.PostException;
import com.example.crudpersional.service.PostService;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.meta.When;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(LikeController.class)
class LikeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PostService postService;

    @MockBean
    EncrypterConfig encoderConfig;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithMockUser
    @DisplayName("좋아요 성공")
    void 좋아요_성공() throws Exception {
        LikeResponse response = LikeResponse.builder()
                .likeId(1l)
                .message("좋아요 성공!")
                .userName("김건우")
                .postId(10l)
                .build();

        when(postService.like(anyLong(), anyString())).thenReturn(response);
        mockMvc.perform(post("/api/v1/posts/1/likes")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result").exists())
                .andExpect(jsonPath("$.result").value("좋아요 성공!"));

        verify(postService,times(1)).like(anyLong(), anyString());
    }


    @Test
    @DisplayName("좋아요 실패 :  Login하지 않은 경우")
    @WithAnonymousUser // 미인증 사용자
    void like_fail1() throws Exception {
        mockMvc.perform(post("/api/v1/posts/1/likes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }


    @Test
    @DisplayName("좋아요 실패(2) - 게시물이 존재하지 않는 경우")
    @WithMockUser
    void like_fail2() throws Exception {
        /**void 메서드의 경우,doThrow사용! when반환값이 없으면 구문이 컴파일되지 않는다. 선택의 여지가 없다**/
        doThrow(new PostException(ErrorCode.POST_NOT_FOUND))
                .when(postService).like(any(), any());

        mockMvc.perform(post("/api/v1/posts/1/likes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(ErrorCode.POST_NOT_FOUND.getStatus().value()));
    }





}