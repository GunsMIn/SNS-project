package com.example.crudpersional.mvc.dto;

import com.example.crudpersional.domain.dto.post.PostSelectResponse;
import com.example.crudpersional.domain.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
public class PostMvcResponse {

    private Long postId;

    private String title;

    private String body;

    private String userName;

    private String createdAt;

    private String lastModifiedAt;

    public PostMvcResponse(PostSelectResponse post) {
        this.postId = post.getId();
        this.title = post.getTitle();
        this.body = post.getBody();
        this.userName = post.getUserName();
        this.createdAt = post.getCreatedAt();
        this.lastModifiedAt = post.getLastModifiedAt();
    }
}
