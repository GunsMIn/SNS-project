package com.example.crudpersional.exceptionManager;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.SQLException;

@Getter
@AllArgsConstructor
public class ErrorDto {
    private String errorCode;
    private String message;

    public ErrorDto(LikeException e) {
        this.errorCode = e.getErrorCode().toString();
        this.message = e.getErrorCode().getMessage();
    }

    public ErrorDto(SQLException e) {
        this.errorCode = ErrorCode.DATABASE_ERROR.toString();
        this.message = ErrorCode.DATABASE_ERROR.getMessage();
    }
}