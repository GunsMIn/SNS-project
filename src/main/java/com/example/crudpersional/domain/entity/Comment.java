package com.example.crudpersional.domain.entity;


import lombok.*;

import javax.persistence.*;

@Builder
@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // 다 대 1 관계
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne // 다 대 1 관계
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "comment")
    private String comment;

    //dto - > entity로 바꾸는 메서드
    public static Comment of(User user, Post post, String comment) {
        Comment entity = new Comment();
        entity.setUser(user);
        entity.setPost(post);
        entity.setComment(comment);
        return entity;
    }
}

