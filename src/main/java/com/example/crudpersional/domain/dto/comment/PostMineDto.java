package com.example.crudpersional.domain.dto.comment;

import com.example.crudpersional.domain.entity.Post;
import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostMineDto {

        private Long id;
        private String title;
        private String body;
        private String userName;
        private String createdAt;

        public static PostMineDto fromEntity(Post post) {
            return PostMineDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .body(post.getBody())
                    .userName(post.getUser().getUsername())
                    .createdAt(post.getRegisteredAt())
                    .build();
        }

        /* Page<Entity> -> Page<Dto> 변환처리 */
        public static Page<PostMineDto> toDtoList(Page<Post> post){
            Page<PostMineDto> postDtoList =
                    post.map(m -> PostMineDto.builder()
                                                .id(m.getId())
                                                .title(m.getTitle())
                                                .body(m.getBody())
                                                .userName(m.getUser().getUsername())
                                                .createdAt(m.getRegisteredAt())
                                                .build());

                    return postDtoList;
        }
    }


