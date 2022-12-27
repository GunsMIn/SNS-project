package com.example.crudpersional.domain.entity;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.*;
import static javax.persistence.EnumType.*;

@Getter
@Entity
@ToString
@NoArgsConstructor @Setter
/*@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE user SET deleted_at = CURRENT_TIMESTAMP where id = ?")*/
public class User extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    private String userName;

    private String password;

    @Enumerated(STRING)
    private UserRole role;

    @OneToMany(mappedBy = "user", cascade = ALL)
    private List<Post> posts = new ArrayList<>();

    @Builder
    public User(Long id, String userName, String password, UserRole role) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.role = role;
    }

    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public User(Long id, String userName, String password) {
        this.id = id;
        this.userName = userName;
        this.password = password;
    }
}
