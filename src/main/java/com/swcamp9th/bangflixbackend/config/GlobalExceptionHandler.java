package com.swcamp9th.bangflixbackend.config;

import com.swcamp9th.bangflixbackend.common.ResponseMessage;
import com.swcamp9th.bangflixbackend.exception.*;
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
        InvalidEmailCodeException.class
    })
    public ResponseEntity<ResponseMessage<Object>> handleBadRequestException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ResponseMessage<>(400, e.getMessage(), null));
    }

    // 401: 지정한 리소스에 대한 권한이 없다
    @ExceptionHandler({
        InvalidUserException.class,
        LoginException.class,
        ExpiredTokenExcepiton.class,
        JwtException.class
    })
    public ResponseEntity<ResponseMessage<Object>> handleInvalidUserException(Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ResponseMessage<>(401, e.getMessage(), null));
    }

    // 500: 내부 서버 에러
    @ExceptionHandler({
        MailSendException.class,
        RedisException.class,
        IOException.class
    })
    public ResponseEntity<ResponseMessage<Object>> handleInternalServerErrorException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseMessage<>(500, e.getMessage(), null));
    }
}
