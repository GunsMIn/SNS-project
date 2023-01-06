package com.example.crudpersional.service;
import com.example.crudpersional.domain.dto.comment.CommentResponse;
import com.example.crudpersional.domain.dto.comment.CommentUpdateResponse;
import com.example.crudpersional.domain.dto.post.LikeResponse;
import com.example.crudpersional.domain.entity.*;
import com.example.crudpersional.domain.entity.alarm.AlarmType;
import com.example.crudpersional.exceptionManager.ErrorCode;
import com.example.crudpersional.exceptionManager.LikeException;
import com.example.crudpersional.exceptionManager.PostException;
import com.example.crudpersional.exceptionManager.UserException;
import com.example.crudpersional.fixture.*;
import com.example.crudpersional.repository.LikeRepository;
import com.example.crudpersional.repository.PostRepository;
import com.example.crudpersional.repository.UserRepository;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.Optional;

import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithMockUser;
import com.example.crudpersional.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.example.crudpersional.repository.AlarmRepository;
import com.example.crudpersional.repository.CommentRepository;
import com.example.crudpersional.repository.PostRepository;
import com.example.crudpersional.repository.UserRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class LikeServiceTest {
    @InjectMocks
    PostService postService;
    @Mock
    PostRepository postRepository ;
    @Mock
    UserRepository userRepository ;
    @Mock
    CommentRepository commentRepository;
    @Mock
    AlarmRepository alarmRepository;
    @Mock
    LikeRepository likeRepository;

    @Test
    @DisplayName("좋아요 성공")
    void 좋아요_성공() throws Exception {
        AllFixture all = AllFixture.getDto();
        /**좋아요 누를 유저**/
        User doLikeUser = UserEntityFixture.get(all.getUserName(), all.getPassword());
        /**post 작성 유저**/
        User UserOfPost = User.builder().id(2l).userName("post작성유저").build();
        Post post = PostEntityFixture.get(UserOfPost);
        LikeEntity like = LikeFixture.get(doLikeUser, post);
        AlarmEntity alarm= AlarmFixture.get(post,doLikeUser,AlarmType.NEW_LIKE_ON_POST);


        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(userRepository.findOptionalByUserName(doLikeUser.getUsername())).thenReturn(Optional.of(doLikeUser));
        //Optional.of(like) 를 아래 thenReturn에 넣어주면 좋아요 이미 누른 에러 나옴
        when(likeRepository.findByUserAndPost(doLikeUser, post)).thenReturn(Optional.empty());
        when(likeRepository.save(any())).thenReturn(like);

        LikeResponse response = postService.likes(post.getId(), doLikeUser.getUsername());
        //assertj
        assertThat(response.getLikeId()).isEqualTo(like.getId());
        //jupiter
        assertDoesNotThrow(()-> response);
    }

    @Test
    @DisplayName("좋아요 실패1 : 좋아요 누를 post 없음")
    void 좋아요_실패_post없음() throws Exception {

        AllFixture all = AllFixture.getDto();
        /**좋아요 누를 유저**/
        User doLikeUser = UserEntityFixture.get(all.getUserName(), all.getPassword());
        /**post 작성 유저**/
        User UserOfPost = User.builder().id(2l).userName("post작성유저").build();
        Post post = PostEntityFixture.get(UserOfPost);
        LikeEntity like = LikeFixture.get(doLikeUser, post);
        AlarmEntity alarm= AlarmFixture.get(post,doLikeUser,AlarmType.NEW_LIKE_ON_POST);

        /**Post 없는 상황 가정🔽**/
        when(postRepository.findById(any())).thenReturn(Optional.empty());
        when(userRepository.findOptionalByUserName(doLikeUser.getUsername())).thenReturn(Optional.of(doLikeUser));
        //Optional.of(like) 를 아래 thenReturn에 넣어주면 좋아요 이미 누른 에러 나옴
        when(likeRepository.findByUserAndPost(doLikeUser, post)).thenReturn(Optional.empty());
        when(likeRepository.save(any())).thenReturn(like);

        //post없음
        PostException postException = assertThrows(PostException.class,
                () -> postService.like(post.getId(), doLikeUser.getUsername()));

        assertThat(postException.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);
        assertThat(postException.getErrorCode().getStatus()).isEqualTo(ErrorCode.POST_NOT_FOUND.getStatus());
        assertThat(postException.getErrorCode().getMessage()).isEqualTo(ErrorCode.POST_NOT_FOUND.getMessage());
    }


    @Test
    @DisplayName("좋아요 실패2 : 유저 없음")
    void 좋아요_실패_user없음() throws Exception {

        AllFixture all = AllFixture.getDto();
        /**좋아요 누를 유저**/
        User doLikeUser = UserEntityFixture.get(all.getUserName(), all.getPassword());
        /**post 작성 유저**/
        User UserOfPost = User.builder().id(2l).userName("post작성유저").build();
        Post post = PostEntityFixture.get(UserOfPost);
        LikeEntity like = LikeFixture.get(doLikeUser, post);
        AlarmEntity alarm= AlarmFixture.get(post,doLikeUser,AlarmType.NEW_LIKE_ON_POST);


        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        /**유저 없는 상황 가정🔽**/
        when(userRepository.findOptionalByUserName(doLikeUser.getUsername())).thenReturn(Optional.empty());
        //Optional.of(like) 를 아래 thenReturn에 넣어주면 좋아요 이미 누른 에러 나옴
        when(likeRepository.findByUserAndPost(doLikeUser, post)).thenReturn(Optional.empty());
        when(likeRepository.save(any())).thenReturn(like);

        //user 없음
        UserException userException = assertThrows(UserException.class,
                () -> postService.like(post.getId(), doLikeUser.getUsername()));

        assertThat(userException.getErrorCode()).isEqualTo(ErrorCode.USERNAME_NOT_FOUND);
        assertThat(userException.getErrorCode().getStatus()).isEqualTo(ErrorCode.USERNAME_NOT_FOUND.getStatus());
        assertThat(userException.getErrorCode().getMessage()).isEqualTo(ErrorCode.USERNAME_NOT_FOUND.getMessage());
    }


    @Test
    @DisplayName("좋아요 실패3 : 이미 해당 게시글에 좋아요를 함")
    void 좋아요_실패_이미좋아요함() throws Exception {

        AllFixture all = AllFixture.getDto();
        /**좋아요 누를 유저**/
        User doLikeUser = UserEntityFixture.get(all.getUserName(), all.getPassword());
        /**post 작성 유저**/
        User UserOfPost = User.builder().id(2l).userName("post작성유저").build();
        Post post = PostEntityFixture.get(UserOfPost);
        LikeEntity like = LikeFixture.get(doLikeUser, post);
        AlarmEntity alarm= AlarmFixture.get(post,doLikeUser,AlarmType.NEW_LIKE_ON_POST);


        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(userRepository.findOptionalByUserName(doLikeUser.getUsername())).thenReturn(Optional.of(doLikeUser));
        /**이미 좋아요를 눌러서 empty가 아님🔽**/
        //Optional.of(like) 를 아래 thenReturn에 넣어주면 좋아요 이미 누른 에러 나옴
        when(likeRepository.findByUserAndPost(doLikeUser, post)).thenReturn(Optional.of(like));
        when(likeRepository.save(any())).thenReturn(like);

        //user 없음
        LikeException likeException = assertThrows(LikeException.class,
                () -> postService.like(post.getId(), doLikeUser.getUsername()));

        /**CONFILT 409에러 유발 테스트🔽**/
        assertThat(likeException.getErrorCode()).isEqualTo(ErrorCode.ALREADY_LIKED);
        assertThat(likeException.getErrorCode().getStatus()).isEqualTo(ErrorCode.ALREADY_LIKED.getStatus());
        assertThat(likeException.getErrorCode().getMessage()).isEqualTo(ErrorCode.ALREADY_LIKED.getMessage());
    }
}
