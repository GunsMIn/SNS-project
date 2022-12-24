package com.example.crudpersional.mvc.dto;

import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.domain.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberForm {

    @NotEmpty(message = "회원 id를 입력해주세요")
    private String userName;
    @NotEmpty(message = "password를 입력해주세요")
    private String password;

    public User toEntity(String password) {
        return User.builder()
                .userName(userName)
                .password(password)
                .role(UserRole.USER)
                .build();
    }

}
