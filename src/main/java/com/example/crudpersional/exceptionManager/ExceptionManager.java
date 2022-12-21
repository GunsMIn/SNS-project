package com.example.crudpersional.exceptionManager;

import com.example.crudpersional.domain.dto.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionManager {


    @ExceptionHandler(UserException.class)
    public ResponseEntity<?> userAppExceptionHandler(UserException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name(),e.getErrorCode().getMessage()));
    }


    @ExceptionHandler(PostException.class)
    public ResponseEntity<?> postAppExceptionHandler(PostException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error(e.getErrorCode().name(),e.getErrorCode().getMessage()));
    }

  /*  @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> runtimeExceptionHandler(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST) // 5xx에러를 400에러로 바꿔줌
                .body(Response.error(e.getMessage()));
    }*/

}