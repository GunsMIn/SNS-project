package com.example.crudpersional.domain.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @AllArgsConstructor @NoArgsConstructor
public class PostUpdateResponse {
    private String message;
    private Long postId;
}
