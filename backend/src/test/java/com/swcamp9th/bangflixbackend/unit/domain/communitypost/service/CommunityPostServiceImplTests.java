package com.swcamp9th.bangflixbackend.unit.domain.communitypost.service;

import com.swcamp9th.bangflixbackend.domain.communitypost.dto.CommunityPostCreateDTO;
import com.swcamp9th.bangflixbackend.domain.communitypost.dto.CommunityPostDTO;
import com.swcamp9th.bangflixbackend.domain.communitypost.dto.CommunityPostUpdateDTO;
import com.swcamp9th.bangflixbackend.domain.communitypost.entity.CommunityPost;
import com.swcamp9th.bangflixbackend.domain.communitypost.repository.CommunityFileRepository;
import com.swcamp9th.bangflixbackend.domain.communitypost.repository.CommunityLikeRepository;
import com.swcamp9th.bangflixbackend.domain.communitypost.repository.CommunityPostRepository;
import com.swcamp9th.bangflixbackend.domain.communitypost.service.CommunityPostServiceImpl;
import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import com.swcamp9th.bangflixbackend.domain.user.exception.MemberNotFoundException;
import com.swcamp9th.bangflixbackend.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommunityPostServiceImplTests {

    @Mock
    private CommunityPostRepository communityPostRepository;

    @Mock
    private CommunityFileRepository communityFileRepository;

    @Mock
    private CommunityLikeRepository communityLikeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CommunityPostServiceImpl communityPostService;

    private Member mockMember;
    private CommunityPost mockPost;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockMember = new Member();
        mockMember.setId("user123");
        mockMember.setMemberCode(1);

        mockPost = new CommunityPost();
        mockPost.setCommunityPostCode(100);
        mockPost.setTitle("Test Title");
        mockPost.setContent("Test Content");
        mockPost.setCreatedAt(LocalDateTime.now());
        mockPost.setActive(true);
        mockPost.setMember(mockMember);
    }

    /**
     * 게시글 생성 테스트
     */
    @Test
    void createPost_ShouldSavePost_WhenValidRequest() {
        // Given
        CommunityPostCreateDTO request = new CommunityPostCreateDTO();
        request.setTitle("New Post");
        request.setContent("New Content");

        when(userRepository.findById("user123")).thenReturn(Optional.of(mockMember));
        when(modelMapper.map(request, CommunityPost.class)).thenReturn(mockPost);
        when(communityPostRepository.save(any(CommunityPost.class))).thenReturn(mockPost);

        // When
        communityPostService.createPost("user123", request, null);

        // Then
        verify(communityPostRepository, times(1)).save(any(CommunityPost.class));
    }

    /**
     * 게시글 생성 실패 - 회원가입되지 않은 사용자
     */
    @Test
    void createPost_ShouldThrowException_WhenUserNotFound() {
        // Given
        CommunityPostCreateDTO request = new CommunityPostCreateDTO();
        request.setTitle("New Post");
        request.setContent("New Content");

        when(userRepository.findById("user123")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> communityPostService.createPost("user123", request, null))
                .isInstanceOf(MemberNotFoundException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    /**
     * 게시글 수정 테스트
     */
    @Test
    void updatePost_ShouldUpdatePost_WhenValidRequest() {
        // Given
        CommunityPostUpdateDTO updateDTO = new CommunityPostUpdateDTO();
        updateDTO.setTitle("Updated Title");
        updateDTO.setContent("Updated Content");

        when(communityPostRepository.findById(100)).thenReturn(Optional.of(mockPost));
        when(userRepository.findById("user123")).thenReturn(Optional.of(mockMember));

        // When
        communityPostService.updatePost("user123", 100, updateDTO, null);

        // Then
        assertThat(mockPost.getTitle()).isEqualTo("Updated Title");
        assertThat(mockPost.getContent()).isEqualTo("Updated Content");
        verify(communityPostRepository, times(1)).save(mockPost);
    }

    /**
     * 게시글 삭제 테스트
     */
    @Test
    void deletePost_ShouldDeactivatePost_WhenValidRequest() {
        // Given
        when(communityPostRepository.findById(100)).thenReturn(Optional.of(mockPost));
        when(userRepository.findById("user123")).thenReturn(Optional.of(mockMember));

        // When
        communityPostService.deletePost("user123", 100);

        // Then
        assertThat(mockPost.getActive()).isFalse();
        verify(communityPostRepository, times(1)).save(mockPost);
    }

    /**
     * 게시글 삭제 실패 - 존재하지 않는 게시글
     */
    @Test
    void deletePost_ShouldThrowException_WhenPostNotFound() {
        // Given
        when(communityPostRepository.findById(100)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> communityPostService.deletePost("user123", 100))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("존재하지 않는 게시글입니다.");
    }

    /**
     * 모든 게시글 조회
     */
    @Test
    void getAllPosts_ShouldReturnPosts_WhenUserExists() {
        // Given
        when(userRepository.findById("user123")).thenReturn(Optional.of(mockMember));
        when(communityPostRepository.findByActiveTrue((Sort) any()))
                .thenReturn(List.of(mockPost));

        CommunityPostDTO mockPostDTO = new CommunityPostDTO();
        mockPostDTO.setTitle("Test Title");
        mockPostDTO.setContent("Test Content");

        when(modelMapper.map(mockPost, CommunityPostDTO.class)).thenReturn(mockPostDTO);

        // When
        List<CommunityPostDTO> posts = communityPostService.getAllPosts("user123");

        // Then
        assertThat(posts).hasSize(1);
        assertThat(posts.get(0).getTitle()).isEqualTo("Test Title");
    }

    /**
     * 특정 게시글 조회
     */
    @Test
    void findPostByCode_ShouldReturnPost_WhenPostExists() {
        // Given
        when(communityPostRepository.findById(100)).thenReturn(Optional.of(mockPost));
        when(userRepository.findById("user123")).thenReturn(Optional.of(mockMember));

        CommunityPostDTO mockPostDTO = new CommunityPostDTO();
        mockPostDTO.setTitle("Test Title");

        when(modelMapper.map(mockPost, CommunityPostDTO.class)).thenReturn(mockPostDTO);

        // When
        CommunityPostDTO post = communityPostService.findPostByCode("user123", 100);

        // Then
        assertThat(post.getTitle()).isEqualTo("Test Title");
    }
}
