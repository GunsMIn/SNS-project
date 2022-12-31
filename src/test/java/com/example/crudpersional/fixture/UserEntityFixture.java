package com.example.crudpersional.fixture;

import com.example.crudpersional.domain.entity.User;
import com.example.crudpersional.domain.entity.UserRole;

import java.sql.Timestamp;
import java.time.Instant;
//user in test
public class  UserEntityFixture {

    public static User get(String userName,String password) {
      return  User.builder()
               .id(1l)
                .userName(userName)
                .password(password)
                .registeredAt(String.valueOf(Timestamp.from(Instant.now())))
                .role(UserRole.USER)
                .build();

    }


}
