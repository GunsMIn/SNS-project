package com.example.crudpersional.service;
import com.example.crudpersional.domain.dto.comment.CommentResponse;
import com.example.crudpersional.domain.dto.comment.CommentUpdateResponse;
import com.example.crudpersional.domain.dto.comment.PostMineDto;
import com.example.crudpersional.domain.entity.*;
import com.example.crudpersional.domain.entity.alarm.AlarmType;
import com.example.crudpersional.exceptionManager.ErrorCode;
import com.example.crudpersional.exceptionManager.PostException;
import com.example.crudpersional.exceptionManager.UserException;
import com.example.crudpersional.fixture.*;
import com.example.crudpersional.repository.LikeRepository;
import com.example.crudpersional.repository.PostRepository;
import com.example.crudpersional.repository.UserRepository;
import org.hibernate.mapping.Any;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
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

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CommentServiceTest {

    @InjectMocks
    CommentService commentService;
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

 @Nested
 @DisplayName("?????? ?????? ??????/??????")
 class Insert{
    @Test
    @DisplayName("?????? ?????? ??????")
    void ??????_??????_??????() throws Exception {

        AllFixture all = AllFixture.getDto();
        User user = UserEntityFixture.get(all.getUserName(), all.getPassword());
        Post post = PostEntityFixture.get(user);
        Comment comment = CommentFixture.get(user, post);
        //??? ????????? ?????? ??????
        AlarmEntity alarm= AlarmFixture.get(post, user, AlarmType.NEW_COMMENT_ON_POST);
        /**?????? ?????? ?????? ??????**/
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(userRepository.findOptionalByUserName(anyString())).thenReturn(Optional.of(user));
        when(commentRepository.save(any())).thenReturn(comment);
        when(alarmRepository.save(any())).thenReturn(alarm);

        CommentResponse commentResponse = commentService.writeComment(all.getPostId(), all.getComment(), all.getUserName());

        assertEquals(commentResponse.getComment(),"???????????????");
        assertEquals(commentResponse.getUserName(),"test");
        assertEquals(commentResponse.getId(),1L);
        assertEquals(commentResponse.getPostId(),1L);


        assertDoesNotThrow(()->commentResponse);
    }

    @Test
    @WithMockUser
    @DisplayName("?????? ?????? ?????? : ?????? ?????? ?????? ??????")
    void ??????_??????_??????1() throws Exception {

        AllFixture all = AllFixture.getDto();
        User user = UserEntityFixture.get(all.getUserName(), all.getPassword());
        Post post = PostEntityFixture.get(user);
        Comment comment = CommentFixture.get(user, post);

        /**????????? ???????????? ?????? ?????? ??????**/
        when(userRepository.findOptionalByUserName(anyString())).thenReturn(Optional.empty());
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(commentRepository.save(any())).thenReturn(comment);

        UserException userException = assertThrows(UserException.class,
                () -> commentService.writeComment(all.getPostId(), all.getComment(), all.getUserName()));

        Assertions.assertEquals(userException.getErrorCode(), ErrorCode.USERNAME_NOT_FOUND);
        Assertions.assertEquals(userException.getErrorCode().getMessage(), "?????? user??? ?????? ??? ????????????.");
    }

    @Test
    @WithMockUser
    @DisplayName("?????? ?????? ?????? : ????????? ?????? ?????? ??????")
    void ??????_??????_??????2() throws Exception {

        AllFixture all = AllFixture.getDto();
        User user = UserEntityFixture.get(all.getUserName(), all.getPassword());
        Post post = PostEntityFixture.get(user);
        Comment comment = CommentFixture.get(user, post);

        /**post??? ???????????? ?????? ?????? ??????**/
        when(userRepository.findOptionalByUserName(anyString())).thenReturn(Optional.of(user));
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(commentRepository.save(any())).thenReturn(comment);

        PostException postException = assertThrows(PostException.class,
                () -> commentService.writeComment(all.getPostId(), all.getComment(), all.getUserName()));

        Assertions.assertEquals(postException.getErrorCode(), ErrorCode.POST_NOT_FOUND);
        Assertions.assertEquals(postException.getErrorCode().getMessage(),  "?????? ???????????? ????????????.");
    }
 }


    @Nested
    @DisplayName("?????? ?????? ??????/??????")
    class Update{
        @Test
        @WithMockUser
        @DisplayName("?????? ?????? ??????")
        void ??????_??????_??????() throws Exception {

            AllFixture all = AllFixture.getDto();
            User user = UserEntityFixture.get(all.getUserName(), all.getPassword());
            Post post = PostEntityFixture.get(user);
            Comment comment = CommentFixture.get(user, post);


            when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
            when(userRepository.findOptionalByUserName(anyString())).thenReturn(Optional.of(user));
            when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

            CommentUpdateResponse updateResponse = commentService.modifyComment(all.getCommentId(), comment.getId(),"????????????", all.getUserName());

            assertEquals(updateResponse.getComment(),"????????????");
            assertEquals(updateResponse.getId(),comment.getId());
            assertEquals(updateResponse.getPostId(),comment.getPost().getId());
            assertDoesNotThrow(()->updateResponse);

        }

        @Test
        @WithMockUser
        @DisplayName("?????? ?????? ?????? : ?????? ????????? != ?????? ?????? ??????")
        void ??????_??????_??????_?????????_?????????() throws Exception {
            AllFixture all = AllFixture.getDto();
            //user1??? ?????? comment ?????????
            User user1 = UserEntityFixture.get(all.getUserName(), all.getPassword());
            //user2??? user1??? ???????????? post???  comment??? ??????????????? ????????? (???????????? ????????? ??????)
            // ADMIN ??? ?????? ????????? ???????????? USERROLE USER??? ????????????
            User user2 = User.builder().id(2l).userName("????????????").role(UserRole.USER).build();
            Post post = PostEntityFixture.get(user1);
            //user1??? ????????? comment????
            Comment comment = CommentFixture.get(user1, post);
            when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
            //user1??? ??? ??????????????? ??????
            when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
            //?????????????????? user2?????? ??????
            when(userRepository.findOptionalByUserName(anyString())).thenReturn(Optional.of(user2));
            //user1??? ??? comment??? user2??? ??????????????? ??????????????? -> UserException ErrorCode.INVALID_PERMISSION ??????
            UserException userException =
                    assertThrows(UserException.class, () -> commentService.modifyComment(post.getId(),comment.getId(),
                            "????????????", user2.getUsername()));

            assertEquals(userException.getErrorCode(),ErrorCode.INVALID_PERMISSION);
            assertEquals(userException.getErrorCode().getMessage(),ErrorCode.INVALID_PERMISSION.getMessage());
        }


        @Test
        @WithMockUser
        @DisplayName("?????? ?????? : ?????? ???????????? ??????")
        void ??????_??????_??????_??????????????????() {
            AllFixture all = AllFixture.getDto();
            User user1 = UserEntityFixture.get(all.getUserName(), all.getPassword());
            User user2 = User.builder().id(2l).userName("????????????").build();
            //user2??? ????????? post
            Post post = PostEntityFixture.get(user2);
            //user1??? user2??? ??? ?????? ??? ??????
            Comment comment = CommentFixture.get(user1, post);

            /**?????? ???????????? ?????? ??????**/
            when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());
            when(userRepository.findOptionalByUserName(user2.getUsername())).thenReturn(Optional.of(user2));
            when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

            PostException postException = assertThrows(PostException.class, () ->
                    commentService.modifyComment(post.getId(), comment.getId(),comment.getComment(), user2.getUsername()));

            assertEquals(postException.getErrorCode(), ErrorCode.COMMENT_NOT_FOUND);
            assertEquals(postException.getErrorCode().getMessage(), ErrorCode.COMMENT_NOT_FOUND.getMessage());
        }

        @Test
        @WithMockUser
        @DisplayName("?????? ?????? : ?????? ???????????? ??????")
        void ??????_??????_??????_??????????????????() {
            AllFixture all = AllFixture.getDto();
            User user1 = UserEntityFixture.get(all.getUserName(), all.getPassword());
            User user2 = User.builder().id(2l).userName("????????????").build();
            //user2??? ????????? post
            Post post = PostEntityFixture.get(user2);
            //user1??? user2??? ??? ?????? ??? ??????
            Comment comment = CommentFixture.get(user1, post);

            when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
            when(userRepository.findOptionalByUserName(anyString())).thenReturn(Optional.empty());
            when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
            UserException userException = assertThrows(UserException.class, () ->
                    commentService.modifyComment(post.getId(), comment.getId(),comment.getComment(), user2.getUsername()));

            assertEquals(userException.getErrorCode(), ErrorCode.USERNAME_NOT_FOUND);
            assertEquals(userException.getErrorCode().getMessage(), ErrorCode.USERNAME_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("?????? ?????? ??????/??????")
    class Delete {


     @Test
     @DisplayName("?????? ?????? ??????")
     void ??????_??????_??????() throws Exception {

         AllFixture all = AllFixture.getDto();
         User user = UserEntityFixture.get(all.getUserName(), all.getPassword());
         Post post = PostEntityFixture.get(user);
         Comment comment = CommentFixture.get(user, post);

         when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
         when(userRepository.findOptionalByUserName(anyString())).thenReturn(Optional.of(user));
         when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
         boolean result = commentService.deleteComment(all.getPostId(),all.getCommentId(), all.getUserName());

         assertEquals(result,true);
         assertDoesNotThrow(()-> result);
     }


        @Test
        @DisplayName("?????? ?????? ?????? : ??????????????? != ?????? ???????????? ??????")
        void ??????_??????_??????1() throws Exception {
            AllFixture all = AllFixture.getDto();
            User user = UserEntityFixture.get(all.getUserName(), all.getPassword());
            /**admin??? ?????? ????????? ???????????? ???????????? role ????????? ?????????????????? **/
            User anotherUser = User.builder().id(2l).userName("????????????").role(UserRole.USER).build();
            Post post = PostEntityFixture.get(user);
            Comment comment = CommentFixture.get(user, post);

            /**user??? ????????? comment**/
            when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
            /**????????????????????? ?????? another user**/
            when(userRepository.findOptionalByUserName(anyString())).thenReturn(Optional.of(anotherUser));
            when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

            UserException userException = assertThrows(UserException.class, () -> commentService.deleteComment(post.getId(),comment.getId(), anotherUser.getUsername()));

            assertEquals(userException.getErrorCode(),ErrorCode.INVALID_PERMISSION);
            assertEquals(userException.getErrorCode().getMessage(),ErrorCode.INVALID_PERMISSION.getMessage());

        }


        @Test
        @DisplayName("?????? ?????? ?????? : ?????? ???????????? ??????")
        void ??????_??????_??????2() throws Exception {

            AllFixture all = AllFixture.getDto();
            User user = UserEntityFixture.get(all.getUserName(), all.getPassword());
            User anotherUser = User.builder().id(2l).userName("????????????").build();
            Post post = PostEntityFixture.get(user);
            Comment comment = CommentFixture.get(user, post);
            when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
            when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());
            when(userRepository.findOptionalByUserName(anyString())).thenReturn(Optional.of(anotherUser));

            PostException postException = assertThrows(PostException.class, () -> commentService.deleteComment(all.getPostId(),all.getCommentId(), all.getUserName()));

            assertEquals(postException.getErrorCode(),ErrorCode.COMMENT_NOT_FOUND);
            assertEquals(postException.getErrorCode().getStatus(),ErrorCode.COMMENT_NOT_FOUND.getStatus());
            assertEquals(postException.getErrorCode().getMessage(),ErrorCode.COMMENT_NOT_FOUND.getMessage());
        }


        @Test
        @DisplayName("?????? ?????? ?????? : ?????? ???????????? ??????")
        void ??????_??????_??????3() throws Exception {

            AllFixture all = AllFixture.getDto();
            User user = UserEntityFixture.get(all.getUserName(), all.getPassword());
            User anotherUser = User.builder().id(2l).userName("????????????").build();
            Post post = PostEntityFixture.get(user);
            Comment comment = CommentFixture.get(user, post);

            when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
            when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
            when(userRepository.findOptionalByUserName(anyString())).thenReturn(Optional.empty());

            UserException userException = assertThrows(UserException.class, () -> commentService.deleteComment(all.getPostId(),all.getCommentId(), all.getUserName()));

            assertEquals(userException.getErrorCode(),ErrorCode.USERNAME_NOT_FOUND);
            assertEquals(userException.getErrorCode().getStatus(),ErrorCode.USERNAME_NOT_FOUND.getStatus());
            assertEquals(userException.getErrorCode().getMessage(),ErrorCode.USERNAME_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("?????? ????????? ??????")
    class SelectList {


        @Test
        @DisplayName("?????? ????????? ?????? ??????")
        void ??????_?????????_??????_??????() throws Exception {

            AllFixture all = AllFixture.getDto();
            User user = UserEntityFixture.get(all.getUserName(), all.getPassword());
            Post post = PostEntityFixture.get(user);
            //Comment comment = CommentFixture.get(user, post);
            when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
            when(commentRepository.findAllByPost(any(), any())).thenReturn(Page.empty());
            PageRequest request = PageRequest.of(0, 10, Sort.Direction.DESC, "registeredAt");

            Page<CommentResponse> responses = commentService.getComments(post.getId(), request);

            assertEquals(responses.getTotalPages(), 1);
            assertDoesNotThrow(() -> responses);
        }

        @Test
        @DisplayName("?????? ????????? ?????? ?????? : ?????? ????????? ?????? ??? post ?????? ?????? ??????")
        void ??????_?????????_??????_??????() throws Exception {

            AllFixture all = AllFixture.getDto();
            User user = UserEntityFixture.get(all.getUserName(), all.getPassword());
            Post post = PostEntityFixture.get(user);
            //Comment comment = CommentFixture.get(user, post);
            when(postRepository.findById(anyLong())).thenReturn(Optional.empty());
            when(commentRepository.findAllByPost(any(), any())).thenReturn(Page.empty());
            PageRequest request = PageRequest.of(0, 10, Sort.Direction.DESC, "registeredAt");

            PostException postException = assertThrows(PostException.class,
                    () -> commentService.getComments(post.getId(), request));


            assertEquals(postException.getErrorCode(), ErrorCode.POST_NOT_FOUND);
            assertEquals(postException.getErrorCode().getStatus(), ErrorCode.POST_NOT_FOUND.getStatus());
            assertEquals(postException.getErrorCode().getMessage(), ErrorCode.POST_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("???????????? ?????? ??????/??????")
    class MyPeed{
    @Test
    @DisplayName("???????????? ?????? ??????")
    void ????????????_??????() throws Exception {

        AllFixture all = AllFixture.getDto();
        User user = UserEntityFixture.get(all.getUserName(), all.getPassword());
        Post post = PostEntityFixture.get(user);
        PageRequest request = PageRequest.of(0, 10, Sort.Direction.DESC, "registeredAt");

        when(userRepository.findOptionalByUserName(any())).thenReturn(Optional.of(user));
        when(postRepository.findPostsByUser(any(), any())).thenReturn(Page.empty());

        Page<PostMineDto> myPeed = postService.getMyPeed(user.getUsername(), request);

        assertThat(myPeed.getTotalPages()).isEqualTo(1);
        assertDoesNotThrow(() -> myPeed);
    }

    @Test
    @DisplayName("???????????? ?????? ?????? : ?????? ?????? ?????? ??????")
    void ????????????_??????() throws Exception {
        AllFixture all = AllFixture.getDto();
        User user = UserEntityFixture.get(all.getUserName(), all.getPassword());
        Post post = PostEntityFixture.get(user);
        PageRequest request = PageRequest.of(0, 10, Sort.Direction.DESC, "registeredAt");

        when(userRepository.findOptionalByUserName(any())).thenReturn(Optional.empty());
        when(postRepository.findPostsByUser(any(), any())).thenReturn(Page.empty());

        UserException userException = assertThrows(UserException.class,
                () -> postService.getMyPeed(user.getUsername(), request));

        assertThat(userException.getErrorCode()).isEqualTo(ErrorCode.USERNAME_NOT_FOUND);
        assertThat(userException.getErrorCode().getMessage()).isEqualTo(ErrorCode.USERNAME_NOT_FOUND.getMessage());
        assertThat(userException.getErrorCode().getStatus()).isEqualTo(ErrorCode.USERNAME_NOT_FOUND.getStatus());
    }
    }
 }
