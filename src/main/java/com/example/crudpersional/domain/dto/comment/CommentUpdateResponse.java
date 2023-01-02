package com.example.crudpersional.domain.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentUpdateResponse {
    private String message;
    private Long commentId;
}
