package com.example.crudpersional.domain.dto.post;

import com.example.crudpersional.domain.entity.LikeEntity;
import com.example.crudpersional.domain.entity.Post;
import com.example.crudpersional.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikeResponse {
    private Long likeId;
    private Long postId;
    private String  userName;
    private String message;
    private Integer count;// 좋아요 갯수


    public static LikeResponse of(LikeEntity likeEntity) {

        return LikeResponse.builder()
                .likeId(likeEntity.getId())
                .postId(likeEntity.getPost().getId())
                .userName(likeEntity.getUser().getUsername())
                .message("좋아요를 눌렀습니다")
                .build();
    }

    public static LikeResponse of(LikeEntity likeEntity,Integer count) {
        return LikeResponse.builder()
                .likeId(likeEntity.getId())
                .postId(likeEntity.getPost().getId())
                .userName(likeEntity.getUser().getUsername())
                .count(count)
                .message("좋아요를 눌렀습니다")
                .build();
    }

}
