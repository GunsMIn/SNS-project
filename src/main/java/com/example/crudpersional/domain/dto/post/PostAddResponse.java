package com.example.crudpersional.domain.dto.post;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder
@AllArgsConstructor
public class PostAddResponse {

    private String message;
    private Long postId;


}
