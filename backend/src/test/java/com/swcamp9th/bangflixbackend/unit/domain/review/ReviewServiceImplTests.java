package com.swcamp9th.bangflixbackend.unit.domain.review;

import com.swcamp9th.bangflixbackend.domain.review.dto.*;
import com.swcamp9th.bangflixbackend.domain.review.entity.Review;
import com.swcamp9th.bangflixbackend.domain.review.entity.ReviewLike;
import com.swcamp9th.bangflixbackend.domain.review.repository.ReviewFileRepository;
import com.swcamp9th.bangflixbackend.domain.review.repository.ReviewLikeRepository;
import com.swcamp9th.bangflixbackend.domain.review.repository.ReviewRepository;
import com.swcamp9th.bangflixbackend.domain.review.repository.ReviewTendencyGenreRepository;
import com.swcamp9th.bangflixbackend.domain.review.service.ReviewServiceImpl;
import com.swcamp9th.bangflixbackend.domain.theme.entity.Theme;
import com.swcamp9th.bangflixbackend.domain.theme.repository.ThemeRepository;
import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import com.swcamp9th.bangflixbackend.domain.user.repository.UserRepository;
import com.swcamp9th.bangflixbackend.shared.exception.AlreadyLikedException;
import com.swcamp9th.bangflixbackend.shared.exception.InvalidUserException;
import com.swcamp9th.bangflixbackend.shared.exception.LikeNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTests {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private ThemeRepository themeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewFileRepository reviewFileRepository;

    @Mock
    private ReviewLikeRepository reviewLikeRepository;

    @Mock
    private ReviewTendencyGenreRepository reviewTendencyGenreRepository;

    @InjectMocks
    private ReviewServiceImpl reviewServiceImpl;

    private Member dummyMember;
    private Theme dummyTheme;
    private Review dummyReview;

    @BeforeEach
    void setUp() {
        // 기본 dummy 객체 설정
        dummyMember = new Member();
        dummyMember.setId("user1");
        dummyMember.setMemberCode(1);
        dummyMember.setPoint(0);

        dummyTheme = new Theme();
        dummyTheme.setThemeCode(1);

        dummyReview = new Review();
        dummyReview.setReviewCode(100);
        dummyReview.setMember(dummyMember);
        dummyReview.setTheme(dummyTheme);
        dummyReview.setActive(true);
        dummyReview.setCreatedAt(LocalDateTime.now());
    }

    // --- createReview 테스트 ---

    @Test
    void testCreateReview_withoutImages_shouldSaveReviewAndUpdateMemberPoint() {
        // given
        CreateReviewDTO createReviewDTO = new CreateReviewDTO();
        createReviewDTO.setThemeCode(1);
        // (나머지 값들은 필요에 따라 설정)

        when(modelMapper.map(createReviewDTO, Review.class)).thenReturn(dummyReview);
        when(themeRepository.findById(1)).thenReturn(Optional.of(dummyTheme));
        when(userRepository.findById("user1")).thenReturn(Optional.of(dummyMember));
        when(reviewRepository.save(dummyReview)).thenReturn(dummyReview);
        // images가 null인 경우 saveReviewFile 호출되지 않음

        // when
        reviewServiceImpl.createReview(createReviewDTO, null, "user1");

        // then
        verify(reviewRepository).save(dummyReview);
        verify(userRepository).save(dummyMember);
        assertThat(dummyMember.getPoint()).isEqualTo(5);
    }

    // --- updateReview 테스트 ---

    @Test
    void testUpdateReview_validUser_shouldUpdateReview() {
        // given
        UpdateReviewDTO updateReviewDTO = new UpdateReviewDTO();
        updateReviewDTO.setReviewCode(100);
        updateReviewDTO.setContent("Updated content");
        // 예시로 content만 업데이트

        when(reviewRepository.findById(100)).thenReturn(Optional.of(dummyReview));

        // when
        reviewServiceImpl.updateReview(updateReviewDTO, "user1");

        // then
        verify(reviewRepository).save(dummyReview);
        assertThat(dummyReview.getContent()).isEqualTo("Updated content");
    }

    @Test
    void testUpdateReview_invalidUser_shouldThrowInvalidUserException() {
        // given
        UpdateReviewDTO updateReviewDTO = new UpdateReviewDTO();
        updateReviewDTO.setReviewCode(100);
        updateReviewDTO.setContent("Updated content");

        when(reviewRepository.findById(100)).thenReturn(Optional.of(dummyReview));

        // when/then
        assertThatThrownBy(() -> reviewServiceImpl.updateReview(updateReviewDTO, "user2"))
                .isInstanceOf(InvalidUserException.class)
                .hasMessageContaining("리뷰 수정 권한이 없습니다");
    }

    // --- deleteReview 테스트 ---

    @Test
    void testDeleteReview_validUser_shouldDeactivateReview() {
        // given
        ReviewCodeDTO reviewCodeDTO = new ReviewCodeDTO();
        reviewCodeDTO.setReviewCode(100);
        dummyReview.setActive(true);
        when(reviewRepository.findById(100)).thenReturn(Optional.of(dummyReview));

        // when
        reviewServiceImpl.deleteReview(reviewCodeDTO, "user1");

        // then
        verify(reviewRepository).save(dummyReview);
        assertThat(dummyReview.getActive()).isFalse();
    }

    @Test
    void testDeleteReview_invalidUser_shouldThrowInvalidUserException() {
        // given
        ReviewCodeDTO reviewCodeDTO = new ReviewCodeDTO();
        reviewCodeDTO.setReviewCode(100);
        when(reviewRepository.findById(100)).thenReturn(Optional.of(dummyReview));

        // when/then
        assertThatThrownBy(() -> reviewServiceImpl.deleteReview(reviewCodeDTO, "user2"))
                .isInstanceOf(InvalidUserException.class)
                .hasMessageContaining("리뷰 삭제 권한이 없습니다");
    }

    // --- likeReview 테스트 ---

    @Test
    void testLikeReview_whenNoLikeExists_shouldCreateNewReviewLike() {
        // given
        int reviewCode = 100;
        ReviewCodeDTO reviewCodeDTO = new ReviewCodeDTO();
        reviewCodeDTO.setReviewCode(reviewCode);

        when(userRepository.findById("user1")).thenReturn(Optional.of(dummyMember));
        when(reviewLikeRepository.findByMemberCodeAndReviewCode(1, reviewCode)).thenReturn(null);

        // when
        reviewServiceImpl.likeReview(reviewCodeDTO, "user1");

        // then : 새 ReviewLike 객체가 저장되었음을 검증
        verify(reviewLikeRepository).save(any(ReviewLike.class));
    }

    @Test
    void testLikeReview_whenLikeExistsAndActive_shouldThrowAlreadyLikedException() {
        // given
        int reviewCode = 100;
        ReviewCodeDTO reviewCodeDTO = new ReviewCodeDTO();
        reviewCodeDTO.setReviewCode(reviewCode);

        ReviewLike existingLike = new ReviewLike();
        existingLike.setActive(true);

        when(userRepository.findById("user1")).thenReturn(Optional.of(dummyMember));
        when(reviewLikeRepository.findByMemberCodeAndReviewCode(1, reviewCode)).thenReturn(existingLike);

        // when/then
        assertThatThrownBy(() -> reviewServiceImpl.likeReview(reviewCodeDTO, "user1"))
                .isInstanceOf(AlreadyLikedException.class)
                .hasMessageContaining("이미 좋아요가 존재합니다.");
    }

    @Test
    void testLikeReview_whenLikeExistsAndInactive_shouldUpdateReviewLike() {
        // given
        int reviewCode = 100;
        ReviewCodeDTO reviewCodeDTO = new ReviewCodeDTO();
        reviewCodeDTO.setReviewCode(reviewCode);

        ReviewLike existingLike = new ReviewLike();
        existingLike.setActive(false);

        when(userRepository.findById("user1")).thenReturn(Optional.of(dummyMember));
        when(reviewLikeRepository.findByMemberCodeAndReviewCode(1, reviewCode)).thenReturn(existingLike);

        // when
        reviewServiceImpl.likeReview(reviewCodeDTO, "user1");

        // then: 기존 객체의 active가 true로 바뀌고 저장됨
        verify(reviewLikeRepository).save(existingLike);
        assertThat(existingLike.getActive()).isTrue();
    }

    // --- deleteLikeReview 테스트 ---

    @Test
    void testDeleteLikeReview_whenNoLikeExists_shouldThrowLikeNotFoundException() {
        // given
        int reviewCode = 100;
        ReviewCodeDTO reviewCodeDTO = new ReviewCodeDTO();
        reviewCodeDTO.setReviewCode(reviewCode);

        when(userRepository.findById("user1")).thenReturn(Optional.of(dummyMember));
        when(reviewLikeRepository.findByMemberCodeAndReviewCode(1, reviewCode)).thenReturn(null);

        // when/then
        assertThatThrownBy(() -> reviewServiceImpl.deleteLikeReview(reviewCodeDTO, "user1"))
                .isInstanceOf(LikeNotFoundException.class)
                .hasMessageContaining("좋아요가 존재하지 않습니다.");
    }

    @Test
    void testDeleteLikeReview_whenLikeExistsAndActive_shouldDeactivateReviewLike() {
        // given
        int reviewCode = 100;
        ReviewCodeDTO reviewCodeDTO = new ReviewCodeDTO();
        reviewCodeDTO.setReviewCode(reviewCode);

        ReviewLike existingLike = new ReviewLike();
        existingLike.setActive(true);

        when(userRepository.findById("user1")).thenReturn(Optional.of(dummyMember));
        when(reviewLikeRepository.findByMemberCodeAndReviewCode(1, reviewCode)).thenReturn(existingLike);

        // when
        reviewServiceImpl.deleteLikeReview(reviewCodeDTO, "user1");

        // then
        verify(reviewLikeRepository).save(existingLike);
        assertThat(existingLike.getActive()).isFalse();
    }

    @Test
    void testDeleteLikeReview_whenLikeExistsAndInactive_shouldThrowLikeNotFoundException() {
        // given
        int reviewCode = 100;
        ReviewCodeDTO reviewCodeDTO = new ReviewCodeDTO();
        reviewCodeDTO.setReviewCode(reviewCode);

        ReviewLike existingLike = new ReviewLike();
        existingLike.setActive(false);

        when(userRepository.findById("user1")).thenReturn(Optional.of(dummyMember));
        when(reviewLikeRepository.findByMemberCodeAndReviewCode(1, reviewCode)).thenReturn(existingLike);

        // when/then
        assertThatThrownBy(() -> reviewServiceImpl.deleteLikeReview(reviewCodeDTO, "user1"))
                .isInstanceOf(LikeNotFoundException.class)
                .hasMessageContaining("좋아요가 존재하지 않습니다.");
    }

    // --- findReviewReport 테스트 ---

    @Test
    void testFindReviewReport_whenAvgScoreIsNull_shouldReturnNull() {
        // given
        when(userRepository.findById("user1")).thenReturn(Optional.of(dummyMember));
        when(reviewRepository.findAvgScoreByMemberCode(1)).thenReturn(null);

        // when
        ReviewReportDTO result = reviewServiceImpl.findReviewReport("user1");

        // then
        assertThat(result).isNull();
    }

    @Test
    void testFindReviewReport_whenAvgScoreExists_shouldReturnReport() {
        // given
        when(userRepository.findById("user1")).thenReturn(Optional.of(dummyMember));
        when(reviewRepository.findAvgScoreByMemberCode(1)).thenReturn(80);
        List<String> genres = List.of("Action", "Comedy");
        when(reviewRepository.findTopGenresByMemberCode(eq(1), any(Pageable.class)))
                .thenReturn(genres);

        // when
        ReviewReportDTO result = reviewServiceImpl.findReviewReport("user1");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAvgScore()).isEqualTo(80);
        assertThat(result.getGenres()).containsExactlyElementsOf(genres);
    }

    // --- findReviewStatistics 테스트 ---

    @Test
    void testFindReviewStatistics_whenStatisticsIsEmpty_shouldReturnNull() {
        // given
        when(reviewRepository.findStatisticsByThemeCode(1)).thenReturn(Optional.empty());

        // when
        StatisticsReviewDTO result = reviewServiceImpl.findReviewStatistics(1);

        // then
        assertThat(result).isNull();
    }

    @Test
    void testFindReviewStatistics_whenStatisticsHasNullAvgTotalScore_shouldReturnNull() {
        // given
        StatisticsReviewDTO dto = new StatisticsReviewDTO();
        dto.setAvgTotalScore(null);
        when(reviewRepository.findStatisticsByThemeCode(1)).thenReturn(Optional.of(dto));

        // when
        StatisticsReviewDTO result = reviewServiceImpl.findReviewStatistics(1);

        // then
        assertThat(result).isNull();
    }

    @Test
    void testFindReviewStatistics_whenStatisticsValid_shouldReturnDto() {
        // given
        StatisticsReviewDTO dto = new StatisticsReviewDTO();
        dto.setAvgTotalScore(75.0);
        when(reviewRepository.findStatisticsByThemeCode(1)).thenReturn(Optional.of(dto));

        // when
        StatisticsReviewDTO result = reviewServiceImpl.findReviewStatistics(1);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAvgTotalScore()).isEqualTo(75.0);
    }
}
