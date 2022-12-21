package com.example.crudpersional.domain.dto.user;

import com.example.crudpersional.domain.entity.Post;
import com.example.crudpersional.domain.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;

@AllArgsConstructor @NoArgsConstructor @Getter
public class UserSelectResponse {


    private Long userId;

    private String userName;

    private UserRole role;


}
