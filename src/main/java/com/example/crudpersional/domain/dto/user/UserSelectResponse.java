package com.example.crudpersional.domain.dto.user;

import com.example.crudpersional.domain.entity.Post;
import com.example.crudpersional.domain.entity.UserRole;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;

public class UserSelectResponse {


    private Long userId;

    private String userName;

    private UserRole role;


}
