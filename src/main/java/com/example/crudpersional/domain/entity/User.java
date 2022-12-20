package com.example.crudpersional.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

import static javax.persistence.EnumType.*;

@Getter
@Entity
@ToString
@NoArgsConstructor
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE user SET deleted_at = CURRENT_TIMESTAMP where id = ?")
public class User extends BaseEntity{

    @Id
    @GeneratedValue
    private Long id;

    private String userName;

    private String password;

    @Enumerated(STRING)
    private UserRole role;

    @Builder
    public User(Long id, String userName, String password, UserRole role) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.role = role;
    }
}
