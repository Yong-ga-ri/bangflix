package com.swcamp9th.bangflixbackend.domain.user.controller;

import com.swcamp9th.bangflixbackend.shared.response.ResponseCode;
import com.swcamp9th.bangflixbackend.shared.response.SuccessResponse;
import com.swcamp9th.bangflixbackend.domain.user.dto.*;
import com.swcamp9th.bangflixbackend.domain.user.service.UserServiceImpl;
import com.swcamp9th.bangflixbackend.domain.user.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestPart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {

    private final UserServiceImpl userService;
    private final EmailService emailService;

    @PostMapping(value = "/signup", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "회원가입 API")
    public ResponseEntity<SuccessResponse<Void>> signup(@Valid @RequestPart(value = "signupDto") SignupRequestDto signupRequestDto, @RequestPart(value = "imgFile", required = false) MultipartFile imgFile) throws IOException {

        if (imgFile == null) {
            userService.signupWithoutProfile(signupRequestDto);
        } else {
            userService.signup(signupRequestDto, imgFile);
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.empty(ResponseCode.CREATED));
    }

    @PostMapping("/login")
    @Operation(summary = "로그인 API")
    public ResponseEntity<SuccessResponse<SignResponseDto>> login(@Valid @RequestBody SignRequestDto signRequestDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(ResponseCode.OK, userService.login(signRequestDto)));
    }

    @PostMapping("/refresh")
    @Operation(summary = "엑세스 토큰 재발급 API")
    public ResponseEntity<SuccessResponse<ReissueTokenResponseDto>> refresh(@Valid @RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(ResponseCode.OK, userService.refreshTokens(refreshTokenRequestDto.getRefreshToken())));
    }

    @PostMapping("/confirm-id")
    @Operation(summary = "아이디 중복체크 API")
    public ResponseEntity<SuccessResponse<DuplicateCheckResponseDto>> confirmId(@Valid @RequestBody ConfirmIdRequestDto confirmIdRequestDto) {
        DuplicateCheckResponseDto result = userService.findId(confirmIdRequestDto.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(ResponseCode.OK, "사용 가능한 아이디입니다", result));
    }

    @PostMapping("/confirm-nickname")
    @Operation(summary = "닉네임 중복체크 API")
    public ResponseEntity<SuccessResponse<DuplicateCheckResponseDto>> confirmNickname(@Valid @RequestBody ConfirmNicknameRequestDto confirmNicknameRequestDto) {
        DuplicateCheckResponseDto result = userService.findNickName(confirmNicknameRequestDto.getNickname());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(ResponseCode.OK, "사용 가능한 아이디입니다", result));
    }

    @PostMapping("/send-email")
    @Operation(summary = "인증 이메일 발송 API")
    public ResponseEntity<SuccessResponse<Void>> sendEmail(@RequestBody EmailRequestDto emailRequestDto) {
        emailService.sendSimpleMessage(emailRequestDto.getEmail());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.empty(ResponseCode.CREATED, "인증 이메일 발송에 성공했습니다"));
    }

    @PostMapping("/confirm-email")
    @Operation(summary = "인증 이메일 검증 API")
    public ResponseEntity<SuccessResponse<Void>> confirmEmail(@RequestBody EmailCodeRequestDto emailCodeRequestDto) {
        emailService.findEmailCode(emailCodeRequestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.empty(ResponseCode.CREATED, "이메일 인증에 성공했습니다"));

    }
}