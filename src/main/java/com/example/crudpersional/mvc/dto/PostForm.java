package com.example.crudpersional.mvc.dto;

import com.example.crudpersional.domain.entity.Post;
import com.example.crudpersional.domain.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostForm {

    private String userName;
    @NotEmpty(message ="글의 제목을 입력해주세요")
    private String title;
    @NotEmpty(message ="글의 내용을 입력해주세요")
    private String body;

    //User user
    public Post toEntity(User user) {
        return Post.builder()
                .title(title)
                .body(body)
                .user(user)
                .build();
    }
}