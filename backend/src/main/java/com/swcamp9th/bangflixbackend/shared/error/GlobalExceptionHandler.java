package com.swcamp9th.bangflixbackend.shared.error;

import com.swcamp9th.bangflixbackend.domain.user.exception.ExpiredTokenException;
import com.swcamp9th.bangflixbackend.shared.error.exception.AuthenticationException;
import com.swcamp9th.bangflixbackend.shared.error.exception.BusinessException;
import io.jsonwebtoken.JwtException;
import io.lettuce.core.RedisException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * MethodArgumentNotValidException에서 발생한 모든 검증 에러 메시지를 추출합니다.
     *
     * @param e MethodArgumentNotValidException 검증 에러를 포함하는 예외 객체
     * @return 에러 메시지를 쉼표(,)로 구분한 문자열
     */
    private static String getValidationErrorMessages(MethodArgumentNotValidException e) {
        return e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
    }

    /**
     * 메소드 인자 검증 실패로 발생하는 예외를 처리합니다. <br>
     * 에러 로그를 남기고, BAD_REQUEST 상태의 ErrorResponse를 반환합니다.
     *
     * @param e MethodArgumentNotValidException 인스턴스
     * @return 에러 응답을 포함한 ResponseEntity
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("Validation error: ", e);

        String errorMessage = getValidationErrorMessages(e);
        return ResponseEntity.status(ErrorCode.BAD_REQUEST.getStatus())
                .body(ErrorResponse.of(ErrorCode.BAD_REQUEST, errorMessage));
    }

    /**
     * HTTP 요청 파싱 및 파라미터 바인딩 과정에서 발생하는 예외를 처리합니다. <br>
     * 에러 로그를 남기고, BAD_REQUEST 상태의 ErrorResponse를 반환합니다.
     *
     * @param e HttpMessageNotReadableException 또는 MissingServletRequestParameterException 인스턴스
     * @return 에러 응답을 포함한 ResponseEntity
     */
    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequestExceptions(Exception e) {
        log.error("Bad request: ", e);

        return ResponseEntity.status(ErrorCode.BAD_REQUEST.getStatus())
                .body(ErrorResponse.of(ErrorCode.BAD_REQUEST));
    }

    /**
     * 비즈니스 로직 실행 중 발생하는 예외를 처리합니다. <br>
     * 에러 로그를 남기고, 예외에 포함된 ErrorCode를 기반으로 ErrorResponse를 반환합니다.
     *
     * @param e BusinessException 인스턴스
     * @return 에러 응답을 포함한 ResponseEntity
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.error("Business Error: ", e);

        final ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode));
    }

    /**
     * 사용자 인증 및 권한 관련 예외를 처리합니다. <br>
     * 에러 로그를 남기고, 인증 관련 에러 상태와 메시지를 포함한 ErrorResponse를 반환합니다.
     *
     * @param e ExpiredTokenException 또는 JwtException 인스턴스 (AuthenticationException의 하위 타입)
     * @return 에러 응답을 포함한 ResponseEntity
     */
    @ExceptionHandler({
            ExpiredTokenException.class,
            JwtException.class
    })
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e) {
        log.error("Authentication error: ", e);

        final ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode, errorCode.getMessage()));
    }

    /**
     * 내부 서버 에러와 관련된 예외를 처리합니다. <br>
     * 외부 서비스 오류나 일반적인 런타임 예외에 대해 에러 로그를 남기고, <br>
     * INTERNAL_SERVER_ERROR 상태의 ErrorResponse를 반환합니다.
     *
     * @param e MailSendException, RedisException, IOException, NullPointerException,
     *          IllegalArgumentException, IndexOutOfBoundsException, UnsupportedOperationException,
     *          IllegalStateException, ArithmeticException 중 하나의 RuntimeException 인스턴스
     * @return 에러 응답을 포함한 ResponseEntity
     */
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
    public ResponseEntity<ErrorResponse> handleInternalServerErrorException(RuntimeException e) {
        log.error("internal server error: ", e);

        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    /**
     * 처리되지 않은 모든 예외에 대한 최종 핸들러입니다. <br>
     * 에러 로그를 남기고, INTERNAL_SERVER_ERROR 상태의 ErrorResponse를 반환합니다.
     *
     * @param e 처리되지 않은 예외 객체
     * @return 에러 응답을 포함한 ResponseEntity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUnhandledExceptions(Exception e) {
        log.error("unhandled exception: ", e);

        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
