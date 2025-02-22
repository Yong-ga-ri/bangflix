package com.swcamp9th.bangflixbackend.shared.error;

import com.swcamp9th.bangflixbackend.domain.user.exception.ExpiredTokenException;
import com.swcamp9th.bangflixbackend.shared.error.exception.BusinessException;
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

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseMessage<Object>> handleBusinessException(BusinessException e) {
        final ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus())
                .body(new ResponseMessage<>(errorCode.getStatus(), errorCode.getMessage(), null));
    }

    // 401: 지정한 리소스에 대한 권한이 없다
    @ExceptionHandler({
            ExpiredTokenException.class,
            JwtException.class
    })
    public ResponseEntity<ResponseMessage<Object>> handleInvalidUserException(Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ResponseMessage<>(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), null));
    }

    // 500: 내부 서버 에러
    @ExceptionHandler({
            MailSendException.class,
            RedisException.class,
            IOException.class,
            NullPointerException.class,
            IllegalArgumentException.class,
            IndexOutOfBoundsException.class,
            UnsupportedOperationException.class,
            IllegalStateException.class,
            ArithmeticException.class,
    })
    public ResponseEntity<ResponseMessage<Object>> handleInternalServerErrorException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseMessage<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null));
    }
}
