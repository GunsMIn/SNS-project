package com.example.crudpersional.domain.dto.post;

import com.example.crudpersional.domain.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostSelectResponse {

    private Long id;

    private String title;

    private String body;

    private String userName;

    private String createdAt;

    private String lastModifiedAt;

    public PostSelectResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.body = post.getBody();
        this.userName = post.getUser().getUserName();
        this.createdAt = post.getRegisteredAt();
        this.lastModifiedAt = post.getUpdatedAt();
    }
}
