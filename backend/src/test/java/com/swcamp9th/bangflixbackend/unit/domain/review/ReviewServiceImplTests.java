package com.swcamp9th.bangflixbackend.unit.domain.review;

import com.swcamp9th.bangflixbackend.domain.review.dto.*;
import com.swcamp9th.bangflixbackend.domain.review.entity.Review;
import com.swcamp9th.bangflixbackend.domain.review.entity.ReviewFile;
import com.swcamp9th.bangflixbackend.domain.review.entity.ReviewLike;
import com.swcamp9th.bangflixbackend.domain.review.repository.ReviewFileRepository;
import com.swcamp9th.bangflixbackend.domain.review.repository.ReviewLikeRepository;
import com.swcamp9th.bangflixbackend.domain.review.repository.ReviewRepository;
import com.swcamp9th.bangflixbackend.domain.review.repository.ReviewTendencyGenreRepository;
import com.swcamp9th.bangflixbackend.domain.review.service.ReviewServiceImpl;
import com.swcamp9th.bangflixbackend.domain.theme.entity.Theme;
import com.swcamp9th.bangflixbackend.domain.theme.repository.ThemeRepository;
import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import com.swcamp9th.bangflixbackend.domain.user.service.UserService;
import com.swcamp9th.bangflixbackend.shared.error.AlreadyLikedException;
import com.swcamp9th.bangflixbackend.shared.error.LikeNotFoundException;
import com.swcamp9th.bangflixbackend.shared.error.ReviewNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTests {

    @Mock
    private ModelMapper modelMapper;
    @Mock
    private ThemeRepository themeRepository;
    @Mock
    private UserService userService;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ReviewFileRepository reviewFileRepository;
    @Mock
    private ReviewLikeRepository reviewLikeRepository;
    @Mock
    private ReviewTendencyGenreRepository reviewTendencyGenreRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    // 샘플 객체들
    private CreateReviewDTO createReviewDTO;
    private Review review;
    private ReviewDTO reviewDTO;
    private Theme theme;
    private Member member;
    private ReviewCodeDTO reviewCodeDTO;

    @BeforeEach
    void setUp() {
        // Member 샘플 (실제 Member 엔티티를 사용)
        member = new Member();
        member.setMemberCode(1000);
        member.setNickname("TestUser");
        member.setImage("testUserImage.png");

        // Theme 샘플
        theme = new Theme();
        theme.setThemeCode(1);
        theme.setName("Escape Room");
        theme.setPosterImage("poster.png");

        // CreateReviewDTO 샘플
        createReviewDTO = new CreateReviewDTO();
        createReviewDTO.setThemeCode(1);
        createReviewDTO.setContent("Great experience!");
        createReviewDTO.setTotalScore(90);

        // Review 엔티티 샘플
        review = new Review();
        review.setReviewCode(500);
        review.setTheme(theme);
        review.setMember(member);
        review.setTotalScore(90);
        review.setCreatedAt(LocalDateTime.now().minusDays(1));
        review.setActive(true);

        // ReviewDTO 샘플 (ModelMapper 매핑 결과)
        reviewDTO = new ReviewDTO();
        reviewDTO.setReviewCode(500);
        reviewDTO.setMemberCode(member.getMemberCode());
        reviewDTO.setMemberNickname(member.getNickname());
        reviewDTO.setMemberImage(member.getImage());
        reviewDTO.setThemeCode(theme.getThemeCode());
        reviewDTO.setThemeName(theme.getName());
        reviewDTO.setThemeImage(theme.getPosterImage());
        reviewDTO.setImagePaths(Collections.emptyList());
        reviewDTO.setLikes(0);
        reviewDTO.setIsLike(false);

        // ReviewCodeDTO 샘플
        reviewCodeDTO = new ReviewCodeDTO();
        reviewCodeDTO.setReviewCode(500);
    }

    @Test
    @DisplayName("createReview: 이미지 없이 리뷰 생성 성공")
    void testCreateReview_withoutImages() {

        // given
        when(modelMapper.map(createReviewDTO, Review.class)).thenReturn(review);
        when(themeRepository.findById(createReviewDTO.getThemeCode())).thenReturn(Optional.of(theme));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // when
        reviewService.createReview(createReviewDTO, null, member);

        // then
        verify(reviewRepository, times(1)).save(review);
        verify(userService, times(1)).memberGetPoint(member, 5);

        // images가 null인 경우 reviewFileRepository.save()가 호출되지 않아야 함
        verify(reviewFileRepository, never()).save(any(ReviewFile.class));
    }

    @Test
    @DisplayName("createReview: 이미지와 함께 리뷰 생성 성공")
    void testCreateReview_withImages() {

        // given
        MultipartFile file = new MockMultipartFile("file", "image.png", "image/png", "dummyImageContent".getBytes());
        List<MultipartFile> images = List.of(file);
        when(modelMapper.map(createReviewDTO, Review.class)).thenReturn(review);
        when(themeRepository.findById(createReviewDTO.getThemeCode())).thenReturn(Optional.of(theme));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // when
        reviewService.createReview(createReviewDTO, images, member);

        // then
        verify(reviewRepository, times(1)).save(review);
        verify(reviewFileRepository, atLeastOnce()).save(any(ReviewFile.class));
        verify(userService, times(1)).memberGetPoint(member, 5);
    }

    @Test
    @DisplayName("deleteReview: 기존 리뷰 삭제 성공")
    void testDeleteReview_success() {

        // given
        when(reviewRepository.findById(reviewCodeDTO.getReviewCode())).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        // when
        reviewService.deleteReview(reviewCodeDTO, member.getMemberCode());

        // then
        assertThat(review.getActive()).isFalse();
        verify(reviewRepository, times(1)).save(review);
    }

    @Test
    @DisplayName("deleteReview: 리뷰가 존재하지 않으면 예외 발생")
    void testDeleteReview_reviewNotFoundThrowsException() {

        // given
        when(reviewRepository.findById(reviewCodeDTO.getReviewCode())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reviewService.deleteReview(reviewCodeDTO, member.getMemberCode()))
                .isInstanceOf(ReviewNotFoundException.class)
                .hasMessage("리뷰가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("findReviewsWithFilters: 멤버코드 포함, 기본 최신순 정렬")
    void testFindReviewsWithFilters_withMemberCode_defaultSort() {

        // given
        int themeCode = theme.getThemeCode();
        Pageable pageable = PageRequest.of(0, 10);
        List<Review> reviews = new ArrayList<>(List.of(review));
        when(reviewRepository.findByThemeCodeAndActiveTrueWithFetchJoin(eq(themeCode), eq(pageable))).thenReturn(reviews);
        when(modelMapper.map(review, ReviewDTO.class)).thenReturn(reviewDTO);
        when(reviewLikeRepository.findByReviewCodeAndMemberCode(eq(review.getReviewCode()), any(Integer.class)))
                .thenReturn(Optional.empty());
        when(reviewTendencyGenreRepository.findMemberGenreByMemberCode(any(Integer.class))).thenReturn(Collections.emptyList());

        // when
        List<ReviewDTO> result = reviewService.findReviewsWithFilters(themeCode, null, pageable, member.getMemberCode());

        // then
        assertThat(result).hasSize(1);
        verify(reviewRepository, times(1)).findByThemeCodeAndActiveTrueWithFetchJoin(eq(themeCode), eq(pageable));
    }

    @Test
    @DisplayName("findReviewsWithFilters: 멤버코드 포함, 점수 높은 순 정렬")
    void testFindReviewsWithFilters_withMemberCode_highScore() {

        // given
        int themeCode = theme.getThemeCode();
        Pageable pageable = PageRequest.of(0, 10);
        Review reviewHigh = new Review();
        reviewHigh.setReviewCode(501);
        reviewHigh.setTheme(theme);
        reviewHigh.setMember(member);
        reviewHigh.setTotalScore(95);
        reviewHigh.setCreatedAt(LocalDateTime.now().minusHours(2));
        reviewHigh.setActive(true);
        Review reviewLow = new Review();
        reviewLow.setReviewCode(502);
        reviewLow.setTheme(theme);
        reviewLow.setMember(member);
        reviewLow.setTotalScore(70);
        reviewLow.setCreatedAt(LocalDateTime.now().minusHours(1));
        reviewLow.setActive(true);

        List<Review> reviews = new ArrayList<>(List.of(reviewLow, reviewHigh));
        when(reviewRepository.findByThemeCodeAndActiveTrueWithFetchJoin(eq(themeCode), eq(pageable))).thenReturn(reviews);
        when(modelMapper.map(any(Review.class), eq(ReviewDTO.class))).thenReturn(reviewDTO);
        when(reviewLikeRepository.findByReviewCodeAndMemberCode(any(Integer.class), any(Integer.class)))
                .thenReturn(Optional.empty());
        when(reviewTendencyGenreRepository.findMemberGenreByMemberCode(any(Integer.class))).thenReturn(Collections.emptyList());

        // when
        List<ReviewDTO> result = reviewService.findReviewsWithFilters(themeCode, "highScore", pageable, member.getMemberCode());

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("findReviewsWithFilters: 멤버코드 미포함, 기본 최신순 정렬")
    void testFindReviewsWithFilters_withoutMemberCode_defaultSort() {

        // given
        int themeCode = theme.getThemeCode();
        Pageable pageable = PageRequest.of(0, 10);
        List<Review> reviews = new ArrayList<>(List.of(review));
        when(reviewRepository.findByThemeCodeAndActiveTrueWithFetchJoin(eq(themeCode), eq(pageable))).thenReturn(reviews);
        when(modelMapper.map(review, ReviewDTO.class)).thenReturn(reviewDTO);
        when(reviewTendencyGenreRepository.findMemberGenreByMemberCode(any(Integer.class))).thenReturn(Collections.emptyList());

        // when
        List<ReviewDTO> result = reviewService.findReviewsWithFilters(themeCode, null, pageable);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("getReviewDTOS: 멤버코드 포함 결과 매핑")
    void testGetReviewDTOS_withMemberCode() {

        // given
        List<Review> reviewList = List.of(review);
        when(modelMapper.map(review, ReviewDTO.class)).thenReturn(reviewDTO);
        when(reviewLikeRepository.findByReviewCodeAndMemberCode(eq(review.getReviewCode()), eq(member.getMemberCode())))
                .thenReturn(Optional.empty());
        when(reviewTendencyGenreRepository.findMemberGenreByMemberCode(eq(member.getMemberCode())))
                .thenReturn(Collections.emptyList());

        // when
        List<ReviewDTO> result = reviewService.getReviewDTOS(reviewList, member.getMemberCode());

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getReviewCode()).isEqualTo(review.getReviewCode());
    }

    @Test
    @DisplayName("getReviewDTOS: 멤버코드 미포함 결과 매핑")
    void testGetReviewDTOS_withoutMemberCode() {

        // given
        List<Review> reviewList = List.of(review);
        when(modelMapper.map(review, ReviewDTO.class)).thenReturn(reviewDTO);
        when(reviewTendencyGenreRepository.findMemberGenreByMemberCode(any(Integer.class)))
                .thenReturn(Collections.emptyList());

        // when
        List<ReviewDTO> result = reviewService.getReviewDTOS(reviewList);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("getReviewDTO: 단일 리뷰 DTO 매핑")
    void testGetReviewDTO() {

        // given
        when(modelMapper.map(review, ReviewDTO.class)).thenReturn(reviewDTO);
        when(reviewTendencyGenreRepository.findMemberGenreByMemberCode(eq(member.getMemberCode())))
                .thenReturn(Collections.emptyList());

        // when
        ReviewDTO result = reviewService.getReviewDTO(review);

        // then
        assertThat(result.getReviewCode()).isEqualTo(review.getReviewCode());
    }

    @Test
    @DisplayName("findReviewReport: 리뷰 리포트 반환")
    void testFindReviewReport_returnsReport() {

        // given
        int memberCode = member.getMemberCode();
        int avgScore = 85;
        List<String> topGenres = List.of("Action", "Thriller", "Adventure");
        when(reviewRepository.findAvgScoreByMemberCode(memberCode)).thenReturn(avgScore);
        Pageable pageable = PageRequest.of(0, 3);
        when(reviewRepository.findTopGenresByMemberCode(memberCode, pageable)).thenReturn(topGenres);

        // when
        ReviewReportDTO report = reviewService.findReviewReport(memberCode);

        // then
        assertThat(report).isNotNull();
        assertThat(report.getAvgScore()).isEqualTo(avgScore);
    }

    @Test
    @DisplayName("findReviewReport: 평균 점수가 없으면 null 반환")
    void testFindReviewReport_returnsNull_whenNoAvgScore() {

        // given
        int memberCode = member.getMemberCode();
        when(reviewRepository.findAvgScoreByMemberCode(memberCode)).thenReturn(null);

        // when
        ReviewReportDTO report = reviewService.findReviewReport(memberCode);

        // then
        assertThat(report).isNull();
    }

    @Test
    @DisplayName("findReviewByMemberCode: 멤버의 리뷰 반환")
    void testFindReviewByMemberCode_returnsReviews() {

        // given
        int memberCode = member.getMemberCode();
        Pageable pageable = PageRequest.of(0, 10);
        List<Review> reviews = List.of(review);
        when(reviewRepository.findByMemberCode(memberCode, pageable)).thenReturn(reviews);
        when(modelMapper.map(review, ReviewDTO.class)).thenReturn(reviewDTO);
        when(reviewLikeRepository.findByReviewCodeAndMemberCode(eq(review.getReviewCode()), eq(memberCode)))
                .thenReturn(Optional.empty());
        when(reviewTendencyGenreRepository.findMemberGenreByMemberCode(eq(memberCode)))
                .thenReturn(Collections.emptyList());

        // when
        List<ReviewDTO> result = reviewService.findReviewByMemberCode(memberCode, pageable);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("findReviewByMemberCode: 리뷰가 없으면 null 반환")
    void testFindReviewByMemberCode_returnsNull_whenEmpty() {

        // given
        int memberCode = member.getMemberCode();
        Pageable pageable = PageRequest.of(0, 10);
        when(reviewRepository.findByMemberCode(memberCode, pageable)).thenReturn(Collections.emptyList());

        // when
        List<ReviewDTO> result = reviewService.findReviewByMemberCode(memberCode, pageable);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("findReviewStatistics: 테마 리뷰 통계 반환")
    void testFindReviewStatistics_returnsStatistics() {

        // given
        int themeCode = theme.getThemeCode();
        StatisticsReviewDTO statisticsReviewDTO = new StatisticsReviewDTO();
        statisticsReviewDTO.setAvgTotalScore(88.0);
        when(reviewRepository.findStatisticsByThemeCode(themeCode)).thenReturn(Optional.of(statisticsReviewDTO));

        // when
        StatisticsReviewDTO result = reviewService.findReviewStatistics(themeCode);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAvgTotalScore()).isEqualTo(88.0);
    }

    @Test
    @DisplayName("findReviewStatistics: 통계가 없으면 null 반환")
    void testFindReviewStatistics_returnsNull_whenNoStatistics() {

        // given
        int themeCode = theme.getThemeCode();
        when(reviewRepository.findStatisticsByThemeCode(themeCode)).thenReturn(Optional.empty());

        // when
        StatisticsReviewDTO result = reviewService.findReviewStatistics(themeCode);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("likeReview: 새 좋아요 생성")
    void testLikeReview_createNewLike() {

        // given
        int memberCode = member.getMemberCode();
        when(reviewLikeRepository.findByMemberCodeAndReviewCode(memberCode, reviewCodeDTO.getReviewCode()))
                .thenReturn(null);
        // when
        reviewService.likeReview(reviewCodeDTO, memberCode);
        // then
        ArgumentCaptor<ReviewLike> captor = ArgumentCaptor.forClass(ReviewLike.class);
        verify(reviewLikeRepository).save(captor.capture());
        ReviewLike savedLike = captor.getValue();
        assertThat(savedLike.getMemberCode()).isEqualTo(memberCode);
        assertThat(savedLike.getReviewCode()).isEqualTo(reviewCodeDTO.getReviewCode());
        assertThat(savedLike.getActive()).isTrue();
    }

    @Test
    @DisplayName("likeReview: 비활성 좋아요 활성화")
    void testLikeReview_updateInactiveLike() {

        // given
        int memberCode = member.getMemberCode();
        ReviewLike existingLike = new ReviewLike();
        existingLike.setActive(false);
        when(reviewLikeRepository.findByMemberCodeAndReviewCode(memberCode, reviewCodeDTO.getReviewCode()))
                .thenReturn(existingLike);
        // when
        reviewService.likeReview(reviewCodeDTO, memberCode);
        // then
        verify(reviewLikeRepository, times(1)).save(existingLike);
        assertThat(existingLike.getActive()).isTrue();
    }

    @Test
    @DisplayName("likeReview: 이미 활성화된 좋아요면 예외 발생")
    void testLikeReview_alreadyActiveLikeThrowsException() {

        // given
        int memberCode = member.getMemberCode();
        ReviewLike existingLike = new ReviewLike();
        existingLike.setActive(true);
        when(reviewLikeRepository.findByMemberCodeAndReviewCode(memberCode, reviewCodeDTO.getReviewCode()))
                .thenReturn(existingLike);

        // when & then
        assertThatThrownBy(() -> reviewService.likeReview(reviewCodeDTO, memberCode))
                .isInstanceOf(AlreadyLikedException.class)
                .hasMessage("이미 좋아요가 존재합니다.");
    }

    @Test
    @DisplayName("deleteLikeReview: 좋아요 취소 성공")
    void testDeleteLikeReview_success() {

        // given
        int memberCode = member.getMemberCode();
        ReviewLike existingLike = new ReviewLike();
        existingLike.setActive(true);
        when(reviewLikeRepository.findByMemberCodeAndReviewCode(memberCode, reviewCodeDTO.getReviewCode()))
                .thenReturn(existingLike);

        // when
        reviewService.deleteLikeReview(reviewCodeDTO, memberCode);

        // then
        verify(reviewLikeRepository, times(1)).save(existingLike);
        assertThat(existingLike.getActive()).isFalse();
    }

    @Test
    @DisplayName("deleteLikeReview: 좋아요가 없으면 예외 발생 (null)")
    void testDeleteLikeReview_likeNotFoundThrowsException_whenNull() {

        // given
        int memberCode = member.getMemberCode();
        when(reviewLikeRepository.findByMemberCodeAndReviewCode(memberCode, reviewCodeDTO.getReviewCode()))
                .thenReturn(null);

        // when & then
        assertThatThrownBy(() -> reviewService.deleteLikeReview(reviewCodeDTO, memberCode))
                .isInstanceOf(LikeNotFoundException.class)
                .hasMessage("좋아요가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("deleteLikeReview: 좋아요가 비활성 상태면 예외 발생")
    void testDeleteLikeReview_likeNotFoundThrowsException_whenInactive() {

        // given
        int memberCode = member.getMemberCode();
        ReviewLike existingLike = new ReviewLike();
        existingLike.setActive(false);
        when(reviewLikeRepository.findByMemberCodeAndReviewCode(memberCode, reviewCodeDTO.getReviewCode()))
                .thenReturn(existingLike);

        // when & then
        assertThatThrownBy(() -> reviewService.deleteLikeReview(reviewCodeDTO, memberCode))
                .isInstanceOf(LikeNotFoundException.class)
                .hasMessage("좋아요가 존재하지 않습니다.");
    }
}
