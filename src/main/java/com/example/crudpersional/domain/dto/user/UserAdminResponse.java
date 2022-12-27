package com.example.crudpersional.domain.dto.user;

import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.domain.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UserAdminResponse {

    private Long userId;
    private String userName;
    private UserRole role;

    public static UserAdminResponse transferResponse(User user) {
        return UserAdminResponse.builder()
                .userId(user.getId())
                .userName(user.getUserName())
                .role(user.getRole())
                .build();
    }
}
