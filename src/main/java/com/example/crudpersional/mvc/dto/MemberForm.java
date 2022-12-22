package com.example.crudpersional.mvc.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberForm {


    private String name;

    private String loginId;

    private String password;

    private String city;

    private String street;

    private String zipcode;
}
