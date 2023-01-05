package com.example.crudpersional.domain.entity;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Builder
@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
//@Table(name = "\"likes\"")
//@SQLDelete(sql = "UPDATE \"likes\" SET deleted_at = current_timestamp WHERE id = ?")
//@Where(clause = "deleted_at is NULL")
@Where(clause = "deleted = false")
@SQLDelete(sql = "UPDATE LikeEntity SET deleted = true WHERE like_id = ?")
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

    /**SoftDeleteColumn**/
    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

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
