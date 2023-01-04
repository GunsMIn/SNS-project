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
    @DisplayName("조회")
    class PostList {
        @Test
        @WithMockUser   // 인증된 상태
        @DisplayName("post 단건 조회 성공")
        void post_단건_조회_성공() throws Exception {
            //조회 시 응답
            PostSelectResponse postEntity = PostSelectResponse.builder()
                    .id(1L)
                    .title("테스트 제목")
                    .body("테스트 내용")
                    .userName("김건우")
                    .createdAt(String.valueOf(LocalDateTime.now()))
                    .lastModifiedAt(String.valueOf(LocalDateTime.now()))
                    .build();

            //Service의 조회 메서드 사용시 post entity 반환
            when(postService.getPost(any()))
                    .thenReturn(postEntity);

            String url = "/api/v1/posts/1";
            mockMvc.perform(get(url)
                    .with(csrf()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.id").value(1L))
                    .andExpect(jsonPath("$.result.title").exists())
                    .andExpect(jsonPath("$.result.title").value("테스트 제목"))
                    .andExpect(jsonPath("$.result.body").value("테스트 내용"))
                    .andExpect(jsonPath("$.result.userName").value("김건우"))
                    .andExpect(jsonPath("$.result.createdAt").exists())
                    .andExpect(jsonPath("$.result.lastModifiedAt").exists());

            verify(postService, atLeastOnce()).getPost(any());
        }

        @Test
        @DisplayName("최신순 정렬 페이징 조회")
        @WithMockUser
        public void 페이징_테스트() throws Exception {

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
    @DisplayName("작성")
    class PostInsert {
        @Test
        @WithMockUser   // 인증된 상태
        @DisplayName("포스트 작성 성공")
        void post_success() throws Exception {
            /**given**/
            //포스트 작성 시 필요한 dto
            PostAddRequest postRequest = PostAddRequest.builder()
                    .title("테스트 제목")
                    .body("테스트 내용")
                    .build();
            //예상 응답값
            PostAddResponse postAddResponse = PostAddResponse.builder()
                    .postId(1L)
                    .message("포스트 등록 성공")
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
                    .andExpect(jsonPath("$.result.message").value("포스트 등록 성공"))
                    .andExpect(jsonPath("$.result.postId").exists())
                    .andExpect(jsonPath("$.result.postId").value(1L));
            verify(postService, atLeastOnce()).addPost(any(), any());
        }

        @Test
        @WithAnonymousUser // 인증 된지 않은 상태
        @DisplayName("포스트 작성 실패 : 권한 인증 없음 ")
        void 작성실패() throws Exception {
            /**given**/
            PostAddRequest postRequest = PostAddRequest.builder()
                    .title("테스트 제목")
                    .body("테스트 제목")
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
    @DisplayName("수정")
    class PostUpdate {
        @Test
        @WithMockUser   // 인증된 상태
        @DisplayName("포스트 수정 성공")
        void 수정_성공() throws Exception {
            /**given**/
            //요청 dto
            PostUpdateRequest modifyRequest = PostUpdateRequest.builder()
                    .title("테스트 제목")
                    .body("테스트 제목")
                    .build();
            //응답 객체
            Post postEntity = Post.builder()
                    .id(1L)
                    .build();
            PostUpdateResponse postUpdateResponse = new PostUpdateResponse("수정성공", postEntity.getId());

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
                    .andExpect(jsonPath("$.result.message").value("수정성공"))
                    .andExpect(jsonPath("$.result.postId").exists())
                    .andExpect(jsonPath("$.result.postId").value(1L))
                    .andExpect(status().isOk());
            verify(postService, atLeastOnce()).updatePost(any(), any(), any());
        }

        @Test
        @WithAnonymousUser // 인증 되지 않은 상태
        @DisplayName("포스트 수정 실패 : 인증 실패")
        void 수정_실패() throws Exception {
            /**given**/
            PostUpdateRequest modifyRequest = PostUpdateRequest.builder()
                    .title("테스트 제목")
                    .body("테스트 제목")
                    .build();
            /**when**/
            //INVALID_PERMISSION에러가 나타나는 상황이 주어질 것
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
        @DisplayName("포스트 수정 실패 : 작성자 불일치")
        void 수정실패_작성자불일치() throws Exception {
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
        @DisplayName("포스트 수정 실패 : 포스트 없음")
        void 수정_실패_포스트X() throws Exception {
            /**given**/
            PostUpdateRequest postUpdateRequest = PostUpdateRequest
                    .builder().title("제목").body("내용")
                    .build();
            /**when**/
            when(postService.updatePost(any(), any(), any()))
                    .thenThrow(new PostException(ErrorCode.POST_NOT_FOUND,"포스트 없음"));
            /**then**/
            String url = "/api/v1/posts/1";
            mockMvc.perform(put(url)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(postUpdateRequest)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.resultCode").value("ERROR"))
                    .andExpect(jsonPath("$.result.errorCode").value("POST_NOT_FOUND"))
                    .andExpect(jsonPath("$.result.message").value("포스트 없음")).andDo(print());
            verify(postService, times(1)).updatePost(any(), any(), any());
        }

        @Test
        @DisplayName("포스트 수정 실패(4) : 데이터베이스 에러")
        @WithMockUser
        public void post_update_fail4() throws Exception {
            /**given**/
            PostUpdateRequest postUpdateRequest = PostUpdateRequest
                    .builder().title("제목").body("내용")
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
    @DisplayName("삭제")
    class PostDelete{

        @Test
        @WithMockUser
        @DisplayName("포스트 삭제 성공")
        void 삭제_성공() throws Exception {

            PostDeleteResponse response = PostDeleteResponse.builder()
                    .postId(1L)
                    .message("삭제")
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
    @DisplayName("포스트 삭제 실패 : 인증 실패")
    void 삭제실패() throws Exception {

        when(postService.deletePost(any(), any()))
                .thenThrow(new PostException(ErrorCode.INVALID_PERMISSION, ""));

        mockMvc.perform(delete("/api/v1/posts/1L")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }



    @Test
    @WithMockUser   // 인증된 상태
    @DisplayName("포스트 삭제 실패 : 작성자 불일치")
    void 삭제실패_작성자불일치() throws Exception {

        when(postService.deletePost(any(), any()))
                .thenThrow(new PostException(ErrorCode.INVALID_PERMISSION, ""));

        mockMvc.perform(delete("/api/v1/posts/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getStatus().value()));
    }

    @Test
    @WithMockUser   // 인증된 상태
    @DisplayName("데이터베이스 에러")
    void 데이터베이스() throws Exception {

        when(postService.deletePost(any(), any()))
                .thenThrow(new PostException(ErrorCode.DATABASE_ERROR, "데이터 베이스 에러"));

        mockMvc.perform(delete("/api/v1/posts/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(ErrorCode.DATABASE_ERROR.getStatus().value()));
    }
    }

    @Nested
    @DisplayName("마이피드 보기 성공/실패")
    class MyPeed{


    @Test
    @DisplayName("마이피드 성공")
    @WithMockUser
    void 마이피드_성공 () throws Exception {

        when(postService.getMyPost(any(), any())).thenReturn(Page.empty());
        mockMvc.perform(get("/api/v1/posts/my")
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print())
                .andExpect(status().isOk());
    }

        @Test
        @DisplayName("마이피드 성공")
        @WithAnonymousUser
        void 마이피드_실패_로그인X () throws Exception {

            when(postService.getMyPost(any(), any())).thenThrow(new UserException(ErrorCode.INVALID_PERMISSION));
            mockMvc.perform(get("/api/v1/posts/my")
                    .contentType(MediaType.APPLICATION_JSON)
            ).andDo(print())
                    .andExpect(status().isUnauthorized());
        }


    }


    @Nested
    @DisplayName("알림 조회 성공/실패")
    class AlarmTest{


    @Test
    @WithMockUser
    @DisplayName("알람 조회 페이징 리스트")
    void 알람_페이징_조회_성공() throws Exception {

        PageRequest request = PageRequest.of(0, 20, Sort.Direction.DESC, "registeredAt");
        String url = "/api/v1/posts/alarms";

        mockMvc.perform(get(url)
                .param("size", "20")
                .param("sort", "registeredAt")
                .param("direction", "Sort.Direction.DESC"))
                .andExpect(status().isOk());


        assertThat(request.getPageNumber()).isEqualTo(0);
        assertThat(request.getPageSize()).isEqualTo(20);
        assertThat(request.getSort()).isEqualTo(Sort.by("registeredAt").descending());
        verify(postService, times(1)).getAlarms(any());
    }



        @Test
        @WithAnonymousUser
        @DisplayName("인증 실패 - 알람 조회 페이징 리스트")
        void 알람_페이징_조회_실패() throws Exception {

            PageRequest request = PageRequest.of(0, 20, Sort.Direction.DESC, "registeredAt");
            String url = "/api/v1/posts/alarms";

            mockMvc.perform(get(url)
                    .param("size", "20")
                    .param("sort", "registeredAt")
                    .param("direction", "Sort.Direction.DESC"))
                    .andExpect(status().isUnauthorized());

            verify(postService, never()).getAlarms(any());
        }

    }
}