package com.example.crudpersional.domain.entity;

import lombok.*;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;


@Setter
@Getter
@Entity
@NoArgsConstructor
@Where(clause = "deleted = false")
@SQLDelete(sql = "UPDATE Post SET deleted = true WHERE post_id = ?")
public class Post extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    private String title;

    private String body;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id") // 연관관계의 주인
    private User user;

    @OneToMany(mappedBy = "post",cascade = REMOVE, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post",cascade = REMOVE, orphanRemoval = true)
    private List<LikeEntity> likes = new ArrayList<>();

    //포스트 댓글 갯수
    private Integer commentCount = 0;
    //포스트 좋아요 갯수
    private Integer likeCount = 0;


    /**SoftDeleteColumn**/
    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    /**댓글 갯수 추가 삭제 메서드**/
    public void addComment() {
        this.commentCount++ ;
    }
    public void deleteComment() {
        this.commentCount--;
    }

    /**좋아요 갯수 추가 삭제 메서드**/
    public void addLike() {
        this.likeCount++ ;
    }
    public void deleteLike() {
        this.likeCount--;
    }


    @Builder
    public Post(Long id, String title, String body, User user, List<Comment> comments, List<LikeEntity> likes) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.user = user;
        this.comments = comments;
        this.likes = likes;
    }

    public Post(Long id, String title, String body, User user) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.user = user;
    }

    public static Post of(String title, String body, User user) {
        Post entity = new Post();
        entity.setTitle(title);
        entity.setBody(body);
        entity.setUser(user);
        return entity;
    }

    public void update(String title, String body) {
        this.title = title;
        this.body = body;
    }


}
