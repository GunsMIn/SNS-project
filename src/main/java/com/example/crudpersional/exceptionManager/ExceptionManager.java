package com.example.crudpersional.exceptionManager;

import com.example.crudpersional.domain.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RestControllerAdvice
public class ExceptionManager {
    /**UserException**/
    @ExceptionHandler(UserException.class)
    public ResponseEntity<?> userAppExceptionHandler(UserException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error("ERROR", errorResponse));

    }

    /**PostException**/
    @ExceptionHandler(PostException.class)
    public ResponseEntity<?> postAppExceptionHandler(PostException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(Response.error("ERROR", errorResponse));
    }

    /**LikeException**/
   @ExceptionHandler(LikeException.class)
    public ResponseEntity<?> likeExceptionHandler(LikeException e) {
       log.info("에러 :{} / {} ",e.getErrorCode(),e.getMessage());
       ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getMessage());
       return  ResponseEntity.status(e.getErrorCode().getStatus())
               .body(Response.error(new ErrorDto(e)));
    }



}