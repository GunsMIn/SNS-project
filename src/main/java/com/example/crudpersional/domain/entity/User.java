package com.example.crudpersional.domain.entity;

import javax.persistence.*;

import static javax.persistence.EnumType.*;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userName;

    private String password;

    @Enumerated(STRING)
    private UserRole role;
}
