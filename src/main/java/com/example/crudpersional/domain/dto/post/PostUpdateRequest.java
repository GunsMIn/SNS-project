package com.example.crudpersional.domain.dto.post;

import com.example.crudpersional.domain.entity.Post;
import lombok.*;

@Getter @AllArgsConstructor @NoArgsConstructor @ToString
public class PostUpdateRequest {

    //private Long userId;
    private String title;
    private String body;

    public Post toEntity() {
        return Post.builder()
                .title(title)
                .body(body)
                .build();
    }
}
