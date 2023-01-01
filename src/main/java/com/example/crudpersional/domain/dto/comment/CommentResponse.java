package com.example.crudpersional.domain.dto.comment;

import com.example.crudpersional.domain.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CommentResponse {
    private Long id;
    private String comment;
    private String userName;
    private Long postId;
    private String createdAt;
    private String lastModifiedAt;

    public static CommentResponse of(Comment comment){
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
