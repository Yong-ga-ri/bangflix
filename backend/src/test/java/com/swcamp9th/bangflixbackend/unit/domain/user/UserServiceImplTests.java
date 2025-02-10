package com.swcamp9th.bangflixbackend.unit.domain.user;

import com.swcamp9th.bangflixbackend.domain.user.dto.UserInfoResponseDto;
import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import com.swcamp9th.bangflixbackend.domain.user.repository.UserRepository;
import com.swcamp9th.bangflixbackend.domain.user.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userInfoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findUserInfoById_ExistingUser() {
        // give
        String userId = "testUser123";
        Member mockUser = new Member();
        mockUser.setId(userId);
        mockUser.setNickname("TestNickname");
        mockUser.setIsAdmin(false);
        mockUser.setImage("test-image.jpg");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // when
        UserInfoResponseDto result = userInfoService.findUserInfoById(userId);

        // then
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("TestNickname", result.getNickname());
        assertFalse(result.isAdmin());
        assertEquals("test-image.jpg", result.getImage());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void findUserInfoById_NonExistingUser() {
        // give
        String userId = "nonExistingUser";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> userInfoService.findUserInfoById(userId));

        // then
        assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }
}