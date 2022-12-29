package com.example.crudpersional.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Result<T> { // count 필요 응답 class
    private int count; // list.sise() 개수
    private T data; // list 타입

}