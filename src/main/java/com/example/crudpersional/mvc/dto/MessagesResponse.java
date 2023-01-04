package com.example.crudpersional.mvc.dto;

import com.example.crudpersional.domain.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class MessagesResponse {

    private List<Comment> messages;

    private Integer count;
}
