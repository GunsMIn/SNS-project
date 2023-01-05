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
    private String userName;
    private Long postId;
    private String createdAt;
    private String lastModifiedAt;
    //댓글 작성 시 알람 유무 확인용 컬럼
    private boolean CheckAlarm;

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
/*
    public static CommentResponse toResponsO(Comment comment){
        return new CommentResponse(
                comment.getId(),
                comment.getComment(),
                comment.getUser().getUsername(),
                comment.getPost().getId(),
                comment.getRegisteredAt(),
                comment.getUpdatedAt()
        );
    }*/

    public CommentResponse(Long id, String comment, String userName, Long postId, String createdAt, String lastModifiedAt) {
        this.id = id;
        this.comment = comment;
        this.userName = userName;
        this.postId = postId;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
    }


}
