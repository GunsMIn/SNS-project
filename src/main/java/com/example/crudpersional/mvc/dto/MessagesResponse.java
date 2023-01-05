package com.example.crudpersional.mvc.dto;

import com.example.crudpersional.domain.dto.comment.CommentResponse;
import com.example.crudpersional.domain.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class MessagesResponse {

    private List<CommentResponse> messages;

    private Integer count;
}
