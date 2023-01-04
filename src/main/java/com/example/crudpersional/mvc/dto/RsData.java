package com.example.crudpersional.mvc.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RsData<T> {
    //결과 데이터
    private String resultCode;
    private String msg;
    private T data;
}
