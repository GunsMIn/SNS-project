package com.example.crudpersional.service;

import com.example.crudpersional.domain.dto.post.LikeResponse;
import com.example.crudpersional.domain.entity.*;
import com.example.crudpersional.exceptionManager.ErrorCode;
import com.example.crudpersional.exceptionManager.LikeException;
import com.example.crudpersional.exceptionManager.PostException;
import com.example.crudpersional.exceptionManager.UserException;
import com.example.crudpersional.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.crudpersional.domain.entity.alarm.AlarmType.NEW_COMMENT_ON_POST;
import static com.example.crudpersional.domain.entity.alarm.AlarmType.NEW_LIKE_ON_POST;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LikeService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeEntityRepository;
    private final CommentRepository commentRepository;
    private final AlarmRepository alarmRepository;


    /**like 엔티티 변환용**/
    public LikeResponse likes(Long postId, String userName) {
        //1.해당 글 찾음 2.해당 유저 찾음 3.like 눌렀는지 확인 비지니스 로직🔽
        Post post = checkPost(postId);
        User user = checkUser(userName);
        checkCountOfLike(post, user);
        /**해당 포스트의 likeCount++ 해주는 메서드**/
        post.addLike();
        LikeEntity like = LikeEntity.of(user, post);
        LikeEntity savedLike = likeEntityRepository.save(like);
        /**해당 글 좋아요 갯수도 구하기**/
        Integer count = getLikeCount(postId);
        LikeResponse likeResponse = LikeResponse.of(savedLike,count);

        /**좋아요 눌렀을 때 알림 SAVE()🔽**/
        /**비지니스 로직 : 알람 나 자신이 작성한 글에 좋아요를 눌렀을때는 알림 작동 안됨 (댓글 작성 후 알림 동작🔽)**/
        if (user.getId() != post.getUser().getId()) {
            // 수신자 ,알림 타입 ,발신자 id ,알림 주체 포스트 id
            AlarmEntity alarmEntity = AlarmEntity.of(post.getUser(), NEW_LIKE_ON_POST, user.getId(), post.getId());
            alarmRepository.save(alarmEntity);
        }
        return likeResponse;
    }


    /**like void**/
    public void like(Long postId,String userName) {
        //1.해당 글 찾음 2.해당 유저 찾음 3.like 눌렀는지 확인 비지니스 로직🔽
        Post post = checkPost(postId);
        User user = checkUser(userName);
        checkCountOfLike(post, user);
        LikeEntity like = LikeEntity.of(user, post);
        //좋아요 save부분
        LikeEntity savedLike = likeEntityRepository.save(like);
        LikeResponse likeResponse = LikeResponse.of(savedLike);
        /*좋아요 눌렀을 때 알림 동작*/
        // 알림수신자 ,알림 타입 ,발신자 id ,알림 주체 포스트 id
        AlarmEntity entity = AlarmEntity.of(post.getUser(), NEW_LIKE_ON_POST, user.getId(), post.getId());
        alarmRepository.save(entity); // 알림 저장
    }

    /**해당 글 좋아요 개수 @PathVarable로 들어오는 postId로 post entity조회 후 좋아요 count 계산 후 반환*/
    public Integer getLikeCount(Long postId) {
        Post post = checkPost(postId);
        Integer postLikeCount = likeEntityRepository.countByPost(post);
        return postLikeCount;
    }



    /**authentication.getName() 으로 해당 user 유뮤 검사 메서드**/
    private User checkUser(String userName) {
        /*user 찾기*/
        return userRepository.findOptionalByUserName(userName).orElseThrow(()
                -> new UserException(ErrorCode.USERNAME_NOT_FOUND, ErrorCode.USERNAME_NOT_FOUND.getMessage()));
    }

    /**postId(포스트 id)로 해당 Post 유무 검사(없다면 404에러)**/
    private Post checkPost(Long postId) {
        /*해당 post 찾기*/
        return postRepository.findById(postId).orElseThrow(()
                -> new PostException(ErrorCode.POST_NOT_FOUND,ErrorCode.POST_NOT_FOUND.getMessage()));
    }

    /**commentId(댓글 id)로 해당 Comment 유무 검사(없다면 404에러)**/
    private Comment checkComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new PostException(ErrorCode.COMMENT_NOT_FOUND, ErrorCode.COMMENT_NOT_FOUND.getMessage()));
    }

    /**좋아요 1번 이상 눌렀는지 확인하는 비지니스 로직**/
    private void checkCountOfLike(Post post, User user) {
        //ifPresent() 메소드 = 값을 가지고 있는지 확인 후 예외처리 / 값이 존재한다면 예외처리 진행
        likeEntityRepository.findByUserAndPost(user,post)
                .ifPresent(entity -> {
                    throw new LikeException(ErrorCode.ALREADY_LIKED, ErrorCode.ALREADY_LIKED.getMessage());
                });
    }


}
