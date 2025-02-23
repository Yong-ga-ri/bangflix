package com.swcamp9th.bangflixbackend.shared.error;

import com.swcamp9th.bangflixbackend.domain.user.exception.ExpiredTokenException;
import com.swcamp9th.bangflixbackend.shared.error.exception.BusinessException;
import io.jsonwebtoken.JwtException;
import io.lettuce.core.RedisException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        final ErrorCode errorCode = e.getErrorCode();
        log.error(errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode));
    }

    @ExceptionHandler({
            ExpiredTokenException.class,
            JwtException.class
    })
    public ResponseEntity<ErrorResponse> handleInvalidUserException(Exception e) {
        return ResponseEntity.status(ErrorCode.INVALID_USER.getStatus())
            .body(ErrorResponse.of(ErrorCode.INVALID_USER, e.getMessage()));
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
    public ResponseEntity<ErrorResponse> handleInternalServerErrorException(Exception e) {
        log.error(e.getMessage());
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
