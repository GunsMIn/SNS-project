package com.example.crudpersional.domain.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCommentRequest {
    private String comment;
    private String name;

    public PostCommentRequest(String comment) {
        this.comment = comment;
    }
}