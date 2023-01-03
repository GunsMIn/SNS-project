package com.example.crudpersional.domain.entity;


import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.*;

@Builder
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "deleted = false")
@SQLDelete(sql = "UPDATE Comment SET deleted = true WHERE id = ?")
public class Comment extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY) // 다 대 1 관계
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = LAZY) // 다 대 1 관계
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "comment")
    private String comment;

    /**SoftDeleteColumn**/
    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    /**SoftDelete**/
    public void delete() {
        this.deleted = true;
    }

    //dto - > entity로 바꾸는 메서드
    public static Comment of(User user, Post post, String comment) {
        Comment entity = Comment.builder()
                .user(user)
                .post(post)
                .comment(comment)
                .build();
        return entity;
    }

    public Comment change(String updateComment) {
        this.comment = updateComment;
        return this;
    }

}

