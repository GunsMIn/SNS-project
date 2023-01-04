package com.example.crudpersional.fixture;

import com.example.crudpersional.domain.entity.Comment;
import com.example.crudpersional.domain.entity.LikeEntity;
import com.example.crudpersional.domain.entity.Post;
import com.example.crudpersional.domain.entity.User;

public class LikeFixture {
    //서비스 테스트에서 필요한 likeEntity 생성 클래스와 메서드
    public static LikeEntity get(User user, Post post) {
        return LikeEntity.builder()
                .id(1l)
                .user(user)
                .post(post)
                .count(10)
                .build();
    }
}
