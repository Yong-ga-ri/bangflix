package com.swcamp9th.bangflixbackend.email.service;

import com.swcamp9th.bangflixbackend.domain.user.dto.EmailCodeRequestDto;
import com.swcamp9th.bangflixbackend.shared.exception.InvalidEmailCodeException;
import com.swcamp9th.bangflixbackend.security.service.RedisService;
import io.lettuce.core.RedisException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final RedisService redisService;

    @Value("${mail.username}")
    private static String senderEmail;

    @Transactional
    public String createNumber() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 8; i++) { // 인증 코드 8자리
            int index = random.nextInt(3); // 0~2까지 랜덤, 랜덤값으로 switch문 실행

            switch (index) {
                case 0 -> key.append((char) (random.nextInt(26) + 97)); // 소문자
                case 1 -> key.append((char) (random.nextInt(26) + 65)); // 대문자
                case 2 -> key.append(random.nextInt(10)); // 숫자
            }
        }
        return key.toString();
    }

    @Transactional
    public MimeMessage createMail(String mail, String number) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject("이메일 인증");
        String body = "";
        body += "<h3>요청하신 인증 번호입니다.</h3>";
        body += "<h1>" + number + "</h1>";
        body += "<h3>감사합니다.</h3>";
        message.setText(body, "UTF-8", "html");

        return message;
    }

    // 메일 발송
    @Transactional
    public String sendSimpleMessage(String sendEmail) throws MessagingException {
        String number = createNumber(); // 랜덤 인증번호 생성

        MimeMessage message = createMail(sendEmail, number); // 메일 생성
        try {
            javaMailSender.send(message); // 메일 발송
        } catch (MailException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("메일 발송 중 오류가 발생했습니다.");
        }

        // 레디스에 저장
        redisService.saveEmailCode(sendEmail, number);

        return number; // 생성된 인증번호 반환
    }

    @Transactional
    public boolean findEmailCode(EmailCodeRequestDto emailCodeRequestDto) {
        String redisEmailCode;
        try {
            // Redis에서 이메일 코드 조회
            redisEmailCode = redisService.getEmailCode(emailCodeRequestDto.getEmail());

            if (redisEmailCode == null) {
                throw new InvalidEmailCodeException("이메일 인증 코드가 존재하지 않거나 만료되었습니다");
            }

            // Redis에서 해당 이메일 코드 삭제
//            redisService.deleteEmailCode(emailCodeRequestDto.getEmail());
        } catch (RedisException e) {
            throw new InvalidEmailCodeException("Redis에서 이메일 코드가 정상적으로 삭제되지 않았습니다");
        } catch (Exception e) {
            // 일반적인 예외 처리
            throw new InvalidEmailCodeException("이메일 인증 과정에서 문제가 발생했습니다");
        }

        // 입력받은 코드와 Redis에서 조회된 코드를 비교
        return redisEmailCode.equals(emailCodeRequestDto.getCode());
    }

}
