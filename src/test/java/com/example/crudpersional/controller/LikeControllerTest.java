package com.example.crudpersional.controller;

import com.example.crudpersional.config.encrypt.EncrypterConfig;
import com.example.crudpersional.service.LikeService;
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
    LikeService likeService;
    
    @MockBean
    EncrypterConfig encoderConfig;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithMockUser
    @DisplayName("????????? ??????")
    void ?????????_??????() throws Exception {
        LikeResponse response = LikeResponse.builder()
                .likeId(1l)
                .message("????????? ??????!")
                .userName("?????????")
                .postId(10l)
                .build();

        when(likeService.likes(anyLong(), anyString())).thenReturn(response);
        mockMvc.perform(post("/api/v1/posts/1/likes")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result").exists());

        verify(likeService,times(1)).likes(anyLong(), anyString());
    }


    @Test
    @DisplayName("????????? ?????? :  Login?????? ?????? ??????")
    @WithAnonymousUser // ????????? ?????????
    void like_fail1() throws Exception {
        mockMvc.perform(post("/api/v1/posts/1/likes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }


    @Test
    @DisplayName("????????? ??????(2) - ???????????? ???????????? ?????? ??????")
    @WithMockUser
    void like_fail2() throws Exception {
        /**void ???????????? ??????,doThrow??????! when???????????? ????????? ????????? ??????????????? ?????????. ????????? ????????? ??????**/
        doThrow(new PostException(ErrorCode.POST_NOT_FOUND))
                .when(likeService).likes(any(), any());

        mockMvc.perform(post("/api/v1/posts/1/likes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(ErrorCode.POST_NOT_FOUND.getStatus().value()));
    }





}