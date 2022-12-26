package com.example.crudpersional.exceptionManager;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LikeException  extends RuntimeException{

    private ErrorCode errorCode;
    private String message;

    public LikeException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.message = null;
    }

    @Override
    public String getMessage() {
        if (message == null) {
            return errorCode.getMessage();
        } else {
            return String.format("%s. %s", errorCode.getMessage(), message);
        }

    }
}

