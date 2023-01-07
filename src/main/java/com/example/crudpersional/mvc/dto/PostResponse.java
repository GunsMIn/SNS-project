package com.example.crudpersional.mvc.dto;

import com.example.crudpersional.domain.dto.comment.PostMineDto;
import com.example.crudpersional.domain.entity.Comment;
import com.example.crudpersional.domain.entity.LikeEntity;
import com.example.crudpersional.domain.entity.Post;
import com.example.crudpersional.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.LAZY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostResponse {

    private Long id;
    private String title;
    private String body;
    private User user;
    private String registeredAt;
    private Integer commentCount;
    private Integer likeCount;


    /* Page<Entity> -> Page<Dto> 변환처리 */
    public static Page<PostResponse> toDtoList(Page<Post> post){
        Page<PostResponse> postDtoList =
                post.map(m -> PostResponse.builder()
                        .id(m.getId())
                        .title(m.getTitle())
                        .body(m.getBody())
                        .user(m.getUser())
                        .registeredAt(m.getRegisteredAt())
                        .commentCount(m.getComments().size())
                        .likeCount(m.getLikes().size())
                        .build());

        return postDtoList;
    }

}
