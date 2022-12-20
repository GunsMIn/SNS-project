package com.example.crudpersional.domain.dto.user;


import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.domain.entity.UserRole;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class UserJoinRequest {

    private String userName;
    private String password;

    public User toEntity(String password) {
        return User.builder()
                .userName(userName)
                .password(password)
                .role(UserRole.USER)
                .build();
    }
}
