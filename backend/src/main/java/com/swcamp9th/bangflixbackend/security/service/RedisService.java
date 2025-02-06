package com.swcamp9th.bangflixbackend.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String EMAIL_PREFIX = "REGISTER:";
    private static final String TOKEN_PREFIX = "TOKEN:";

    @Value("${refresh-token.expiration_time}")
    private Long refreshTokenExpireTime;

    @Value("${mail.expiration_time}")
    private Long emailCodeExpireTime;

    public void saveRefreshToken(String id, String refreshToken) {
        redisTemplate.opsForValue().set(TOKEN_PREFIX + id, refreshToken, refreshTokenExpireTime, TimeUnit.MILLISECONDS);
    }

    public String getRefreshToken(String id) {
        return redisTemplate.opsForValue().get(TOKEN_PREFIX + id);
    }

    public void deleteRefreshToken(String id) {
        redisTemplate.delete(TOKEN_PREFIX + id);
    }

    // 리프레시 토큰 유효성 검사
    public boolean isRefreshTokenValid(String id, String refreshToken) {
        String storedRefreshToken = redisTemplate.opsForValue().get(TOKEN_PREFIX + id);
        return refreshToken != null && refreshToken.equals(storedRefreshToken);
    }

    // 이메일 인증번호 저장
    public void saveEmailCode(String email, String number) {
        redisTemplate.opsForValue().set(EMAIL_PREFIX + email, number, emailCodeExpireTime, TimeUnit.MILLISECONDS);
    }

    public String getEmailCode(String email) {
        return redisTemplate.opsForValue().get(EMAIL_PREFIX + email);
    }

    public void deleteEmailCode(String email) {
        redisTemplate.delete(EMAIL_PREFIX + email);
    }

}