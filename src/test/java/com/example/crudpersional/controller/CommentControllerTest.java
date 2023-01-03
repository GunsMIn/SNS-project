package com.example.crudpersional.controller;

import com.example.crudpersional.config.encrypt.EncrypterConfig;
import com.example.crudpersional.domain.dto.comment.CommentResponse;
import com.example.crudpersional.domain.dto.comment.PostCommentRequest;
import com.example.crudpersional.domain.entity.Comment;
import com.example.crudpersional.exceptionManager.UserException;
import com.example.crudpersional.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
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
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PostService postService;

    @Autowired
    ObjectMapper objectMapper;


    @Nested
    @DisplayName("댓글 작성")
    class CommentWrite{

        @Test
        @WithMockUser
        @DisplayName("댓글 등록 성공")
        void 댓글_등록_성공() throws Exception {
            /**given**/
            PostCommentRequest postCommentRequest = new PostCommentRequest("댓글 1");

            CommentResponse response = CommentResponse.builder()
                    .id(1l)
                    .comment("테스트 댓글")
                    .postId(1l)
                    .userName("김건우")
                    .createdAt(String.valueOf(LocalDateTime.now()))
                    .lastModifiedAt(String.valueOf(LocalDateTime.now()))
                    .build();

           /**when**/
            when(postService.writeComment(any(), any(), any())).thenReturn(response);
            String url = "/api/v1/posts/1/comments";
            mockMvc.perform(post(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(postCommentRequest)))
                    .andDo(print())
                    .andExpect(jsonPath("$.result.id").value(1l))
                    .andExpect(jsonPath("$.result.comment").value("테스트 댓글"))
                    .andExpect(jsonPath("$.result.userName").value("김건우"))
                    .andExpect(status().isOk());

            verify(postService, atLeastOnce()).writeComment(any(), anyString(), anyString());

        }

        @Test
        @WithAnonymousUser
        @DisplayName("댓글 등록 실패 : 권한 없음")
        void 댓글_등록_실패_권한인증없음() throws Exception {
            /**given**/
            PostCommentRequest postCommentRequest = new PostCommentRequest("댓글 1");

            CommentResponse response = CommentResponse.builder()
                    .id(1l)
                    .comment("테스트 댓글")
                    .postId(1l)
                    .userName("김건우")
                    .createdAt(String.valueOf(LocalDateTime.now()))
                    .lastModifiedAt(String.valueOf(LocalDateTime.now()))
                    .build();

            /**when**/
            when(postService.writeComment(any(), any(), any())).thenThrow(new UserException(ErrorCode.INVALID_PERMISSION));
            String url = "/api/v1/posts/1/comments";
            mockMvc.perform(post(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(postCommentRequest)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            verify(postService, never()).writeComment(any(), anyString(), anyString());
        }
    }


    @Test
    @WithMockUser
    @DisplayName("댓글 조회 페이징 리스트")
    void 댓글_조회_성공() throws Exception {

        PageRequest request = PageRequest.of(0, 10, Sort.Direction.DESC, "registeredAt");
        String url = "/api/v1/posts/1/comments";

        mockMvc.perform(get(url)
                .param("size", "10")
                .param("sort", "registeredAt")
                .param("direction", "Sort.Direction.DESC"))
                .andExpect(status().isOk());


        assertThat(request.getPageNumber()).isEqualTo(0);
        assertThat(request.getPageSize()).isEqualTo(10);
        assertThat(request.getSort()).isEqualTo(Sort.by("registeredAt").descending());
        verify(postService, times(1)).getComments(anyLong(),any());


    }
}