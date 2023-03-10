package com.example.crudpersional.controller;
import com.example.crudpersional.domain.dto.comment.*;
import com.example.crudpersional.exceptionManager.UserException;
import com.example.crudpersional.service.CommentService;
import com.example.crudpersional.service.PostService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.example.crudpersional.exceptionManager.ErrorCode;
import com.example.crudpersional.exceptionManager.PostException;
import java.time.LocalDateTime;


@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PostService postService;

    @MockBean
    CommentService commentService;

    @Autowired
    ObjectMapper objectMapper;


    @Nested
    @DisplayName("λκΈ μμ±")
    class CommentWrite{

        @Test
        @WithMockUser
        @DisplayName("λκΈ λ±λ‘ μ±κ³΅")
        void λκΈ_λ±λ‘_μ±κ³΅() throws Exception {
            /**given**/
            PostCommentRequest postCommentRequest = new PostCommentRequest("λκΈ 1");

            CommentResponse response = CommentResponse.builder()
                    .id(1l)
                    .comment("νμ€νΈ λκΈ")
                    .postId(1l)
                    .userName("κΉκ±΄μ°")
                    .createdAt(String.valueOf(LocalDateTime.now()))
                    .lastModifiedAt(String.valueOf(LocalDateTime.now()))
                    .build();

           /**when**/
            when(commentService.writeComment(any(), any(), any())).thenReturn(response);
            String url = "/api/v1/posts/1/comments";
            mockMvc.perform(post(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(postCommentRequest)))
                    .andDo(print())
                    .andExpect(jsonPath("$.result.id").value(1l))
                    .andExpect(jsonPath("$.result.comment").value("νμ€νΈ λκΈ"))
                    .andExpect(jsonPath("$.result.userName").value("κΉκ±΄μ°"))
                    .andExpect(status().isOk());

            verify(commentService, atLeastOnce()).writeComment(any(), anyString(), anyString());

        }

        @Test
        @WithAnonymousUser
        @DisplayName("λκΈ λ±λ‘ μ€ν¨ : κΆν μμ")
        void λκΈ_λ±λ‘_μ€ν¨_κΆνμΈμ¦μμ() throws Exception {
            /**given**/
            PostCommentRequest postCommentRequest = new PostCommentRequest("λκΈ 1");

            CommentResponse response = CommentResponse.builder()
                    .id(1l)
                    .comment("νμ€νΈ λκΈ")
                    .postId(1l)
                    .userName("κΉκ±΄μ°")
                    .createdAt(String.valueOf(LocalDateTime.now()))
                    .lastModifiedAt(String.valueOf(LocalDateTime.now()))
                    .build();

            /**when**/
            when(commentService.writeComment(any(), any(), any())).thenThrow(new UserException(ErrorCode.INVALID_PERMISSION));
            String url = "/api/v1/posts/1/comments";
            mockMvc.perform(post(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(postCommentRequest)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            verify(commentService, never()).writeComment(any(), anyString(), anyString());
        }


        @Test
        @WithMockUser
        @DisplayName("λκΈ λ±λ‘ μ€ν¨ : κ²μλ¬Ό μ‘΄μ¬νμ§ μμ")
        void λκΈ_λ±λ‘_μ€ν¨_κ²μλ¬Ό_μ‘΄μ¬X() throws Exception {
            /**given**/
            PostCommentRequest postCommentRequest = new PostCommentRequest("λκΈ 1");

            CommentResponse response = CommentResponse.builder()
                    .id(1l)
                    .comment("νμ€νΈ λκΈ")
                    .postId(1l)
                    .userName("κΉκ±΄μ°")
                    .createdAt(String.valueOf(LocalDateTime.now()))
                    .lastModifiedAt(String.valueOf(LocalDateTime.now()))
                    .build();

            /**when**/
            when(commentService.writeComment(any(), any(), any())).thenThrow(new PostException(ErrorCode.POST_NOT_FOUND));

            String url = "/api/v1/posts/1/comments";
            mockMvc.perform(post(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(postCommentRequest)))
                    .andDo(print())
                    .andExpect(status().isNotFound());

            verify(commentService, atLeastOnce()).writeComment(any(), anyString(), anyString());

        }
    }


    @Test
    @WithMockUser
    @DisplayName("λκΈ μ‘°ν νμ΄μ§ λ¦¬μ€νΈ")
    void λκΈ_μ‘°ν_μ±κ³΅() throws Exception {

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
        verify(commentService, times(1)).getComments(anyLong(),any());
    }


    @Nested
    @DisplayName("λκΈ μμ±")
    class CommentUpdate {

        @Test
        @WithMockUser
        @DisplayName("μμ  μ±κ³΅")
        void μμ _μ±κ³΅() throws Exception {

            CommentModifyRequest request = CommentModifyRequest.builder().comment("λκΈ").build();

            CommentUpdateResponse response = CommentUpdateResponse.builder()
                    .id(1l)
                    .postId(1L)
                    .comment("λκΈ")
                    .userName("κΉκ±΄μ°")
                    .createdAt(String.valueOf(LocalDateTime.now()))
                    .build();
            /*commentService.modifyComment();*/
            when(commentService.modifyComment(anyLong(),anyLong(), anyString(), anyString())).thenReturn(response);
            String url = "/api/v1/posts/1/comments/1";
            mockMvc.perform(put(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.id").value(1l))
                    .andExpect(jsonPath("$.result.comment").value("λκΈ"))
                    .andExpect(jsonPath("$.result.userName").value("κΉκ±΄μ°"));

            verify(commentService, times(1)).modifyComment(anyLong(),anyLong(),anyString(),anyString());
        }

        @Test
        @WithAnonymousUser
        @DisplayName("μμ  μ€ν¨ : μΈμ¦ μ€ν¨")
        void μμ _μ€ν¨() throws Exception {

            CommentModifyRequest request = CommentModifyRequest.builder().comment("λκΈ").build();
            /*commentService.modifyComment();*/
            when(commentService.modifyComment(anyLong(),anyLong(), anyString(), anyString()))
                    .thenThrow(new UserException(ErrorCode.INVALID_PERMISSION));
            String url = "/api/v1/posts/1/comments";
            mockMvc.perform(put(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());

            verify(commentService, never()).modifyComment(anyLong(),anyLong(),anyString(),anyString());
        }


        @Test
        @WithMockUser
        @DisplayName("μμ  μ€ν¨ : postκ° μλ κ²½μ°")
        void μμ _μ€ν¨_post_μμ() throws Exception {

            CommentModifyRequest request = CommentModifyRequest.builder().comment("λκΈ").build();

            /*commentService.modifyComment();*/
            when(commentService.modifyComment(anyLong(), anyLong(),anyString(), anyString()))
                    .thenThrow(new PostException(ErrorCode.POST_NOT_FOUND));

            String url = "/api/v1/posts/1/comments/1";
            mockMvc.perform(put(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                    .andDo(print())
                    .andExpect(status().isNotFound());

            verify(commentService,atLeastOnce()).modifyComment(anyLong(),anyLong(),anyString(),anyString());
        }

        @Test
        @WithMockUser
        @DisplayName("μμ  μ€ν¨ : λ°μ΄ν°λ² μ΄μ€ μλ¬")
        void μμ _μ€ν¨_DB_μλ¬() throws Exception {

            CommentModifyRequest request = CommentModifyRequest.builder().comment("λκΈ").build();

            when(commentService.modifyComment(anyLong(),anyLong(), anyString(), anyString()))
                    .thenThrow(new PostException(ErrorCode.DATABASE_ERROR));

            String url = "/api/v1/posts/1/comments/1";
            mockMvc.perform(put(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                    .andDo(print())
                    .andExpect(status().is(ErrorCode.DATABASE_ERROR.getStatus().value()));

            verify(commentService,atLeastOnce()).modifyComment(anyLong(),anyLong(),anyString(),anyString());
        }


        @Test
        @WithMockUser
        @DisplayName("μμ  μ€ν¨ : μμ±μ λΆμΌμΉ")
        void μμ _μ€ν¨_μμ±μ_λΆμΌμΉ() throws Exception {

            CommentModifyRequest request = CommentModifyRequest.builder().comment("λκΈ").build();

            when(commentService.modifyComment(anyLong(), anyLong(),anyString(), anyString()))
                    .thenThrow(new UserException(ErrorCode.INVALID_PERMISSION));

            String url = "/api/v1/posts/1/comments/1";
            mockMvc.perform(put(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
                    .andDo(print())
                    .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getStatus().value()));

            verify(commentService,atLeastOnce()).modifyComment(anyLong(),anyLong(),anyString(),anyString());
        }

    }


    @Nested
    @DisplayName("λκΈ μ­μ ")
    class CommentDelete{

        @Test
        @WithMockUser
        @DisplayName("λκΈ μ­μ  μ±κ³΅")
        void λκΈ_μ­μ _μ±κ³΅() throws Exception {
            String url = "/api/v1/posts/1/comments/1";
            mockMvc.perform(delete(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.message").value("λκΈ μ­μ  μλ£"))
                    .andExpect(jsonPath("$.result.id").value(1l));

            verify(commentService,times(1)).deleteComment(anyLong(),anyLong(),anyString());
        }


        @Test
        @WithAnonymousUser
        @DisplayName("λκΈ μ­μ  μ€ν¨ : μΈμ¦ μ€ν¨")
        void λκΈ_μ­μ _μ€ν¨1() throws Exception {



            String url = "/api/v1/posts/1/comments/1";
            mockMvc.perform(delete(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getStatus().value()));

            verify(commentService,never()).deleteComment(anyLong(),anyLong(),anyString());
        }

        @Test
        @WithMockUser
        @DisplayName("λκΈ μ­μ  μ€ν¨ : ν¬μ€νΈ μμ")
        void λκΈ_μ­μ _μ€ν¨2() throws Exception {
            /**commentServiceμ λ©μλ λ¦¬ν΄ νμμ΄ voidμ¬μ doThrow λ°©μ μ¬μ©**/
            doThrow(new PostException(ErrorCode.POST_NOT_FOUND))
                    .when(commentService).deleteComment(anyLong(),anyLong(),anyString());

            String url = "/api/v1/posts/1/comments/1";
            mockMvc.perform(delete(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().is(ErrorCode.POST_NOT_FOUND.getStatus().value()));

            verify(commentService,times(1)).deleteComment(anyLong(),anyLong(),anyString());
        }

        @Test
        @WithMockUser
        @DisplayName("λκΈ μ­μ  μ€ν¨ : μμ±μ λΆμΌμΉ")
        void λκΈ_μ­μ _μ€ν¨3() throws Exception {
            /**commentServiceμ λ©μλ λ¦¬ν΄ νμμ΄ voidμ¬μ doThrow λ°©μ μ¬μ©**/
            doThrow(new UserException(ErrorCode.INVALID_PERMISSION))
                    .when(commentService).deleteComment(anyLong(),anyLong(),anyString());

            String url = "/api/v1/posts/1/comments/1";
            mockMvc.perform(delete(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getStatus().value()));

            verify(commentService,times(1)).deleteComment(anyLong(),anyLong(),anyString());
        }


        @Test
        @WithMockUser
        @DisplayName("λκΈ μ­μ  μ€ν¨ : DBμλ¬")
        void λκΈ_μ­μ _μ€ν¨4() throws Exception {
            /**commentServiceμ λ©μλ λ¦¬ν΄ νμμ΄ voidμ¬μ doThrow λ°©μ μ¬μ©**/
            doThrow(new UserException(ErrorCode.DATABASE_ERROR))
                    .when(commentService).deleteComment(anyLong(),anyLong(),anyString());

            String url = "/api/v1/posts/1/comments/1";
            mockMvc.perform(delete(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().is(ErrorCode.DATABASE_ERROR.getStatus().value()));

            verify(commentService,times(1)).deleteComment(anyLong(),anyLong(),anyString());
        }
    }


}