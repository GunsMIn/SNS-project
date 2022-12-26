package com.example.crudpersional.service;

import com.example.crudpersional.domain.dto.post.PostSelectResponse;
import com.example.crudpersional.domain.entity.Post;
import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.repository.PostRepository;
import com.example.crudpersional.repository.UserRepository;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PostServiceTest {
    PostService postService;

    PostRepository postRepository = Mockito.mock(PostRepository.class);
    UserRepository userRepository = Mockito.mock(UserRepository.class);


    @BeforeEach
    void setUp() {
        postService = new PostService(postRepository, userRepository);
    }

    @Data
    public static class PostAndUser{
        private Long postId;
        private Long userId;
        private String userName;
        private String password;
        private String title;
        private String body;

        public PostAndUser getDto() {
            PostAndUser p = new PostAndUser();
            p.setPostId(100L);
            p.setUserId(100L);
            p.setUserName("KIMGUNWOO");
            p.setPassword("1234");
            p.setTitle("테스트 제목");
            p.setBody("테스트 내용");
            return p;
        }
    }

    @Test
    @DisplayName("조회 성공")
    void success_post_get() {
        PostAndUser postAndUser = new PostAndUser();
        PostAndUser dto = postAndUser.getDto();

        User userEntity = new User();
        userEntity.setUserName(dto.getUserName());

        Post postEntity = Post.builder()
                .id(100L)
                .user(userEntity)
                .title("테스트 제목")
                .body("테스트 내용")
                .build();


        when(postRepository.findById(dto.getPostId())).thenReturn(Optional.of(postEntity));

        PostSelectResponse postSelectResponse = postService.getPost(dto.getPostId());
        assertEquals(dto.getUserName(), postSelectResponse.getUserName());
    }

}