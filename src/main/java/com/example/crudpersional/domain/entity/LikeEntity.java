package com.example.crudpersional.domain.entity;

import lombok.*;

import javax.persistence.*;

@Builder
@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
//@Table(name = "\"like\"")
public class LikeEntity extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "like_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    private Integer count;

    //연관관계 편의 메서드
    //1. user
    //2. post의 값을 like 엔티티에 넣어준다.
    public static LikeEntity of(User user, Post post) {
        LikeEntity like = new LikeEntity();
        like.setUser(user);
        like.setPost(post);
        return like;
    }
}
