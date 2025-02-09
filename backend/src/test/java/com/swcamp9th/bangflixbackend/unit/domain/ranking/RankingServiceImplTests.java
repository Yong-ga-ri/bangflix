package com.swcamp9th.bangflixbackend.unit.domain.ranking;

import com.swcamp9th.bangflixbackend.domain.ranking.dto.MemberRankingDTO;
import com.swcamp9th.bangflixbackend.domain.ranking.dto.ReviewRankingDTO;
import com.swcamp9th.bangflixbackend.domain.ranking.dto.ReviewRankingDateDTO;
import com.swcamp9th.bangflixbackend.domain.ranking.entity.ReviewRanking;
import com.swcamp9th.bangflixbackend.domain.ranking.repository.ReviewRankingRepository;
import com.swcamp9th.bangflixbackend.domain.ranking.service.RankingServiceImpl;
import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.entity.Review;
import com.swcamp9th.bangflixbackend.domain.review.entity.ReviewLike;
import com.swcamp9th.bangflixbackend.domain.review.repository.ReviewLikeRepository;
import com.swcamp9th.bangflixbackend.domain.review.repository.ReviewRepository;
import com.swcamp9th.bangflixbackend.domain.review.service.ReviewService;
import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import com.swcamp9th.bangflixbackend.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RankingServiceImplTests {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewLikeRepository reviewLikeRepository;

    @Mock
    private ReviewRankingRepository reviewRankingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewService reviewService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RankingServiceImpl rankingService;

    private Member mockMember;
    private Review mockReview;
    private ReviewRanking mockReviewRanking;
    private ReviewDTO mockReviewDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockMember = new Member();
        mockMember.setId("user123");
        mockMember.setMemberCode(1);
        mockMember.setPoint(100);

        mockReview = new Review();
        mockReview.setReviewCode(200);
        mockReview.setMember(mockMember);
        mockReview.setContent("Test Review");

        mockReviewRanking = new ReviewRanking();
        mockReviewRanking.setReview(mockReview);
        mockReviewRanking.setCreatedAt(LocalDateTime.now());

        mockReviewDTO = new ReviewDTO();
        mockReviewDTO.setReviewCode(200);
        mockReviewDTO.setContent("Test Review");
        mockReviewDTO.setLikes(10);
    }

    /**
     * 리뷰 랭킹 생성 테스트
     */
    @Test
    void createReviewRanking_ShouldCreateRanking_WhenValidRequest() {
        // Given
        when(reviewLikeRepository.findTop5ReviewCodes(any(LocalDateTime.class)))
                .thenReturn(List.of());

        // When
        rankingService.createReviewRanking();

        // Then
        verify(reviewLikeRepository, times(1)).findTop5ReviewCodes(any(LocalDateTime.class));
        verify(reviewRankingRepository, never()).save(any(ReviewRanking.class));
    }

    /**
     * 특정 연도의 리뷰 랭킹 날짜 조회
     */
    @Test
    void findReviewRankingDate_ShouldReturnDates_WhenYearExists() {
        // Given
        when(reviewRankingRepository.findDistinctDatesByYear(2024))
                .thenReturn(Optional.of(List.of("2024-01-07", "2024-01-14")));

        // When
        ReviewRankingDateDTO result = rankingService.findReviewRankingDate(2024);

        // Then
        assertThat(result.getReviewRankingDates()).containsExactly("2024-01-07", "2024-01-14");
    }

    /**
     * 특정 연도의 리뷰 랭킹 날짜 조회 실패
     */
    @Test
    void findReviewRankingDate_ShouldReturnNull_WhenNoDatesExist() {
        // Given
        when(reviewRankingRepository.findDistinctDatesByYear(2024))
                .thenReturn(Optional.empty());

        // When
        ReviewRankingDateDTO result = rankingService.findReviewRankingDate(2024);

        // Then
        assertThat(result).isNull();
    }

    /**
     * 특정 날짜의 리뷰 랭킹 조회
     */
    @Test
    void findReviewRanking_ShouldReturnRanking_WhenDateExists() {
        // Given
        when(userRepository.findById("user123")).thenReturn(Optional.of(mockMember));
        when(reviewRankingRepository.findReviewByCreatedAtDate("2024-01-07"))
                .thenReturn(Optional.of(List.of(mockReviewRanking)));
        when(reviewService.getReviewDTOS(anyList(), anyInt()))
                .thenReturn(List.of(mockReviewDTO));

        ReviewRankingDTO mockReviewRankingDTO = new ReviewRankingDTO();
        mockReviewRankingDTO.setLikes(10);
        mockReviewRankingDTO.setRankingDate("2024-01-07");

        when(modelMapper.map(any(ReviewDTO.class), eq(ReviewRankingDTO.class)))
                .thenReturn(mockReviewRankingDTO);

        // When
        List<ReviewRankingDTO> rankings = rankingService.findReviewRanking("2024-01-07", "user123");

        // Then
        assertThat(rankings).hasSize(1);
        assertThat(rankings.get(0).getRankingDate()).isEqualTo("2024-01-07");
        assertThat(rankings.get(0).getLikes()).isEqualTo(10);
    }

    /**
     * 특정 날짜의 리뷰 랭킹 조회 실패
     */
    @Test
    void findReviewRanking_ShouldReturnNull_WhenNoRankingExists() {
        // Given
        when(userRepository.findById("user123")).thenReturn(Optional.of(mockMember));
        when(reviewRankingRepository.findReviewByCreatedAtDate("2024-01-07"))
                .thenReturn(Optional.of(List.of()));

        // When
        List<ReviewRankingDTO> rankings = rankingService.findReviewRanking("2024-01-07", "user123");

        // Then
        assertThat(rankings).isNull();
    }

    /**
     * 전체 리뷰 랭킹 조회 (페이징)
     */
    @Test
    void findAllReviewRanking_ShouldReturnPagedRanking_WhenValidRequest() {
        // Given
        Pageable pageable = PageRequest.of(0, 5);
        Page<ReviewLike> pagedReviewLikes = new PageImpl<>(List.of());

        when(userRepository.findById("user123")).thenReturn(Optional.of(mockMember));
        when(reviewLikeRepository.findReviewByReviewLikes(pageable)).thenReturn(pagedReviewLikes);
        when(reviewService.getReviewDTOS(anyList(), anyInt())).thenReturn(List.of(new ReviewDTO()));

        // When
        List<ReviewDTO> rankings = rankingService.findAllReviewRanking(pageable, "user123");

        // Then
        assertThat(rankings).isNotEmpty();
    }

    /**
     * 전체 회원 랭킹 조회 (페이징)
     */
    @Test
    void findAllMemberRanking_ShouldReturnPagedMemberRanking_WhenValidRequest() {
        // Given
        Pageable pageable = PageRequest.of(0, 5);
        when(reviewRankingRepository.findTopRankingMember(pageable))
                .thenReturn(List.of(mockMember));

        MemberRankingDTO mockMemberRankingDTO = new MemberRankingDTO();
        when(modelMapper.map(any(Member.class), eq(MemberRankingDTO.class)))
                .thenReturn(mockMemberRankingDTO);

        // When
        List<MemberRankingDTO> rankings = rankingService.findAllMemberRanking(pageable);

        // Then
        assertThat(rankings).isNotEmpty();
    }
}
