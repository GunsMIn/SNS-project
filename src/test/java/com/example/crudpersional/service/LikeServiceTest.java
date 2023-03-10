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
    LikeService likeService;
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
    @DisplayName("????????? ??????")
    void ?????????_??????() throws Exception {
        AllFixture all = AllFixture.getDto();
        /**????????? ?????? ??????**/
        User doLikeUser = UserEntityFixture.get(all.getUserName(), all.getPassword());
        /**post ?????? ??????**/
        User UserOfPost = User.builder().id(2l).userName("post????????????").build();
        Post post = PostEntityFixture.get(UserOfPost);
        LikeEntity like = LikeFixture.get(doLikeUser, post);
        AlarmEntity alarm= AlarmFixture.get(post,doLikeUser,AlarmType.NEW_LIKE_ON_POST);


        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(userRepository.findOptionalByUserName(doLikeUser.getUsername())).thenReturn(Optional.of(doLikeUser));
        //Optional.of(like) ??? ?????? thenReturn??? ???????????? ????????? ?????? ?????? ?????? ??????
        when(likeRepository.findByUserAndPost(doLikeUser, post)).thenReturn(Optional.empty());
        when(likeRepository.save(any())).thenReturn(like);

        LikeResponse response = likeService.likes(post.getId(), doLikeUser.getUsername());
        //assertj
        assertThat(response.getLikeId()).isEqualTo(like.getId());
        //jupiter
        assertDoesNotThrow(()-> response);
    }

    @Test
    @DisplayName("????????? ??????1 : ????????? ?????? post ??????")
    void ?????????_??????_post??????() throws Exception {

        AllFixture all = AllFixture.getDto();
        /**????????? ?????? ??????**/
        User doLikeUser = UserEntityFixture.get(all.getUserName(), all.getPassword());
        /**post ?????? ??????**/
        User UserOfPost = User.builder().id(2l).userName("post????????????").build();
        Post post = PostEntityFixture.get(UserOfPost);
        LikeEntity like = LikeFixture.get(doLikeUser, post);
        AlarmEntity alarm= AlarmFixture.get(post,doLikeUser,AlarmType.NEW_LIKE_ON_POST);

        /**Post ?????? ?????? ??????????**/
        when(postRepository.findById(any())).thenReturn(Optional.empty());
        when(userRepository.findOptionalByUserName(doLikeUser.getUsername())).thenReturn(Optional.of(doLikeUser));
        //Optional.of(like) ??? ?????? thenReturn??? ???????????? ????????? ?????? ?????? ?????? ??????
        when(likeRepository.findByUserAndPost(doLikeUser, post)).thenReturn(Optional.empty());
        when(likeRepository.save(any())).thenReturn(like);

        //post??????
        PostException postException = assertThrows(PostException.class,
                () -> likeService.like(post.getId(), doLikeUser.getUsername()));

        assertThat(postException.getErrorCode()).isEqualTo(ErrorCode.POST_NOT_FOUND);
        assertThat(postException.getErrorCode().getStatus()).isEqualTo(ErrorCode.POST_NOT_FOUND.getStatus());
        assertThat(postException.getErrorCode().getMessage()).isEqualTo(ErrorCode.POST_NOT_FOUND.getMessage());
    }


    @Test
    @DisplayName("????????? ??????2 : ?????? ??????")
    void ?????????_??????_user??????() throws Exception {

        AllFixture all = AllFixture.getDto();
        /**????????? ?????? ??????**/
        User doLikeUser = UserEntityFixture.get(all.getUserName(), all.getPassword());
        /**post ?????? ??????**/
        User UserOfPost = User.builder().id(2l).userName("post????????????").build();
        Post post = PostEntityFixture.get(UserOfPost);
        LikeEntity like = LikeFixture.get(doLikeUser, post);
        AlarmEntity alarm= AlarmFixture.get(post,doLikeUser,AlarmType.NEW_LIKE_ON_POST);


        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        /**?????? ?????? ?????? ??????????**/
        when(userRepository.findOptionalByUserName(doLikeUser.getUsername())).thenReturn(Optional.empty());
        //Optional.of(like) ??? ?????? thenReturn??? ???????????? ????????? ?????? ?????? ?????? ??????
        when(likeRepository.findByUserAndPost(doLikeUser, post)).thenReturn(Optional.empty());
        when(likeRepository.save(any())).thenReturn(like);

        //user ??????
        UserException userException = assertThrows(UserException.class,
                () -> likeService.like(post.getId(), doLikeUser.getUsername()));

        assertThat(userException.getErrorCode()).isEqualTo(ErrorCode.USERNAME_NOT_FOUND);
        assertThat(userException.getErrorCode().getStatus()).isEqualTo(ErrorCode.USERNAME_NOT_FOUND.getStatus());
        assertThat(userException.getErrorCode().getMessage()).isEqualTo(ErrorCode.USERNAME_NOT_FOUND.getMessage());
    }


    @Test
    @DisplayName("????????? ??????3 : ?????? ?????? ???????????? ???????????? ???")
    void ?????????_??????_??????????????????() throws Exception {

        AllFixture all = AllFixture.getDto();
        /**????????? ?????? ??????**/
        User doLikeUser = UserEntityFixture.get(all.getUserName(), all.getPassword());
        /**post ?????? ??????**/
        User UserOfPost = User.builder().id(2l).userName("post????????????").build();
        Post post = PostEntityFixture.get(UserOfPost);
        LikeEntity like = LikeFixture.get(doLikeUser, post);



        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(userRepository.findOptionalByUserName(doLikeUser.getUsername())).thenReturn(Optional.of(doLikeUser));
        /**?????? ???????????? ????????? empty??? ??????????**/
        //Optional.of(like) ??? ?????? thenReturn??? ???????????? ????????? ?????? ?????? ?????? ??????
        when(likeRepository.findByUserAndPost(doLikeUser, post)).thenReturn(Optional.of(like));
        when(likeRepository.save(any())).thenReturn(like);

        //user ??????
        LikeException likeException = assertThrows(LikeException.class,
                () -> likeService.like(post.getId(), doLikeUser.getUsername()));

        /**CONFILT 409?????? ?????? ?????????????**/
        assertThat(likeException.getErrorCode()).isEqualTo(ErrorCode.ALREADY_LIKED);
        assertThat(likeException.getErrorCode().getStatus()).isEqualTo(ErrorCode.ALREADY_LIKED.getStatus());
        assertThat(likeException.getErrorCode().getMessage()).isEqualTo(ErrorCode.ALREADY_LIKED.getMessage());
    }
}
