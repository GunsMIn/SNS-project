package com.example.crudpersional.domain.dto.comment;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentModifyRequest {
    //수정 내용
    private String comment;
}