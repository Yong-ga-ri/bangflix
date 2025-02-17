package com.swcamp9th.bangflixbackend.domain.user.service;

import com.swcamp9th.bangflixbackend.domain.user.dto.*;
import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {
    SignupResponseDto signupWithoutProfile(SignupRequestDto signupRequestDto);
    SignupResponseDto signup(SignupRequestDto signupRequestDto, MultipartFile imgFile) throws IOException;
    SignResponseDto login(SignRequestDto signRequestDto);
    ReissueTokenResponseDto refreshTokens(String refreshToken);
    void logout(String refreshToken);
    UserInfoResponseDto findUserInfoById(String id);
    DuplicateCheckResponseDto findId(String id);
    DuplicateCheckResponseDto findNickName(String nickname);
    void updateUserInfo(String id, UpdateUserInfoRequestDto updateUserInfoRequestDto, MultipartFile imgFile) throws IOException;
    MyPageResponseDto findMyPageInfoById(String userId);
    int findMemberCodeByLoginId(String loginId);
    Member findMemberByLoginId(String loginId);
    void memberGetPoint(Member member, int point);
}
