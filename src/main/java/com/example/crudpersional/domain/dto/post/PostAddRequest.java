package com.example.crudpersional.domain.dto.post;

import com.example.crudpersional.domain.entity.Post;
import com.example.crudpersional.domain.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostAddRequest {

    private Long userId;
    private String title;
    private String body;

    public Post toEntity(User user) {
       return Post.builder()
                .title(title)
                .body(body)
                .user(user)
                .build();
    }
}
