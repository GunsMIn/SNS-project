package com.example.crudpersional.service;
import com.example.crudpersional.domain.dto.post.PostAddRequest;
import com.example.crudpersional.domain.dto.post.PostAddResponse;
import com.example.crudpersional.domain.dto.post.PostSelectResponse;
import com.example.crudpersional.domain.dto.post.PostUpdateRequest;
import com.example.crudpersional.domain.entity.Post;
import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.domain.entity.UserRole;
import com.example.crudpersional.exceptionManager.ErrorCode;
import com.example.crudpersional.exceptionManager.PostException;
import com.example.crudpersional.exceptionManager.UserException;
import com.example.crudpersional.repository.LikeRepository;
import com.example.crudpersional.repository.PostRepository;
import com.example.crudpersional.repository.UserRepository;
import lombok.Data;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Assertions;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class PostServiceTest {


    PostService postService;

    PostRepository postRepository = Mockito.mock(PostRepository.class);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    LikeRepository likeRepository = Mockito.mock(LikeRepository.class);


    @BeforeEach
    void setUp() {
        postService = new PostService(postRepository, userRepository,likeRepository);
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
            p.setPostId(1L);
            p.setUserId(1L);
            p.setUserName("test");
            p.setPassword("1234");
            p.setTitle("테스트 제목");
            p.setBody("테스트 내용");
            return p;
        }
    }

    @Data
    public static class PostTestEntity{
        public static Post get(String userName, String password) {
            Post postEntity = Post.builder()
                    .id(1L)
                    .user(UserTestEntity.get(userName, password))
                    .title("테스트 제목")
                    .body("테스트 내용")
                    .build();
            return postEntity;
        }
    }

    @Data
    public static class UserTestEntity {
        public static User get(String userName, String password) {
            User entity = new User();
            entity.setId(1L);
            entity.setUserName(userName);
            entity.setPassword(password);
            entity.setRole(UserRole.USER);
            return entity;
        }
    }


    @Test
    @DisplayName("조회 성공")
    void 글_조회_단건() {
        PostAndUser postAndUser = new PostAndUser();
        PostAndUser dto = postAndUser.getDto();

        User userEntity = new User();
        userEntity.setUserName(dto.getUserName());
        userEntity.setPassword(dto.getPassword());

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

    @Test
    @DisplayName("글 등록 성공 테스트")
    void 등록성공() {
        PostAndUser postAndUser = new PostAndUser();
        PostAndUser postAndUserDto = postAndUser.getDto();

        //목 객체 생성
        Post mockPost = mock(Post.class);
        User mockUser = mock(User.class);

       // when(userRepository.save(user)).thenReturn(Optional.of(User));
        //user 확인
        when(userRepository.findOptionalByUserName(any()))
                .thenReturn(Optional.of(mockUser));
       //post save 확인
        when(postRepository.save(any()))
                .thenReturn(mockPost);

        PostAddRequest postAddRequest = new PostAddRequest(postAndUser.getTitle(), postAndUser.getBody());

        PostAddResponse postAddResponse = postService.addPost(postAddRequest, postAndUser.getUserName());
        //Assertions.assertEquals(postAddResponse.getPostId(),0l);
        Assertions.assertDoesNotThrow(() -> postService.addPost(postAddRequest, postAndUser.getUserName()));
    }


    //포스트생성시 유저가 존재하지 않을 때 에러
    @Test
    @WithMockUser
    @DisplayName("등록 실패 : 유저 존재하지 않음")
    void 등록실패() {
        PostAndUser postAndUser = new PostAndUser();
        PostAndUser postAndUserDto = postAndUser.getDto();

        PostAddRequest postAddRequest = new PostAddRequest(postAndUser.getTitle(), postAndUser.getBody());


        when(userRepository.findOptionalByUserName(postAndUserDto.getUserName())).thenReturn(Optional.empty());
        when(postRepository.save(any())).thenReturn(mock(Post.class));

        UserException exception = Assertions.assertThrows(UserException.class, () -> postService.addPost(postAddRequest, postAndUser.getUserName()));
        assertEquals(ErrorCode.USERNAME_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("수정 실패 : 작성자!=유저")
    void 수정_실패() {
        PostAndUser postAndUser = new PostAndUser();
        PostAndUser postAndUserDto = postAndUser.getDto();
        User user1 = UserTestEntity.get("user", "password");
        User user2 = UserTestEntity.get("user2", "password2");

        //when(postRepository.findById(fixture.getPostId())).thenReturn(Optional.of(mockPostEntity));
        when(postRepository.findById(postAndUserDto.getPostId())).thenReturn(Optional.of(Post.of("title","body",user1)));
        when(userRepository.findOptionalByUserName(user1.getUserName())).thenReturn(Optional.of(User.of(user2.getUserName(), user2.getPassword())));

        PostUpdateRequest postUpdateRequest = new PostUpdateRequest(postAndUserDto.getTitle(), postAndUserDto.getBody());

        UserException exception = Assertions.assertThrows(UserException.class, ()
                -> postService.updatePost(postAndUserDto.getPostId(), postUpdateRequest ,postAndUserDto.getUserName()));

        assertEquals(ErrorCode.INVALID_PERMISSION, exception.getErrorCode());
    }

}