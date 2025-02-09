package com.swcamp9th.bangflixbackend.unit.domain.communitypost.service;

import com.swcamp9th.bangflixbackend.domain.comment.dto.CommentDTO;
import com.swcamp9th.bangflixbackend.domain.communitypost.dto.CommunityLikeCountDTO;
import com.swcamp9th.bangflixbackend.domain.communitypost.dto.CommunityLikeCreateDTO;
import com.swcamp9th.bangflixbackend.domain.communitypost.entity.CommunityLike;
import com.swcamp9th.bangflixbackend.domain.communitypost.entity.CommunityPost;
import com.swcamp9th.bangflixbackend.domain.communitypost.repository.CommunityLikeRepository;
import com.swcamp9th.bangflixbackend.domain.communitypost.repository.CommunityPostRepository;
import com.swcamp9th.bangflixbackend.domain.communitypost.service.CommunityLikeServiceImpl;
import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import com.swcamp9th.bangflixbackend.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class CommunityLikeServiceImplTests {

    @Mock
    private CommunityLikeRepository communityLikeRepository;

    @Mock
    private CommunityPostRepository communityPostRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CommunityLikeServiceImpl communityLikeService;

    private Member mockMember;
    private CommunityPost mockPost;
    private CommunityLike mockLike;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockMember = new Member();
        mockMember.setId("user123");
        mockMember.setMemberCode(1);

        mockPost = new CommunityPost();
        mockPost.setCommunityPostCode(100);

        mockLike = new CommunityLike();
        mockLike.setCommunityPostCode(100);
        mockLike.setMemberCode(1);
        mockLike.setActive(true);
        mockLike.setCreatedAt(LocalDateTime.now());
    }

    @DisplayName("커뮤니티 게시글 좋아요")
    @Test
    void addLike_ShouldToggleLike_WhenUserExists() {
        // Given
        CommunityLikeCreateDTO request = new CommunityLikeCreateDTO();
        when(modelMapper.map(request, CommunityLike.class)).thenReturn(mockLike);
        request.setCommunityPostCode(100);

        when(userRepository.findById("user123")).thenReturn(Optional.of(mockMember));
        when(communityPostRepository.findById(100)).thenReturn(Optional.of(mockPost));
        when(communityLikeRepository.existsByMemberCodeAndCommunityPostCodeAndActiveTrue(1, 100))
                .thenReturn(true);

        // When
        communityLikeService.addLike("user123", request);

        // Then
        verify(communityLikeRepository, times(1)).save(any(CommunityLike.class));
    }

    @DisplayName("특정 커뮤니티 게시글 좋아요 수 세기")
    @Test
    void countLike_ShouldReturnCorrectCount_WhenPostExists() {
        // Given
        when(communityPostRepository.findById(100)).thenReturn(Optional.of(mockPost));
        when(communityLikeRepository.findByCommunityPostCodeAndActiveTrue(100))
                .thenReturn(List.of(mockLike));

        // When
        CommunityLikeCountDTO countDTO = communityLikeService.countLike(100);

        // Then
        assertThat(countDTO.getLikeCount()).isEqualTo(1);
    }

}
