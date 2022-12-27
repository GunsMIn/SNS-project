package com.example.crudpersional.domain.dto.post;

import com.example.crudpersional.domain.entity.Post;
import lombok.*;

@Getter @AllArgsConstructor @NoArgsConstructor @ToString @Builder
public class PostUpdateRequest {

    private String title;
    private String body;

    public Post toEntity() {
        return Post.builder()
                .title(title)
                .body(body)
                .build();
    }
}
