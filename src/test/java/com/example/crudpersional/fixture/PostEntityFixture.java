package com.example.crudpersional.fixture;

import com.example.crudpersional.domain.entity.Post;
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
}
