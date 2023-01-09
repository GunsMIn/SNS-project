package com.example.crudpersional.domain.dto.comment;

import com.example.crudpersional.domain.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Builder @ToString
public class CommentResponse {
    private Long id;
    private String comment;
    private String userName; //알람 동작 시킨 user
    private Long postId;
    private String createdAt;
    private String lastModifiedAt;



    public static CommentResponse toResponse(Comment comment){
        return new CommentResponse(
                comment.getId(),
                comment.getComment(),
                comment.getUser().getUsername(),
                comment.getPost().getId(),
                comment.getRegisteredAt(),
                comment.getUpdatedAt()
        );
    }


}
