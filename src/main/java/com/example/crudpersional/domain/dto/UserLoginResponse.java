package com.example.crudpersional.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Getter
@NoArgsConstructor
public class UserLoginResponse {
    private  String jwt;
}