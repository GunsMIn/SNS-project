package com.example.crudpersional.domain.dto.user;

import lombok.*;

@Data
@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
public class UserLoginRequest {


    private String userName;
    private String password;


    
}