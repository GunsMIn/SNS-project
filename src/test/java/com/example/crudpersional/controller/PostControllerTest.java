package com.example.crudpersional.controller;

import com.example.crudpersional.exceptionManager.UserException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
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

@WebMvcTest(PostController.class)
public class PostControllerTest {
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PostService postService;

    @MockBean
    EncrypterConfig encoderConfig;

    @Autowired
    ObjectMapper objectMapper;

    @Nested
    @DisplayName("??????")
    class PostList {
        @Test
        @WithMockUser   // ????????? ??????
        @DisplayName("post ?????? ?????? ??????")
        void post_??????_??????_??????() throws Exception {
            //?????? ??? ??????
            PostSelectResponse postEntity = PostSelectResponse.builder()
                    .id(1L)
                    .title("????????? ??????")
                    .body("????????? ??????")
                    .userName("?????????")
                    .createdAt(String.valueOf(LocalDateTime.now()))
                    .lastModifiedAt(String.valueOf(LocalDateTime.now()))
                    .build();

            //Service??? ?????? ????????? ????????? post entity ??????
            when(postService.getPost(any()))
                    .thenReturn(postEntity);

            String url = "/api/v1/posts/1";
            mockMvc.perform(get(url)
                    .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.id").value(1L))
                    .andExpect(jsonPath("$.result.title").exists())
                    .andExpect(jsonPath("$.result.title").value("????????? ??????"))
                    .andExpect(jsonPath("$.result.body").value("????????? ??????"))
                    .andExpect(jsonPath("$.result.userName").value("?????????"))
                    .andExpect(jsonPath("$.result.createdAt").exists())
                    .andExpect(jsonPath("$.result.lastModifiedAt").exists());

            verify(postService, atLeastOnce()).getPost(any());
        }

        @Test
        @DisplayName("????????? ?????? ????????? ??????")
        @WithMockUser
        public void ?????????_?????????() throws Exception {

            PageRequest pageable = PageRequest.of(0, 5, Sort.Direction.DESC, "registeredAt");

            mockMvc.perform(get("/api/v1/posts")
                    .param("page", "0")
                    .param("size", "5")
                    .param("sort", "registeredAt")
                    .param("direction", "Sort.Direction.DESC"))
                    .andExpect(status().isOk());

            assertThat(pageable.getPageNumber()).isEqualTo(0);
            assertThat(pageable.getPageSize()).isEqualTo(5);
            assertThat(pageable.getSort()).isEqualTo(Sort.by("registeredAt").descending());
            verify(postService, atLeastOnce()).getPosts(any());
        }

    }


    @Nested
    @DisplayName("??????")
    class PostInsert {
        @Test
        @WithMockUser   // ????????? ??????
        @DisplayName("????????? ?????? ??????")
        void post_success() throws Exception {
            /**given**/
            //????????? ?????? ??? ????????? dto
            PostAddRequest postRequest = PostAddRequest.builder()
                    .title("????????? ??????")
                    .body("????????? ??????")
                    .build();
            //?????? ?????????
            PostAddResponse postAddResponse = PostAddResponse.builder()
                    .postId(1L)
                    .message("????????? ?????? ??????")
                    .build();

            /**when**/
            when(postService.addPost(any(), any())).thenReturn(postAddResponse);

            /**then**/
            String url = "/api/v1/posts";
            mockMvc.perform(post(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(postRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.message").exists())
                    .andExpect(jsonPath("$.result.message").value("????????? ?????? ??????"))
                    .andExpect(jsonPath("$.result.postId").exists())
                    .andExpect(jsonPath("$.result.postId").value(1L));
            verify(postService, atLeastOnce()).addPost(any(), any());
        }

        @Test
        @WithAnonymousUser // ?????? ?????? ?????? ??????
        @DisplayName("????????? ?????? ?????? : ?????? ?????? ?????? ")
        void ????????????() throws Exception {
            /**given**/
            PostAddRequest postRequest = PostAddRequest.builder()
                    .title("????????? ??????")
                    .body("????????? ??????")
                    .build();
            /**when**/
            when(postService.addPost(any(), any()))
                    .thenThrow(new PostException(ErrorCode.INVALID_PERMISSION, ""));
            /**then**/
            mockMvc.perform(post("/api/v1/posts")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(postRequest)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
            verify(postService, never()).addPost(any(),any());
        }
    }

    @Nested
    @DisplayName("??????")
    class PostUpdate {
        @Test
        @WithMockUser   // ????????? ??????
        @DisplayName("????????? ?????? ??????")
        void ??????_??????() throws Exception {
            /**given**/
            //?????? dto
            PostUpdateRequest modifyRequest = PostUpdateRequest.builder()
                    .title("????????? ??????")
                    .body("????????? ??????")
                    .build();
            //?????? ??????
            Post postEntity = Post.builder()
                    .id(1L)
                    .build();
            PostUpdateResponse postUpdateResponse = new PostUpdateResponse("????????????", postEntity.getId());

            /**when**/
            when(postService.updatePost(any(), any(), any()))
                    .thenReturn(postUpdateResponse);

            /**then**/
            mockMvc.perform(put("/api/v1/posts/1")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(modifyRequest)))
                    .andDo(print())
                    .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                    .andExpect(jsonPath("$.result.message").exists())
                    .andExpect(jsonPath("$.result.message").value("????????????"))
                    .andExpect(jsonPath("$.result.postId").exists())
                    .andExpect(jsonPath("$.result.postId").value(1L))
                    .andExpect(status().isOk());
            verify(postService, atLeastOnce()).updatePost(any(), any(), any());
        }

        @Test
        @WithAnonymousUser // ?????? ?????? ?????? ??????
        @DisplayName("????????? ?????? ?????? : ?????? ??????")
        void ??????_??????() throws Exception {
            /**given**/
            PostUpdateRequest modifyRequest = PostUpdateRequest.builder()
                    .title("????????? ??????")
                    .body("????????? ??????")
                    .build();
            /**when**/
            //INVALID_PERMISSION????????? ???????????? ????????? ????????? ???
            when(postService.updatePost(any(), any(), any()))
                    .thenThrow(new PostException(ErrorCode.INVALID_PERMISSION, ""));
            /**then**/
            //putMappin url
            String url = "/api/v1/posts/1";
            mockMvc.perform(put(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(modifyRequest)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
            verify(postService, never()).updatePost(any(), any(), any());
        }


        @Test
        @WithMockUser
        @DisplayName("????????? ?????? ?????? : ????????? ?????????")
        void ????????????_??????????????????() throws Exception {
            /**given**/
            PostUpdateRequest modifyRequest = PostUpdateRequest.builder()
                    .title("title_modify")
                    .body("body_modify")
                    .build();
            /**when**/
            when(postService.updatePost(any(), any(), any()))
                    .thenThrow(new PostException(ErrorCode.INVALID_PERMISSION));
            /**then**/
            mockMvc.perform(put("/api/v1/posts/1")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(modifyRequest)))
                    .andDo(print())
                    .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getStatus().value()));

            //verify(postService, never()).updatePost(any(), any(), any());
        }

        @Test
        @WithMockUser
        @DisplayName("????????? ?????? ?????? : ????????? ??????")
        void ??????_??????_?????????X() throws Exception {
            /**given**/
            PostUpdateRequest postUpdateRequest = PostUpdateRequest
                    .builder().title("??????").body("??????")
                    .build();
            /**when**/
            when(postService.updatePost(any(), any(), any()))
                    .thenThrow(new PostException(ErrorCode.POST_NOT_FOUND,"????????? ??????"));
            /**then**/
            String url = "/api/v1/posts/1";
            mockMvc.perform(put(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(postUpdateRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("POST_NOT_FOUND"))
                    .andExpect(jsonPath("$.result.message").value("????????? ??????")).andDo(print());
            verify(postService, times(1)).updatePost(any(), any(), any());
        }

        @Test
        @DisplayName("????????? ?????? ??????(4) : ?????????????????? ??????")
        @WithMockUser
        public void post_update_fail4() throws Exception {
            /**given**/
            PostUpdateRequest postUpdateRequest = PostUpdateRequest
                    .builder().title("??????").body("??????")
                    .build();
            /**when**/
            when(postService.updatePost(any(), any(), any())).thenThrow(new PostException(ErrorCode.DATABASE_ERROR));
            /**then**/
            mockMvc.perform(put("/api/v1/posts/1")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(postUpdateRequest)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("DATABASE_ERROR"))
                    .andDo(print());

            verify(postService, times(1)).updatePost(any(), any(), any());
        }

    }




    @Nested
    @DisplayName("??????")
    class PostDelete{

        @Test
        @WithMockUser
        @DisplayName("????????? ?????? ??????")
        void ??????_??????() throws Exception {

            PostDeleteResponse response = PostDeleteResponse.builder()
                    .postId(1L)
                    .message("??????")
                    .build();
            when(postService.deletePost(anyLong(), anyString())).thenReturn(response);

            mockMvc.perform(delete("/api/v1/posts/1")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(jsonPath("$.result.message").exists())
                    .andExpect(jsonPath("$.result.postId").exists())
                    .andExpect(status().isOk());
        }


    @Test
    @WithAnonymousUser
    @DisplayName("????????? ?????? ?????? : ?????? ??????")
    void ????????????() throws Exception {

        when(postService.deletePost(any(), any()))
                .thenThrow(new PostException(ErrorCode.INVALID_PERMISSION, ""));

        mockMvc.perform(delete("/api/v1/posts/1L")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }



    @Test
    @WithMockUser   // ????????? ??????
    @DisplayName("????????? ?????? ?????? : ????????? ?????????")
    void ????????????_??????????????????() throws Exception {

        when(postService.deletePost(any(), any()))
                .thenThrow(new PostException(ErrorCode.INVALID_PERMISSION, ""));

        mockMvc.perform(delete("/api/v1/posts/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getStatus().value()));
    }

    @Test
    @WithMockUser   // ????????? ??????
    @DisplayName("?????????????????? ??????")
    void ??????????????????() throws Exception {

        when(postService.deletePost(any(), any()))
                .thenThrow(new PostException(ErrorCode.DATABASE_ERROR, "????????? ????????? ??????"));

        mockMvc.perform(delete("/api/v1/posts/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(ErrorCode.DATABASE_ERROR.getStatus().value()));
    }
    }

    @Nested
    @DisplayName("???????????? ?????? ??????/??????")
    class MyPeed{


    @Test
    @DisplayName("???????????? ??????")
    @WithMockUser
    void ????????????_?????? () throws Exception {

        when(postService.getMyPeed(any(), any())).thenReturn(Page.empty());
        mockMvc.perform(get("/api/v1/posts/my")
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk());
    }

        @Test
        @DisplayName("???????????? ??????")
        @WithAnonymousUser
        void ????????????_??????_?????????X () throws Exception {

            when(postService.getMyPeed(any(), any())).thenThrow(new UserException(ErrorCode.INVALID_PERMISSION));
            mockMvc.perform(get("/api/v1/posts/my")
                    .contentType(MediaType.APPLICATION_JSON)
            ).andDo(print())
                    .andExpect(status().isUnauthorized());
        }


    }


    @Nested
    @DisplayName("?????? ?????? ??????/??????")
    class AlarmTest{


    @Test
    @WithMockUser
    @DisplayName("?????? ?????? ????????? ?????????")
    void ??????_?????????_??????_??????() throws Exception {

        PageRequest request = PageRequest.of(0, 20, Sort.Direction.DESC, "registeredAt");
        String url = "/api/v1/alarms";

        mockMvc.perform(get(url)
                .param("size", "20")
                .param("sort", "registeredAt")
                .param("direction", "Sort.Direction.DESC"))
                .andExpect(status().isOk());


        assertThat(request.getPageNumber()).isEqualTo(0);
        assertThat(request.getPageSize()).isEqualTo(20);
        assertThat(request.getSort()).isEqualTo(Sort.by("registeredAt").descending());
        verify(postService, times(1)).getAlarms(any(),any());
    }



        @Test
        @WithAnonymousUser
        @DisplayName("?????? ?????? - ?????? ?????? ????????? ?????????")
        void ??????_?????????_??????_??????() throws Exception {

            PageRequest request = PageRequest.of(0, 20, Sort.Direction.DESC, "registeredAt");
            String url = "/api/posts/alarms";

            mockMvc.perform(get(url)
                    .param("size", "20")
                    .param("sort", "registeredAt")
                    .param("direction", "Sort.Direction.DESC"))
                    .andExpect(status().isUnauthorized());

            verify(postService, never()).getAlarms(any(),any());
        }

    }
}