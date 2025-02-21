package com.swcamp9th.bangflixbackend.shared.error.handler;

import com.swcamp9th.bangflixbackend.shared.error.*;
import com.swcamp9th.bangflixbackend.shared.error.exception.BusinessException;
import com.swcamp9th.bangflixbackend.shared.error.exception.FileUploadException;
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
            DuplicateException.class,
            InvalidEmailCodeException.class,
            LoginException.class,
            MemberNotFoundException.class,
            ReviewNotFoundException.class,
            FileUploadException.class
    })
    public ResponseEntity<ResponseMessage<Object>> handleBadRequestException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ResponseMessage<>(HttpStatus.BAD_REQUEST.value(), e.getMessage(), null));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseMessage<Object>> handleBusinessException(BusinessException e) {
        final ErrorCode errorCode = e.getErrorCode();
//        final ErrorResponse response = ErrorResponse.of(errorCode, e.getErrors());
        return ResponseEntity.status(errorCode.getStatus())
                .body(new ResponseMessage<>(errorCode.getStatus(), errorCode.getMessage(), null));

//        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 401: 지정한 리소스에 대한 권한이 없다
    @ExceptionHandler({
            InvalidUserException.class,
            ExpiredTokenExcepiton.class,
            JwtException.class
    })
    public ResponseEntity<ResponseMessage<Object>> handleInvalidUserException(Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(new ResponseMessage<>(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), null));
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
            ArithmeticException.class,
    })
    public ResponseEntity<ResponseMessage<Object>> handleInternalServerErrorException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseMessage<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage(), null));
    }
}
