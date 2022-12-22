package com.example.crudpersional.mvc.dto;

import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.domain.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberForm {

    private String userName;

    private String Password;

    public User toEntity(String password) {
        return User.builder()
                .userName(userName)
                .password(password)
                .role(UserRole.USER)
                .build();
    }

}
