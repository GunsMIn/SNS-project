package com.example.crudpersional.domain.dto.user;

import com.example.crudpersional.domain.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class UserListResponse {

    private Long id;

    private String userName;

    private UserRole role;
}
