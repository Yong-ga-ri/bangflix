package com.swcamp9th.bangflixbackend.shared.exception.handler;

import com.swcamp9th.bangflixbackend.shared.exception.*;
import com.swcamp9th.bangflixbackend.shared.response.ResponseMessage;
import io.jsonwebtoken.JwtException;
import io.lettuce.core.RedisException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 400: 잘못된 요청 예외 처리
    @ExceptionHandler({
        AlreadyLikedException.class,
        LikeNotFoundException.class,
        DuplicateException.class,
        InvalidEmailCodeException.class,
        LoginException.class,
    })
    public ResponseEntity<ResponseMessage<Object>> handleBadRequestException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ResponseMessage<>(400, e.getMessage(), null));
    }

    // 401: 지정한 리소스에 대한 권한이 없다
    @ExceptionHandler({
        InvalidUserException.class,
        ExpiredTokenExcepiton.class,
        JwtException.class
    })
    public ResponseEntity<ResponseMessage<Object>> handleInvalidUserException(Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ResponseMessage<>(401, e.getMessage(), null));
    }

//    // 500: 내부 서버 에러
    @ExceptionHandler({
        MailSendException.class,
        RedisException.class,
        IOException.class,
        NullPointerException.class,
        IllegalArgumentException.class,
        IndexOutOfBoundsException.class,
        UnsupportedOperationException.class,
        IllegalStateException.class,
        ArithmeticException.class
    })
    public ResponseEntity<ResponseMessage<Object>> handleInternalServerErrorException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseMessage<>(500, e.getMessage(), null));
    }
}
