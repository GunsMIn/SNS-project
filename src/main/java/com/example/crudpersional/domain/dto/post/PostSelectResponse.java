package com.example.crudpersional.domain.dto.post;

import com.example.crudpersional.domain.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostSelectResponse {

    private Long id;

    private String title;

    private String body;

    private String userName;

    private String createdAt;

    private String lastModifiedAt;


    public PostSelectResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.body = post.getBody();
        this.userName = post.getUser().getUserName();
        this.createdAt = post.getRegisteredAt();
        this.lastModifiedAt = post.getUpdatedAt();
    }

/*    public static Page<PostSelectResponse> toDtoList(Page<Post> postEntities){
        Page<PostSelectResponse> postDtoList = postEntities.map(m -> PostSelectResponse.builder()
                .id(m.getId())
                .title(m.getTitle())
                .body(m.getBody())
                .userName(m.getUser().getUserName())
                .createdAt(m.getRegisteredAt())
                .build());
        return postDtoList;
    }*/
}
