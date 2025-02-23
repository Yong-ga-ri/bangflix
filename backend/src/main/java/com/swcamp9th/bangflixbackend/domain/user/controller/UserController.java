package com.swcamp9th.bangflixbackend.domain.user.controller;

import com.swcamp9th.bangflixbackend.shared.response.ResponseCode;
import com.swcamp9th.bangflixbackend.shared.response.SuccessResponse;
import com.swcamp9th.bangflixbackend.domain.user.dto.*;
import com.swcamp9th.bangflixbackend.domain.user.service.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import static com.swcamp9th.bangflixbackend.shared.filter.RequestFilter.SERVLET_REQUEST_ATTRIBUTE_KEY;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {

    private final UserServiceImpl userService;

    @PostMapping("")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "로그아웃 API")
    public ResponseEntity<SuccessResponse<Void>> logout(
            @Valid @RequestBody RefreshTokenRequestDto refreshTokenRequestDto
    ) {
        userService.logout(refreshTokenRequestDto.getRefreshToken());

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(SuccessResponse.empty(ResponseCode.DELETED, "로그아웃 성공"));
    }

    @GetMapping("/info")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "회원 정보 조회(아이디, 닉네임, 이메일, 프로필 이미지) API")
    public ResponseEntity<SuccessResponse<UserInfoResponseDto>> findUserInfoById(
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String userId
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(ResponseCode.OK, userService.findUserInfoById(userId)));
    }

    @PutMapping(value = "/info", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "회원 정보 수정(닉네임, 이메일, 프로필 이미지) API")
    public ResponseEntity<SuccessResponse<Void>> updateUserInfo(
            @Valid @RequestPart UpdateUserInfoRequestDto updateUserInfoRequestDto,
            @RequestPart(value = "imgFile", required = false) MultipartFile imgFile,
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String userId
    ) {
        userService.updateUserInfo(userId, updateUserInfoRequestDto, imgFile);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(SuccessResponse.empty(ResponseCode.UPDATED));
    }

    @GetMapping("mypage")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "마이페이지 회원 정보 조회(닉네임, 포인트, 프로필 이미지) API")
    public ResponseEntity<SuccessResponse<MyPageResponseDto>> findMyPageInfoById(@RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(ResponseCode.OK, userService.findMyPageInfoById(userId)));
    }
}