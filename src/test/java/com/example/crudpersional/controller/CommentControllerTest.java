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
            when(commentService.writeComment(any(), any(), any())).thenReturn(response);
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

            verify(commentService, atLeastOnce()).writeComment(any(), anyString(), anyString());

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
        @DisplayName("댓글 등록 실패 : 게시물 존재하지 않음")
        void 댓글_등록_실패_게시물_존재X() throws Exception {
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
        verify(commentService, times(1)).getComments(anyLong(),any());
    }


    @Nested
    @DisplayName("댓글 작성")
    class CommentUpdate {

        @Test
        @WithMockUser
        @DisplayName("수정 성공")
        void 수정_성공() throws Exception {

            CommentModifyRequest request = CommentModifyRequest.builder().comment("댓글").build();

            CommentUpdateResponse response = CommentUpdateResponse.builder()
                    .id(1l)
                    .postId(1L)
                    .comment("댓글")
                    .userName("김건우")
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
                    .andExpect(jsonPath("$.result.comment").value("댓글"))
                    .andExpect(jsonPath("$.result.userName").value("김건우"));

            verify(commentService, times(1)).modifyComment(anyLong(),anyLong(),anyString(),anyString());
        }

        @Test
        @WithAnonymousUser
        @DisplayName("수정 실패 : 인증 실패")
        void 수정_실패() throws Exception {

            CommentModifyRequest request = CommentModifyRequest.builder().comment("댓글").build();
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
        @DisplayName("수정 실패 : post가 없는 경우")
        void 수정_실패_post_없음() throws Exception {

            CommentModifyRequest request = CommentModifyRequest.builder().comment("댓글").build();

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
        @DisplayName("수정 실패 : 데이터베이스 에러")
        void 수정_실패_DB_에러() throws Exception {

            CommentModifyRequest request = CommentModifyRequest.builder().comment("댓글").build();

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
        @DisplayName("수정 실패 : 작성자 불일치")
        void 수정_실패_작성자_불일치() throws Exception {

            CommentModifyRequest request = CommentModifyRequest.builder().comment("댓글").build();

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
    @DisplayName("댓글 삭제")
    class CommentDelete{

        @Test
        @WithMockUser
        @DisplayName("댓글 삭제 성공")
        void 댓글_삭제_성공() throws Exception {
            String url = "/api/v1/posts/1/comments/1";
            mockMvc.perform(delete(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.message").value("댓글 삭제 완료"))
                    .andExpect(jsonPath("$.result.id").value(1l));

            verify(commentService,times(1)).deleteComment(anyLong(),anyLong(),anyString());
        }


        @Test
        @WithAnonymousUser
        @DisplayName("댓글 삭제 실패 : 인증 실패")
        void 댓글_삭제_실패1() throws Exception {



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
        @DisplayName("댓글 삭제 실패 : 포스트 없음")
        void 댓글_삭제_실패2() throws Exception {
            /**commentService의 메서드 리턴 타입이 void여서 doThrow 방식 사용**/
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
        @DisplayName("댓글 삭제 실패 : 작성자 불일치")
        void 댓글_삭제_실패3() throws Exception {
            /**commentService의 메서드 리턴 타입이 void여서 doThrow 방식 사용**/
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
        @DisplayName("댓글 삭제 실패 : DB에러")
        void 댓글_삭제_실패4() throws Exception {
            /**commentService의 메서드 리턴 타입이 void여서 doThrow 방식 사용**/
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