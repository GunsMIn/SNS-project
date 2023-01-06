package com.example.crudpersional.mvc.service;

import com.example.crudpersional.domain.entity.LikeEntity;
import com.example.crudpersional.domain.entity.Post;
import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.exceptionManager.ErrorCode;
import com.example.crudpersional.exceptionManager.LikeException;
import com.example.crudpersional.exceptionManager.PostException;
import com.example.crudpersional.exceptionManager.UserException;
import com.example.crudpersional.repository.AlarmRepository;
import com.example.crudpersional.repository.LikeRepository;
import com.example.crudpersional.repository.PostRepository;
import com.example.crudpersional.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private final AlarmRepository alarmRepository;


    @Transactional
    public void AddLike(Long postId, String requestUserName) throws SQLException {

        User requestUser = userRepository.findOptionalByUserName(requestUserName)
                .orElseThrow(() -> new UserException(ErrorCode.USERNAME_NOT_FOUND));
        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));
        Long requestUserId = requestUser.getId();
        likeRepository.findLikeByUserAndPost(requestUserId, postId)
                .ifPresent((like) -> {
                    throw new LikeException(ErrorCode.ALREADY_LIKED);
                });
        likeRepository.save(LikeEntity.of(requestUser, foundPost));
    }
}
