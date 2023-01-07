package com.example.crudpersional.service;

import com.example.crudpersional.domain.dto.comment.CommentResponse;
import com.example.crudpersional.domain.dto.comment.CommentUpdateResponse;
import com.example.crudpersional.domain.entity.*;
import com.example.crudpersional.exceptionManager.ErrorCode;
import com.example.crudpersional.exceptionManager.PostException;
import com.example.crudpersional.exceptionManager.UserException;
import com.example.crudpersional.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.crudpersional.domain.entity.alarm.AlarmType.NEW_COMMENT_ON_POST;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CommentService {


    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeEntityRepository;
    private final CommentRepository commentRepository;
    private final AlarmRepository alarmRepository;


    /**comment 쓰기(INSERT)**/
    public CommentResponse writeComment(Long postId, String commentBody, String userName) {
        /*해당 post 찾기*/
        Post post = postRepository.findById(postId).orElseThrow(()
                -> new PostException(ErrorCode.POST_NOT_FOUND,ErrorCode.POST_NOT_FOUND.getMessage()));
        /*user 찾기*/
        User user = userRepository.findOptionalByUserName(userName).orElseThrow(()
                -> new UserException(ErrorCode.USERNAME_NOT_FOUND, ErrorCode.USERNAME_NOT_FOUND.getMessage()));
        /**post엔티티의 댓글 갯수 add 메서드**/
        /**나 같은 경우에는 mvc컨트롤러와 view 단과 서버통신시 필요한 컬럼(commentCount++용 메서드)**/
        post.addComment();
        //연관관계의 값을 넣어준 comment 엔티티🔽
        Comment commentEntity = Comment.of(user, post, commentBody);
        Comment savedComment = commentRepository.save(commentEntity);
        CommentResponse commentResponse = CommentResponse.toResponse(savedComment);
        /*댓글 작성 후 알림 동작🔽*/
                                                 // 수신자 ,알림 타입 ,발신자 id ,알림 주체 포스트 id
        AlarmEntity alarmEntity = AlarmEntity.of(post.getUser(), NEW_COMMENT_ON_POST, user.getId(), post.getId());
        alarmRepository.save(alarmEntity);
        return commentResponse;
    }


    /**comment 수정하기**/
    public CommentUpdateResponse modifyComment(Long postId, Long commentId, String updateComment, String name) {
        //수정 될 답변 관련 변수
        Comment changedComment = null;
        // 1.post 유무 검증 2.수정할 comment 유무 검증 3.user 유무 검증 🔽
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new PostException(ErrorCode.COMMENT_NOT_FOUND, ErrorCode.COMMENT_NOT_FOUND.getMessage()));
        User user = userRepository.findOptionalByUserName(name).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND,ErrorCode.POST_NOT_FOUND.getMessage()));
        //수정하려는 답글의 원작자 userId🔽
        Long commentUserId = comment.getUser().getId();
        //1.답글을 쓴 사람만이 수정 가능 2. ADMIN도 수정 가능🔽
        if (user.getRole().equals(UserRole.USER) && commentUserId != user.getId()) {
            throw new UserException(ErrorCode.INVALID_PERMISSION,ErrorCode.INVALID_PERMISSION.getMessage());
        }else {
            //dirty check 수정 메서드
            changedComment = comment.change(updateComment);
        }
        return CommentUpdateResponse.of(changedComment);
    }


    /**comment 삭제하기**/
    /**service test 하기 위해 void - > boolean으로 변경**/
    public boolean deleteComment(Long postId,Long commentId, String userName) {
        // 1.post 유무 검증 2.수정할 comment 유무 검증 3.user 유무 검증 🔽
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));
        Comment comment = commentRepository.findById(commentId).
                orElseThrow(() -> new PostException(ErrorCode.COMMENT_NOT_FOUND, ErrorCode.COMMENT_NOT_FOUND.getMessage()));
        User loginUser =
                userRepository.findOptionalByUserName(userName).orElseThrow(() -> new UserException(ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.getMessage()));
        //삭제하려는 답글의 원작자 userId🔽
        Long commentUserId = comment.getUser().getId();
        //1.답글을 쓴 사람만이 삭제하기 가능 2.ADMIN도 삭제하기 가능🔽
        if (loginUser.getRole().equals(UserRole.USER) && commentUserId != loginUser.getId()) {
            throw new UserException(ErrorCode.INVALID_PERMISSION, userName + "님은 답글을 삭제할 권한이 없습니다.");
        } else {
            /**post엔티티의 댓글 갯수 delete 메서드**/
            /**나 같은 경우에는 mvc컨트롤러와 view 단과 서버통신시 필요한 컬럼(commentCount--용 메서드)🔽**/
            post.deleteComment();
            commentRepository.deleteById(comment.getId());
        }
        return true;
    }

    /**comment 리스트 조회**/
    @Transactional(readOnly = true)
    public Page<CommentResponse> getComments(Long postId, Pageable pageable) {
        //해당 post 유무 조회
        Post post = postRepository.findById(postId).
                orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));
        //comment List
        Page<Comment> comments = commentRepository.findAllByPost(post, pageable);
        /**comments.map(c -> CommentResponse.toResponse(c)); 🔽 refactoring**/
        return comments.map(CommentResponse::toResponse);
    }
}
