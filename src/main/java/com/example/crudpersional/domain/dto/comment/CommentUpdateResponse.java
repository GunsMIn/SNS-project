package com.example.crudpersional.domain.dto.comment;

import com.example.crudpersional.domain.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentUpdateResponse {
    private Long id;
    private String comment;
    private String userName;
    private Long postId;
    private String createdAt;
    private String lastModifiedAt;


    public static CommentUpdateResponse of(Comment comment){
        return new CommentUpdateResponse(
                comment.getId(),
                comment.getComment(),
                comment.getUser().getUsername(),
                comment.getPost().getId(),
                comment.getRegisteredAt(),
                comment.getUpdatedAt()
        );
    }
}
