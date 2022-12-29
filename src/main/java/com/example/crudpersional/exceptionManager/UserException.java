package com.example.crudpersional.exceptionManager;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserException extends RuntimeException{

    ErrorCode errorCode;
    private String message; //String.format("UserName:%s", userJoinRequest.getUserName())



    public UserException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = null;
    }

    @Override
    public String toString() {
        if(message == null) {
            return errorCode.getMessage();
        }
        return String.format("%s. %s", errorCode.getMessage(), message);
    }
}