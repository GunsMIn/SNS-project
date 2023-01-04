package com.example.crudpersional.fixture;

import com.example.crudpersional.domain.entity.Comment;
import com.example.crudpersional.domain.entity.Post;
import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.domain.entity.UserRole;

import java.sql.Timestamp;
import java.time.Instant;

public class CommentFixture {

    public static Comment get(User user, Post post) {
        return  Comment.builder()
                .id(1l)
                .user(user)
                .post(post)
                .deleted(false)
                .comment("댓글씁니다")
                .build();

    }
}
