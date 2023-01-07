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
 @DisplayName("댓글 등록 성공/실패")
 class Insert{
    @Test
    @DisplayName("답변 등록 성공")
    void 답변_등록_성공() throws Exception {

        AllFixture all = AllFixture.getDto();
        User user = UserEntityFixture.get(all.getUserName(), all.getPassword());
        Post post = PostEntityFixture.get(user);
        Comment comment = CommentFixture.get(user, post);
        //글 작성시 알림 발생
        AlarmEntity alarm= AlarmFixture.get(post, user, AlarmType.NEW_COMMENT_ON_POST);
        /**답변 등록 성공 가정**/
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(userRepository.findOptionalByUserName(anyString())).thenReturn(Optional.of(user));
        when(commentRepository.save(any())).thenReturn(comment);
        when(alarmRepository.save(any())).thenReturn(alarm);

        CommentResponse commentResponse = postService.writeComment(all.getPostId(), all.getComment(), all.getUserName());

        assertEquals(commentResponse.getComment(),"댓글씁니다");
        assertEquals(commentResponse.getUserName(),"test");
        assertEquals(commentResponse.getId(),1L);
        assertEquals(commentResponse.getPostId(),1L);


        assertDoesNotThrow(()->commentResponse);
    }

    @Test
    @WithMockUser
    @DisplayName("댓글 등록 실패 : 유저 존재 하지 않음")
    void 댓글_등록_실패1() throws Exception {

        AllFixture all = AllFixture.getDto();
        User user = UserEntityFixture.get(all.getUserName(), all.getPassword());
        Post post = PostEntityFixture.get(user);
        Comment comment = CommentFixture.get(user, post);

        /**회원이 존재하지 않는 상황 가정**/
        when(userRepository.findOptionalByUserName(anyString())).thenReturn(Optional.empty());
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(commentRepository.save(any())).thenReturn(comment);

        UserException userException = assertThrows(UserException.class,
                () -> postService.writeComment(all.getPostId(), all.getComment(), all.getUserName()));

        Assertions.assertEquals(userException.getErrorCode(), ErrorCode.USERNAME_NOT_FOUND);
        Assertions.assertEquals(userException.getErrorCode().getMessage(), "해당 user를 찾을 수 없습니다.");
    }

    @Test
    @WithMockUser
    @DisplayName("댓글 등록 실패 : 포스트 존재 하지 않음")
    void 댓글_등록_실패2() throws Exception {

        AllFixture all = AllFixture.getDto();
        User user = UserEntityFixture.get(all.getUserName(), all.getPassword());
        Post post = PostEntityFixture.get(user);
        Comment comment = CommentFixture.get(user, post);

        /**post가 존재하지 않는 상황 가정**/
        when(userRepository.findOptionalByUserName(anyString())).thenReturn(Optional.of(user));
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(commentRepository.save(any())).thenReturn(comment);

        PostException postException = assertThrows(PostException.class,
                () -> postService.writeComment(all.getPostId(), all.getComment(), all.getUserName()));

        Assertions.assertEquals(postException.getErrorCode(), ErrorCode.POST_NOT_FOUND);
        Assertions.assertEquals(postException.getErrorCode().getMessage(),  "해당 포스트가 없습니다.");
    }
 }


    @Nested
    @DisplayName("댓글 수정 성공/실패")
    class Update{
        @Test
        @WithMockUser
        @DisplayName("답변 수정 성공")
        void 답변_수정_성공() throws Exception {

            AllFixture all = AllFixture.getDto();
            User user = UserEntityFixture.get(all.getUserName(), all.getPassword());
            Post post = PostEntityFixture.get(user);
            Comment comment = CommentFixture.get(user, post);


            when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
            when(userRepository.findOptionalByUserName(anyString())).thenReturn(Optional.of(user));
            when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

            CommentUpdateResponse updateResponse = postService.modifyComment(all.getCommentId(), comment.getId(),"댓글수정", all.getUserName());

            assertEquals(updateResponse.getComment(),"댓글수정");
            assertEquals(updateResponse.getId(),comment.getId());
            assertEquals(updateResponse.getPostId(),comment.getPost().getId());
            assertDoesNotThrow(()->updateResponse);

        }

        @Test
        @WithMockUser
        @DisplayName("답변 수정 실패 : 답변 작성자 != 답변 수정 유저")
        void 답변_수정_실패_작성자_불일치() throws Exception {
            AllFixture all = AllFixture.getDto();
            //user1은 원래 comment 작성자
            User user1 = UserEntityFixture.get(all.getUserName(), all.getPassword());
            //user2은 user1이 작성헸던 post의  comment를 수정하려는 작성자 (여기서는 로그인 회원)
            // ADMIN 은 모든 수정이 가능해서 USERROLE USER로 설정해둠
            User user2 = User.builder().id(2l).userName("다른유저").role(UserRole.USER).build();
            Post post = PostEntityFixture.get(user1);
            //user1이 작성한 comment🔽
            Comment comment = CommentFixture.get(user1, post);
            when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
            //user1이 쓴 답변이라고 가정
            when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
            //로그인회원은 user2라고 가정
            when(userRepository.findOptionalByUserName(anyString())).thenReturn(Optional.of(user2));
            //user1이 쓴 comment를 user2가 로그인하여 수정하려함 -> UserException ErrorCode.INVALID_PERMISSION 발생
            UserException userException =
                    assertThrows(UserException.class, () -> postService.modifyComment(post.getId(),comment.getId(),
                            "댓글수정", user2.getUsername()));

            assertEquals(userException.getErrorCode(),ErrorCode.INVALID_PERMISSION);
            assertEquals(userException.getErrorCode().getMessage(),ErrorCode.INVALID_PERMISSION.getMessage());
        }


        @Test
        @WithMockUser
        @DisplayName("수정 실패 : 딥변 존재하지 않음")
        void 답변_실패_답변_존재하지않음() {
            AllFixture all = AllFixture.getDto();
            User user1 = UserEntityFixture.get(all.getUserName(), all.getPassword());
            User user2 = User.builder().id(2l).userName("다른유저").build();
            //user2가 작성한 post
            Post post = PostEntityFixture.get(user2);
            //user1가 user2가 쓴 글에 쓴 댓글
            Comment comment = CommentFixture.get(user1, post);

            /**답변 존재하지 않음 가정**/
            when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());
            when(userRepository.findOptionalByUserName(user2.getUsername())).thenReturn(Optional.of(user2));
            when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

            PostException postException = assertThrows(PostException.class, () ->
                    postService.modifyComment(post.getId(), comment.getId(),comment.getComment(), user2.getUsername()));

            assertEquals(postException.getErrorCode(), ErrorCode.COMMENT_NOT_FOUND);
            assertEquals(postException.getErrorCode().getMessage(), ErrorCode.COMMENT_NOT_FOUND.getMessage());
        }

        @Test
        @WithMockUser
        @DisplayName("수정 실패 : 회원 존재하지 않음")
        void 답변_실패_회원_존재하지않음() {
            AllFixture all = AllFixture.getDto();
            User user1 = UserEntityFixture.get(all.getUserName(), all.getPassword());
            User user2 = User.builder().id(2l).userName("다른유저").build();
            //user2가 작성한 post
            Post post = PostEntityFixture.get(user2);
            //user1가 user2가 쓴 글에 쓴 댓글
            Comment comment = CommentFixture.get(user1, post);

            when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
            when(userRepository.findOptionalByUserName(anyString())).thenReturn(Optional.empty());
            when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
            UserException userException = assertThrows(UserException.class, () ->
                    postService.modifyComment(post.getId(), comment.getId(),comment.getComment(), user2.getUsername()));

            assertEquals(userException.getErrorCode(), ErrorCode.USER_NOT_FOUND);
            assertEquals(userException.getErrorCode().getMessage(), ErrorCode.USER_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("댓글 삭제 성공/실패")
    class Delete {


     @Test
     @DisplayName("댓글 삭제 성공")
     void 댓글_삭제_성공() throws Exception {

         AllFixture all = AllFixture.getDto();
         User user = UserEntityFixture.get(all.getUserName(), all.getPassword());
         Post post = PostEntityFixture.get(user);
         Comment comment = CommentFixture.get(user, post);

         when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
         when(userRepository.findOptionalByUserName(anyString())).thenReturn(Optional.of(user));
         when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
         boolean result = postService.deleteComment(all.getPostId(),all.getCommentId(), all.getUserName());

         assertEquals(result,true);
         assertDoesNotThrow(()-> result);
     }


        @Test
        @DisplayName("댓글 삭제 실패 : 답변작성자 != 답변 수정시도 유저")
        void 댓글_삭제_실패1() throws Exception {
            AllFixture all = AllFixture.getDto();
            User user = UserEntityFixture.get(all.getUserName(), all.getPassword());
            /**admin은 모든 삭제가 가능하여 테스트시 role 반드시 넣어주어야함 **/
            User anotherUser = User.builder().id(2l).userName("다른유저").role(UserRole.USER).build();
            Post post = PostEntityFixture.get(user);
            Comment comment = CommentFixture.get(user, post);

            /**user가 작성한 comment**/
            when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
            /**수정시도하려는 사람 another user**/
            when(userRepository.findOptionalByUserName(anyString())).thenReturn(Optional.of(anotherUser));
            when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

            UserException userException = assertThrows(UserException.class, () -> postService.deleteComment(post.getId(),comment.getId(), anotherUser.getUsername()));

            assertEquals(userException.getErrorCode(),ErrorCode.INVALID_PERMISSION);
            assertEquals(userException.getErrorCode().getMessage(),ErrorCode.INVALID_PERMISSION.getMessage());

        }


        @Test
        @DisplayName("댓글 삭제 실패 : 답변 존재하지 않음")
        void 댓글_삭제_실패2() throws Exception {

            AllFixture all = AllFixture.getDto();
            User user = UserEntityFixture.get(all.getUserName(), all.getPassword());
            User anotherUser = User.builder().id(2l).userName("다른유저").build();
            Post post = PostEntityFixture.get(user);
            Comment comment = CommentFixture.get(user, post);
            when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
            when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());
            when(userRepository.findOptionalByUserName(anyString())).thenReturn(Optional.of(anotherUser));

            PostException postException = assertThrows(PostException.class, () -> postService.deleteComment(all.getPostId(),all.getCommentId(), all.getUserName()));

            assertEquals(postException.getErrorCode(),ErrorCode.COMMENT_NOT_FOUND);
            assertEquals(postException.getErrorCode().getStatus(),ErrorCode.COMMENT_NOT_FOUND.getStatus());
            assertEquals(postException.getErrorCode().getMessage(),ErrorCode.COMMENT_NOT_FOUND.getMessage());
        }


        @Test
        @DisplayName("댓글 삭제 실패 : 회원 존재하지 않음")
        void 댓글_삭제_실패3() throws Exception {

            AllFixture all = AllFixture.getDto();
            User user = UserEntityFixture.get(all.getUserName(), all.getPassword());
            User anotherUser = User.builder().id(2l).userName("다른유저").build();
            Post post = PostEntityFixture.get(user);
            Comment comment = CommentFixture.get(user, post);

            when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
            when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
            when(userRepository.findOptionalByUserName(anyString())).thenReturn(Optional.empty());

            UserException userException = assertThrows(UserException.class, () -> postService.deleteComment(all.getPostId(),all.getCommentId(), all.getUserName()));

            assertEquals(userException.getErrorCode(),ErrorCode.USER_NOT_FOUND);
            assertEquals(userException.getErrorCode().getStatus(),ErrorCode.USER_NOT_FOUND.getStatus());
            assertEquals(userException.getErrorCode().getMessage(),ErrorCode.USER_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("답변 페이징 조회")
    class SelectList {


        @Test
        @DisplayName("답변 리스트 조회 성공")
        void 답변_리스트_조회_성공() throws Exception {

            AllFixture all = AllFixture.getDto();
            User user = UserEntityFixture.get(all.getUserName(), all.getPassword());
            Post post = PostEntityFixture.get(user);
            //Comment comment = CommentFixture.get(user, post);
            when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
            when(commentRepository.findAllByPost(any(), any())).thenReturn(Page.empty());
            PageRequest request = PageRequest.of(0, 10, Sort.Direction.DESC, "registeredAt");

            Page<CommentResponse> responses = postService.getComments(post.getId(), request);

            assertEquals(responses.getTotalPages(), 1);
            assertDoesNotThrow(() -> responses);
        }

        @Test
        @DisplayName("답변 리스트 조회 실패 : 답변 리스트 조회 할 post 존재 하지 않음")
        void 답변_리스트_조회_실패() throws Exception {

            AllFixture all = AllFixture.getDto();
            User user = UserEntityFixture.get(all.getUserName(), all.getPassword());
            Post post = PostEntityFixture.get(user);
            //Comment comment = CommentFixture.get(user, post);
            when(postRepository.findById(anyLong())).thenReturn(Optional.empty());
            when(commentRepository.findAllByPost(any(), any())).thenReturn(Page.empty());
            PageRequest request = PageRequest.of(0, 10, Sort.Direction.DESC, "registeredAt");

            PostException postException = assertThrows(PostException.class,
                    () -> postService.getComments(post.getId(), request));


            assertEquals(postException.getErrorCode(), ErrorCode.POST_NOT_FOUND);
            assertEquals(postException.getErrorCode().getStatus(), ErrorCode.POST_NOT_FOUND.getStatus());
            assertEquals(postException.getErrorCode().getMessage(), ErrorCode.POST_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("마이피드 보기 성공/실패")
    class MyPeed{
    @Test
    @DisplayName("마이피드 보기 성공")
    void 마이피드_성공() throws Exception {

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
    @DisplayName("마이피드 보기 실패 : 회원 존재 하지 않음")
    void 마이피드_실패() throws Exception {
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
