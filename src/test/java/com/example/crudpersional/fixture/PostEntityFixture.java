package com.example.crudpersional.fixture;

import com.example.crudpersional.domain.entity.Post;
import com.example.crudpersional.domain.entity.User;

//post
public class PostEntityFixture {
    public static Post get(String userName, String password) {
        Post post = Post.builder()
                .id(1L)
                .user(UserEntityFixture.get(userName, password))
                .title("title")
                .body("body")
                .build();
        return post;
    }

    public static Post get(User user) {
        Post post = Post.builder()
                .id(1L)
                .user(user)
                .title("title")
                .body("body")
                .build();
        return post;
    }
}
