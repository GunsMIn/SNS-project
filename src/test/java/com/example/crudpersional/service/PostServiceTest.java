package com.example.crudpersional.service;
import com.example.crudpersional.domain.dto.post.*;
import com.example.crudpersional.domain.entity.Post;
import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.exceptionManager.ErrorCode;
import com.example.crudpersional.exceptionManager.PostException;
import com.example.crudpersional.exceptionManager.UserException;
import com.example.crudpersional.fixture.AllFixture;
import com.example.crudpersional.fixture.PostEntityFixture;
import com.example.crudpersional.fixture.UserEntityFixture;
import com.example.crudpersional.repository.LikeRepository;
import com.example.crudpersional.repository.PostRepository;
import com.example.crudpersional.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.test.context.support.WithMockUser;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PostServiceTest {


    @InjectMocks
    PostService postService;

    @Mock
    PostRepository postRepository ;
    @Mock
    UserRepository userRepository ;
    @Mock
    LikeRepository likeRepository;

    @Test
    @DisplayName("조회 성공")
    void 글_조회_단건() {
        //postAndUser의 객체
        AllFixture allFixture = new AllFixture();
        AllFixture dto = allFixture.getDto();

        //User 픽스처로 생성된 user엔티티(매개변수는 postAndUser픽스처의 값)
        User user = UserEntityFixture.get(dto.getUserName(), dto.getPassword());
        //Post 픽스처로 생성된 user엔티티(매개변수는 User픽스처의 값)
        Post post = PostEntityFixture.get(user);

        //postRepository에서 조회 메서드를 사용하면 post엔티티를 반환한다는 가정
        when(postRepository.findById(dto.getPostId())).thenReturn(Optional.of(post));
        //postService에서 포스트 단건조회
        PostSelectResponse postSelectResponse = postService.getPost(dto.getPostId());
        //픽스처의 값과 service 반환 값의 테스트
        assertEquals(dto.getUserName(), postSelectResponse.getUserName());
    }

    @Nested
    @DisplayName("포스트 등록")
    class PostAdd {
        @Test
        @DisplayName("글 등록 성공 테스트")
        void 등록성공() {

            AllFixture dto = AllFixture.getDto();
            //User 픽스처로 생성된 user엔티티(매개변수는 postAndUser픽스처의 값)
            User user = UserEntityFixture.get(dto.getUserName(), dto.getPassword());
            //Post 픽스처로 생성된 user엔티티(매개변수는 User픽스처의 값)
            Post post = PostEntityFixture.get(user);
            //postservice의 작성에서 우선 회원을 찾을 것이다. userRepository.findOptionalByUserName
            when(userRepository.findOptionalByUserName(any())).thenReturn(Optional.of(user));
            //그 후 postRepository.save()를 적용
            when(postRepository.save(any())).thenReturn(post);
            //postService에 들어갈 변수
            PostAddRequest postAddRequest = new PostAddRequest(dto.getTitle(), dto.getBody());

            PostAddResponse postAddResponse = postService.addPost(postAddRequest, dto.getUserName());

            Assertions.assertEquals(postAddResponse.getPostId(), dto.getPostId());
            //assertDoesNotThrow() 이상이 없다면 통과
            Assertions.assertDoesNotThrow(() -> postAddResponse);
        }


        //포스트생성시 유저가 존재하지 않을 때 에러
        @Test
        @WithMockUser
        @DisplayName("등록 실패 : 유저 존재 하지 않음")
        void 등록_실패() {
            AllFixture dto = AllFixture.getDto();
            User user = UserEntityFixture.get(dto.getUserName(), dto.getPassword());
            Post post = PostEntityFixture.get(user);
            // 유저 존재 하지 않은 상황 가정
            when(userRepository.findOptionalByUserName(any())).thenReturn(Optional.empty());
            when(postRepository.save(any())).thenReturn(mock(Post.class));

            PostAddRequest postAddRequest = new PostAddRequest(dto.getTitle(), dto.getBody());

            UserException userException
                    = assertThrows(UserException.class, () -> postService.addPost(postAddRequest, dto.getUserName()));

            Assertions.assertEquals(userException.getErrorCode(), ErrorCode.USERNAME_NOT_FOUND);
            Assertions.assertEquals(userException.getErrorCode().getMessage(), ErrorCode.USERNAME_NOT_FOUND.getMessage());

        }
    }

    @Nested
    @DisplayName("포스트 수정")
    class PostUpdate{

    @Test
    @DisplayName("수정 실패 : 작성자!=유저")
    void 수정_실패_작성자_불일치() {

        AllFixture dto = AllFixture.getDto();
        User user1 = UserEntityFixture.get("박지성", "password");
        User user2 = UserEntityFixture.get("손흥민", "password2");
        //user2가 작성한 post
        Post postByUser2 = PostEntityFixture.get(user2);
        //수정 request
        PostUpdateRequest postUpdateRequest = new PostUpdateRequest("수정될 제목", "수정될 내용");
        // post를 작성한 user는 user1
        when(postRepository.findById(dto.getPostId()))
                .thenReturn(Optional.of(Post.of("title","body",user1)));
        // post를 수정 시도한 user는 user2
        when(userRepository.findOptionalByUserName(any()))
                .thenReturn(Optional.of(User.of(user2.getUsername(), user2.getPassword())));

        UserException userException = assertThrows(UserException.class,
                () -> postService.updatePost(dto.getPostId(), postUpdateRequest, dto.getUserName()));

        //ErrorCode 검사
        Assertions.assertEquals(userException.getErrorCode(),ErrorCode.INVALID_PERMISSION);


    }

    @Test
    @DisplayName("수정 실패 : 포스트 존재하지 않음")
    @WithMockUser
    void 수정_실패_포스트_존재하지않음() {
        AllFixture allFixture = AllFixture.getDto();
        //수정 request
        PostUpdateRequest postUpdateRequest = new PostUpdateRequest("수정될 제목", "수정될 내용");
        //해당 post존재하지 않음 Optional.of(mock(Post.class)) <- 이것을 넣으면 에러가 발생하면 안됨
        when(postRepository.findById(any())).thenReturn(Optional.empty());
        when(userRepository.findOptionalByUserName(any())).thenReturn(Optional.of(mock(User.class)));

        PostException postException =
                assertThrows(PostException.class,
                        () -> postService.updatePost(allFixture.getPostId(), postUpdateRequest, allFixture.getUserName()));

        Assertions.assertEquals(postException.getErrorCode(),ErrorCode.POST_NOT_FOUND);
    }

        @Test
        @DisplayName("수정 실패 : 회원 존재하지 않음")
        @WithMockUser
        void 수정_실패_회원_존재하지않음() {
            AllFixture allFixture = AllFixture.getDto();
            //수정 request
            PostUpdateRequest postUpdateRequest = new PostUpdateRequest("수정될 제목", "수정될 내용");

            //해당 user존재 하지않는 상황가정
            when(postRepository.findById(any())).thenReturn(Optional.of(mock(Post.class)));
            when(userRepository.findOptionalByUserName(any())).thenReturn(Optional.empty());

            UserException userException =
                    assertThrows(UserException.class,
                            () -> postService.updatePost(allFixture.getPostId(), postUpdateRequest, allFixture.getUserName()));

            Assertions.assertEquals(userException.getErrorCode(),ErrorCode.USERNAME_NOT_FOUND);
        }


        @Test
        @WithMockUser
        @DisplayName("포스트 수정 성공")
        void postUpdateSuccess() {
            AllFixture allFixture = AllFixture.getDto();
            PostUpdateRequest postUpdateRequest = new PostUpdateRequest("수정될 제목", "수정될 내용");
            User user = UserEntityFixture.get(allFixture.getUserName(), allFixture.getPassword());

            //수정 성공 로직
            when(userRepository.findOptionalByUserName(user.getUsername())).thenReturn(Optional.of(user));
            when(postRepository.findById(any())).thenReturn(Optional.of(PostEntityFixture.get(user)));

            PostUpdateResponse postUpdateResponse
                    = postService.updatePost(allFixture.getPostId(), postUpdateRequest, allFixture.getUserName());

            Assertions.assertEquals(postUpdateResponse.getMessage(),"포스트 수정 완료");

        }
    }

    @Nested
    @DisplayName("포스트 삭제")
    class PostDelete{

        @Test
        @WithMockUser
        @DisplayName("포스트 삭제 성공")
        void 포스트_삭제_성공() {
            AllFixture allFixture = AllFixture.getDto();
            User user = UserEntityFixture.get(allFixture.getUserName(), allFixture.getPassword());

            //수정 성공 로직
            when(userRepository.findOptionalByUserName(user.getUsername())).thenReturn(Optional.of(user));
            when(postRepository.findById(any())).thenReturn(Optional.of(PostEntityFixture.get(user)));

            PostDeleteResponse postDeleteResponse
                    = postService.deletePost(allFixture.getPostId(), allFixture.getUserName());

            Assertions.assertEquals(postDeleteResponse.getMessage(),"포스트 삭제 완료");
            Assertions.assertEquals(postDeleteResponse.getPostId(),1L);

        }


        @Test
        @DisplayName("포스트 삭제 작성자 불일치")
        void 삭제_실패_작성자_불일치() throws Exception {

            AllFixture dto = AllFixture.getDto();
            //유저1 (글 작성한 회원)
            User 유저1 = UserEntityFixture.get("유저1", "1234");
            //유저2 (글 삭제 시도 회원)
            User 유저2 = new User(2l, "유저2", "1234");

            when(postRepository.findById(any())).thenReturn(Optional.of(PostEntityFixture.get(유저1)));
            when(userRepository.findOptionalByUserName(any())).thenReturn(Optional.of(유저2));

            UserException userException =
                    assertThrows(UserException.class, () -> postService.deletePost(dto.getPostId(), dto.getUserName()));
            //에러 코드 확인
            Assertions.assertEquals(userException.getErrorCode(),ErrorCode.INVALID_PERMISSION);
        }

        @Test
        @WithMockUser
        @DisplayName("삭제 실패 : 포스트 존재 하지 않음")
        void 삭제_실패_포스트_존재x() throws Exception {
            AllFixture dto = AllFixture.getDto();
            //포스트 존재하지 않는 상황 가정
            when(postRepository.findById(any())).thenReturn(Optional.empty());

            PostException postException =
                    assertThrows(PostException.class, () -> postService.deletePost(dto.getPostId(), dto.getUserName()));

            Assertions.assertEquals(postException.getErrorCode(),ErrorCode.POST_NOT_FOUND);
            Assertions.assertEquals(postException.getErrorCode().getMessage(),"해당 포스트가 없습니다.");
        }
    }
    }