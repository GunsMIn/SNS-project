package com.example.crudpersional.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
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
    }




    @Test
    @WithMockUser   // 인증된 상태
    @DisplayName("포스트 작성 성공")
    void post_success() throws Exception {

        PostAddRequest postRequest = PostAddRequest.builder()
                .title("테스트 제목")
                .body("테스트 내용")
                .build();

        when(postService.addPost(any(), any()))
                .thenReturn(PostAddResponse.builder()
                        .postId(0L)
                        .build());

        mockMvc.perform(post("/api/v1/posts")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(postRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").exists())
                .andExpect(jsonPath("$.result.postId").exists())
        ;
    }

    @Test
    @WithAnonymousUser // 인증 된지 않은 상태
    @DisplayName("포스트 작성 실패 : 인증 실패")
    void 작성실패() throws Exception {

        PostAddRequest postRequest = PostAddRequest.builder()
                .title("테스트 제목")
                .body("테스트 제목")
                .build();

        when(postService.addPost(any(), any()))
                .thenThrow(new PostException(ErrorCode.INVALID_PERMISSION, ""));

        mockMvc.perform(post("/api/v1/posts")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(postRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser   // 인증된 상태
    @DisplayName("포스트 수정 성공")
    void 수정성공() throws Exception {

        PostUpdateRequest modifyRequest = PostUpdateRequest.builder()
                .title("테스트 제목")
                .body("테스트 제목")
                .build();

        Post postEntity = Post.builder()
                .id(1L)
                .build();
        PostUpdateResponse postUpdateResponse = new PostUpdateResponse("수정성공", postEntity.getId());

        when(postService.updatePost(any(), any(), any()))
                .thenReturn(postUpdateResponse);

        mockMvc.perform(put("/api/v1/posts/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(modifyRequest)))
                .andDo(print())
                .andExpect(jsonPath("$.result.message").exists())
                .andExpect(jsonPath("$.result.postId").exists())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser // 인증 되지 않은 상태
    @DisplayName("포스트 수정 실패 : 인증 실패")
    void 수정실패() throws Exception {

        PostUpdateRequest modifyRequest = PostUpdateRequest.builder()
                .title("테스트 제목")
                .body("테스트 제목")
                .build();

        when(postService.updatePost(any(), any(), any()))
                .thenThrow(new PostException(ErrorCode.INVALID_PERMISSION, ""));

        mockMvc.perform(put("/api/v1/posts/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(modifyRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }



    @Test
    @WithMockUser
    @DisplayName("포스트 수정 실패 : 작성자 불일치")
    void 수정실패_작성자불일치() throws Exception {

        PostUpdateRequest modifyRequest = PostUpdateRequest.builder()
                .title("title_modify")
                .body("body_modify")
                .build();

        when(postService.updatePost(any(), any(), any()))
                .thenThrow(new PostException(ErrorCode.INVALID_PERMISSION, ""));

        mockMvc.perform(put("/api/v1/posts/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(modifyRequest)))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getStatus().value()));
    }



    @Test
    @WithMockUser
    @DisplayName("포스트 삭제 성공")
    void 삭제성공() throws Exception {

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