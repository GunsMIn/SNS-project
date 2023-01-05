package com.example.crudpersional.mvc.dto;

import com.example.crudpersional.domain.entity.Post;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 게시글 정보를 리턴할 응답(Response) 클래스
 * Entity 클래스를 생성자 파라미터로 받아 데이터를 Dto로 변환하여 응답
 * 별도의 전달 객체를 활용해 연관관계를 맺은 엔티티간의 무한참조를 방지
 */

@Getter
public class PostsResponseDto {

    private Long id;
    private String title;
    private String writer;
    private String content;
    private String createdDate, modifiedDate;
    private int view;
    private Long userId;
    private List<CommentResponseDto> comments;

    /* Entity -> Dto*/
    public PostsResponseDto(Post posts) {
        this.id = posts.getId();
        this.title = posts.getTitle();
        this.writer = posts.getUser().getUsername();
        this.content = posts.getBody();
        this.createdDate = posts.getRegisteredAt();
        this.modifiedDate = posts.getUpdatedAt();
        this.userId = posts.getUser().getId();
        this.comments = posts.getComments().stream().map(CommentResponseDto::new).collect(Collectors.toList());
    }
}