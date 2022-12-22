package com.example.crudpersional.mvc.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class PostForm {

    //userId는 추후 다르게 넣어 줄것이다.
    private Long userId;
    private String title;
    private String body;
}
