package com.example.crudpersional.domain.entity;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

import static javax.persistence.FetchType.*;

@Builder
@Setter
@Getter
@Entity
@NoArgsConstructor
/*@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE user SET deleted_at = CURRENT_TIMESTAMP where id = ?")*/
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

    @Builder
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

}
