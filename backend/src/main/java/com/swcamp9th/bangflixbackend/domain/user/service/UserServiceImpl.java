package com.swcamp9th.bangflixbackend.domain.user.service;

import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import com.swcamp9th.bangflixbackend.domain.user.dto.*;
import com.swcamp9th.bangflixbackend.domain.user.exception.DuplicateException;
import com.swcamp9th.bangflixbackend.domain.user.exception.MemberNotFoundException;
import com.swcamp9th.bangflixbackend.domain.user.exception.PasswordNotMatchedException;
import com.swcamp9th.bangflixbackend.domain.user.repository.UserRepository;
import com.swcamp9th.bangflixbackend.shared.error.ErrorCode;
import com.swcamp9th.bangflixbackend.domain.user.exception.ExpiredTokenException;
import com.swcamp9th.bangflixbackend.security.service.RedisService;
import com.swcamp9th.bangflixbackend.shared.error.exception.FileUploadException;
import com.swcamp9th.bangflixbackend.shared.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    @Override
    @Transactional
    public SignupResponseDto signupWithoutProfile(SignupRequestDto signupRequestDto) {
        if (userRepository.existsById(signupRequestDto.getId())) {
            throw new DuplicateException(ErrorCode.DUPLICATE_ID);
        } else if (userRepository.existsByNickname(signupRequestDto.getNickname())) {
            throw new DuplicateException(ErrorCode.DUPLICATE_NICKNAME);
        } else if (userRepository.existsByEmail(signupRequestDto.getEmail())) {
            throw new DuplicateException(ErrorCode.DUPLICATE_EMAIL);
        }

        Member user = Member.builder()
                .id(signupRequestDto.getId())
                .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                .nickname(signupRequestDto.getNickname())
                .email(signupRequestDto.getEmail())
                .isAdmin(signupRequestDto.getIsAdmin())
                .image(null)
                .build();

        userRepository.save(user);

        return new SignupResponseDto(user);
    }

    @Override
    @Transactional
    public SignupResponseDto signup(SignupRequestDto signupRequestDto, MultipartFile imgFile) {
        if (userRepository.existsById(signupRequestDto.getId())) {
            throw new DuplicateException(ErrorCode.DUPLICATE_ID);
        } else if (userRepository.existsByNickname(signupRequestDto.getNickname())) {
            throw new DuplicateException(ErrorCode.DUPLICATE_NICKNAME);
        } else if (userRepository.existsByEmail(signupRequestDto.getEmail())) {
            throw new DuplicateException(ErrorCode.DUPLICATE_EMAIL);
        }

        String uploadsDir = "src/main/resources/static/uploadFiles/profileFile";

        String fileName = UUID.randomUUID().toString().replace("-", "") + "_" + imgFile.getOriginalFilename();
        String filePath = uploadsDir + "/" + fileName;
        String dbFilePath = "/uploadFiles/profileFile/" + fileName;

        Path path = Paths.get(filePath);
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, imgFile.getBytes());
        } catch (IOException e) {
            throw new FileUploadException();
        }

        Member user = Member.builder()
                .id(signupRequestDto.getId())
                .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                .nickname(signupRequestDto.getNickname())
                .email(signupRequestDto.getEmail())
                .isAdmin(signupRequestDto.getIsAdmin())
                .image(dbFilePath)
                .build();

        userRepository.save(user);

        return new SignupResponseDto(user);
    }

    @Override
    @Transactional
    public SignResponseDto login(SignRequestDto signRequestDto) {
        Member user = userRepository.findById(signRequestDto.getId())
                .orElseThrow(MemberNotFoundException::new);

        if (!passwordEncoder.matches(signRequestDto.getPassword(), user.getPassword())) {
            throw new PasswordNotMatchedException();
        }

        String accessToken = jwtUtil.createAccessToken(user);
        String refreshToken = jwtUtil.createRefreshToken(user);

        redisService.saveRefreshToken(user.getId(), refreshToken);

        return new SignResponseDto(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public ReissueTokenResponseDto refreshTokens(String refreshToken) {
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new ExpiredTokenException(ErrorCode.TOKEN_INVALID.getMessage());
        }

        Claims claims = jwtUtil.getRefreshTokenClaims(refreshToken);
        String id = claims.getSubject();

        Member user = userRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        // Redis에서 리프레시 토큰 조회
        if (!redisService.isRefreshTokenValid(id, refreshToken)) {
            throw new ExpiredTokenException(ErrorCode.TOKEN_INVALID.getMessage());
        }

        String newAccessToken = jwtUtil.createAccessToken(user);
        return new ReissueTokenResponseDto(newAccessToken);
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new ExpiredTokenException(ErrorCode.TOKEN_INVALID.getMessage());
        }

        Claims claims = jwtUtil.getRefreshTokenClaims(refreshToken);
        String username = claims.getSubject();

        userRepository.findById(username)
                .orElseThrow(MemberNotFoundException::new);

        // Redis에서 리프레시 토큰 삭제
        redisService.deleteRefreshToken(username);
    }

    @Override
    @Transactional(readOnly = true)
    public UserInfoResponseDto findUserInfoById(String id) {
        Member user = userRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        return new UserInfoResponseDto(
                user.getId(),
                user.getNickname(),
                user.getIsAdmin(),
                user.getImage()
        );
    }

    @Override
    @Transactional
    public DuplicateCheckResponseDto findId(String id) {
        if (id.trim().isEmpty()) {
            return new DuplicateCheckResponseDto(false);
        }
        return new DuplicateCheckResponseDto(userRepository.existsById(id));
    }

    @Override
    @Transactional
    public DuplicateCheckResponseDto findNickName(String nickname) {
        if (nickname.trim().isEmpty()) {
            return new DuplicateCheckResponseDto(false);
        }
        return new DuplicateCheckResponseDto(userRepository.existsByNickname(nickname));
    }

    @Override
    @Transactional
    public void updateUserInfo(String id, UpdateUserInfoRequestDto updateUserInfoRequestDto, MultipartFile imgFile) {
        if (userRepository.existsByNickname(updateUserInfoRequestDto.getNickname())) {
            throw new DuplicateException(ErrorCode.DUPLICATE_NICKNAME);
        }

        if (userRepository.existsByEmail(updateUserInfoRequestDto.getEmail())) {
            throw new DuplicateException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 수정할 멤버 찾기
        Member user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // // 프로필 파일이 수정된 경우
        if (imgFile.getOriginalFilename() != user.getImage().substring(user.getImage().lastIndexOf("/") + 1)) {

            // 이전 프로필 이미지 삭제
            String oldFileName = user.getImage();
            if (oldFileName != null && !oldFileName.isEmpty()) {
                String oldFilePath = "src/main/resources/static" + oldFileName;
                Path oldPath = Paths.get(oldFilePath);
                try {
                    Files.deleteIfExists(oldPath); // 파일이 존재하면 삭제
                } catch (IOException e) {
                    throw new FileUploadException();
                }
            }

            // 파일 경로 및 이름
            String uploadsDir = "src/main/resources/static/uploadFiles/profileFile";
            String newFileName = UUID.randomUUID().toString().replace("-", "") + "_" + imgFile.getOriginalFilename();
            String filePath = uploadsDir + "/" + newFileName;
            String dbFilePath = "/uploadFiles/profileFile/" + newFileName;

            Path path = Paths.get(filePath);
            try {
                Files.createDirectories(path.getParent());
                Files.write(path, imgFile.getBytes());
            } catch (IOException e) {
                throw new FileUploadException();
            }
            user.setImage(dbFilePath);
        }

        user.setEmail(updateUserInfoRequestDto.getEmail());
        user.setNickname(updateUserInfoRequestDto.getNickname());

        userRepository.save(user);
    }

    public MyPageResponseDto findMyPageInfoById(String id) {
        Member user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return new MyPageResponseDto(
                user.getNickname(),
                user.getPoint(),
                user.getImage()
        );
    }

    @Override
    public int findMemberCodeByLoginId(String loginId) {
        return userRepository.findById(loginId)
                .orElseThrow(MemberNotFoundException::new)
                .getMemberCode();
    }

    @Override
    public Member findMemberByLoginId(String loginId) {
        return userRepository.findById(loginId)
                .orElseThrow(MemberNotFoundException::new);
    }

    @Override
    public void memberGetPoint(Member member, int point) {
        member.gainPoint(point);
        userRepository.save(member);
    }
}