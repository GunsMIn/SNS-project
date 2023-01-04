package com.example.crudpersional.domain.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @AllArgsConstructor @NoArgsConstructor @Builder
public class PostDeleteResponse {

    private String message;
    private Long postId;
}


