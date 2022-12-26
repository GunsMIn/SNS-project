package com.example.crudpersional.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Response<T> {

    private String resultCode;
    private T result;

    public static <T> Response<T> error(String resultCode, T result) {
        return new Response(resultCode, result);
    }

    public static <T> Response<T> success(T result) {
        return new Response("SUCCESS", result);
    }

    public static Response<Void> success() {
        return new Response("SUCCESS", null);
    }

    public static Response<Void> success(String message) {
        return new Response("SUCCESS", message);
    }

    public static Response<String> successToMessage (String message) {
        return new Response("SUCCESS", message);
    }




   /* // 어떤 에러가 났는지 반환해주는 문자열
    private String resultCode;
    // 성공을 반환할 때 result로 감싸서 리턴.
    private T result;

    public static Response<Void> error(String resultCode,String result) {
        return new Response(resultCode, result);
    }

    public static <T> Response<T> success(T result){
        return new Response("SUCCESS", result);
        //result에는 객체가 들어간다.
    }*/
}
