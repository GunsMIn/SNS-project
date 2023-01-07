package com.example.crudpersional.service;

import com.example.crudpersional.domain.dto.post.LikeResponse;
import com.example.crudpersional.domain.entity.AlarmEntity;
import com.example.crudpersional.domain.entity.LikeEntity;
import com.example.crudpersional.domain.entity.Post;
import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.exceptionManager.ErrorCode;
import com.example.crudpersional.exceptionManager.LikeException;
import com.example.crudpersional.exceptionManager.PostException;
import com.example.crudpersional.exceptionManager.UserException;
import com.example.crudpersional.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        //해당 글 찾음
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND, "해당 글은 존재하지 않습니다"));
        //해당 유저 찾음
        User user = userRepository.findOptionalByUserName(userName)
                .orElseThrow(() -> new UserException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s님은 존재하지 않습니다.", userName)));

        //like 눌렀는지 확인 비지니스 로직🔽
        //ifPresent() 메소드 = 값을 가지고 있는지 확인 후 예외처리 / 값이 존재한다면 예외처리 진행
        likeEntityRepository.findByUserAndPost(user,post)
                .ifPresent(entity -> {
                    log.info("에러 터져야함");
                    throw new LikeException(ErrorCode.ALREADY_LIKED, ErrorCode.ALREADY_LIKED.getMessage());
                });
        ////like 눌렀는지 확인 비지니스 로직 끝
        /**해당 포스트의 likeCount++ 해주는 메서드**/
        post.addLike();
        LikeEntity like = LikeEntity.of(user, post);
        LikeEntity savedLike = likeEntityRepository.save(like);
        /**해당 글 좋아요 갯수도 구하기**/
        Integer count = getLikeCount(postId);

        LikeResponse likeResponse = LikeResponse.of(savedLike,count);
        //*좋아요 눌렀을 때 알림 동작*//*
        // 알림수신자 ,알림 타입 ,발신자 id ,알림 주체 포스트 id
        AlarmEntity entity = AlarmEntity.of(post.getUser(), NEW_LIKE_ON_POST, user.getId(), post.getId());
        alarmRepository.save(entity); // 알림 저장

        return likeResponse;
    }

    /**like void**/
    public void like(Long postId,String userName) {
        //해당 글 찾음
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND, "해당 글은 존재하지 않습니다"));
        //해당 유저 찾음
        User user = userRepository.findOptionalByUserName(userName)
                .orElseThrow(() -> new UserException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s님은 존재하지 않습니다.", userName)));

        //like 눌렀는지 확인 비지니스 로직🔽
        //ifPresent() 메소드 = 값을 가지고 있는지 확인 후 예외처리 / 값이 존재한다면 예외처리 진행
        likeEntityRepository.findByUserAndPost(user,post)
                .ifPresent(entity -> {
                    log.info("에러 터져야함");
                    throw new LikeException(ErrorCode.ALREADY_LIKED);
                });
        ////like 눌렀는지 확인 비지니스 로직 끝

        LikeEntity like = LikeEntity.of(user, post);
        //좋아요 save부분
        LikeEntity savedLike = likeEntityRepository.save(like);
        LikeResponse likeResponse = LikeResponse.of(savedLike);

        /*좋아요 눌렀을 때 알림 동작*/
        // 알림수신자 ,알림 타입 ,발신자 id ,알림 주체 포스트 id
        AlarmEntity entity = AlarmEntity.of(post.getUser(), NEW_LIKE_ON_POST, user.getId(), post.getId());
        alarmRepository.save(entity); // 알림 저장

    }

    /**
     * 해당 글 좋아요 개수
     * @PathVarable로 들어오는 postId로 post entity조회 후 좋아요 count 계산 후 반환
     * */
    public Integer getLikeCount(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND,ErrorCode.POST_NOT_FOUND.getMessage()));

        Integer postLikeCount = likeEntityRepository.countByPost(post);
        return postLikeCount;
    }


}
