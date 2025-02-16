package com.swcamp9th.bangflixbackend.unit.domain.store;

import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.entity.Review;
import com.swcamp9th.bangflixbackend.domain.review.entity.ReviewLike;
import com.swcamp9th.bangflixbackend.domain.review.repository.ReviewLikeRepository;
import com.swcamp9th.bangflixbackend.domain.review.repository.ReviewRepository;
import com.swcamp9th.bangflixbackend.domain.review.service.ReviewService;
import com.swcamp9th.bangflixbackend.domain.store.dto.StoreDTO;
import com.swcamp9th.bangflixbackend.domain.store.entity.Store;
import com.swcamp9th.bangflixbackend.domain.store.repository.StoreRepository;
import com.swcamp9th.bangflixbackend.domain.store.service.StoreServiceImpl;
import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import com.swcamp9th.bangflixbackend.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceImplTests {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReviewLikeRepository reviewLikeRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewService reviewService;

    @Mock
    private StoreRepository storeRepository;

    private StoreServiceImpl storeService;

    @BeforeEach
    void setUp() {
        storeService = new StoreServiceImpl(modelMapper, userRepository, reviewLikeRepository, reviewRepository, reviewService, storeRepository);
    }

    @Test
    void findStore_ShouldReturnStoreDTO_WhenStoreExists() {
        // given
        Integer storeCode = 1;
        Store store = new Store();  // 필요한 경우 setter 등으로 속성 값을 채울 수 있음.
        StoreDTO expectedStoreDTO = new StoreDTO();

        // storeRepository.findById()가 store를 리턴하도록 모킹
        when(storeRepository.findById(storeCode)).thenReturn(Optional.of(store));
        // modelMapper.map()이 store를 StoreDTO로 변환하도록 모킹
        when(modelMapper.map(store, StoreDTO.class)).thenReturn(expectedStoreDTO);

        // when
        StoreDTO actualStoreDTO = storeService.findStore(storeCode);

        // then
        assertThat(actualStoreDTO).isEqualTo(expectedStoreDTO);
        verify(storeRepository).findById(storeCode);
        verify(modelMapper).map(store, StoreDTO.class);
    }

    @Test
    void findStore_ShouldThrowException_WhenStoreDoesNotExist() {
        // given
        Integer storeCode = 1;
        when(storeRepository.findById(storeCode)).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> storeService.findStore(storeCode))
                .isInstanceOf(java.util.NoSuchElementException.class);
        verify(storeRepository).findById(storeCode);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void findBestReviewByStore_ShouldReturnNull_WhenNoReviewLikesFound() {
        // given
        Integer storeCode = 1;
        String loginId = "user1";
        // reviewLikeRepository가 빈 리스트를 리턴하도록 모킹
        when(reviewLikeRepository.findBestReviewByStoreCode(storeCode)).thenReturn(Collections.emptyList());

        // userRepository.findById()가 Member를 리턴하도록 모킹 (실제 로직에서는 member를 조회하지만 reviewLike가 없으므로 이후 로직은 실행되지 않음)
        Member member = new Member();
        member.setMemberCode(100);
        when(userRepository.findById(loginId)).thenReturn(Optional.of(member));

        // when
        ReviewDTO actualReviewDTO = storeService.findBestReviewByStore(storeCode, loginId);

        // then
        assertThat(actualReviewDTO).isNull();
        verify(reviewLikeRepository).findBestReviewByStoreCode(storeCode);
        verify(userRepository).findById(loginId);
        // reviewRepository, reviewService는 호출되지 않아야 함.
        verifyNoInteractions(reviewRepository, reviewService);
    }

    @Test
    void findBestReviewByStore_ShouldReturnReviewDTO_WhenReviewLikesFoundAndReviewExists() {
        // given
        Integer storeCode = 1;
        String loginId = "user1";

        // 모킹할 ReviewLike 생성 (reviewCode가 10인 리뷰가 가장 좋아요 수가 높은 리뷰라고 가정)
        ReviewLike reviewLike = new ReviewLike();
        reviewLike.setReviewCode(10);
        List<ReviewLike> reviewLikeList = List.of(reviewLike);
        when(reviewLikeRepository.findBestReviewByStoreCode(storeCode)).thenReturn(reviewLikeList);

        // userRepository가 member를 리턴하도록 모킹
        Member member = new Member();
        member.setMemberCode(200);
        when(userRepository.findById(loginId)).thenReturn(Optional.of(member));

        // reviewRepository가 review를 리턴하도록 모킹
        Review review = new Review();
        when(reviewRepository.findById(10)).thenReturn(Optional.of(review));

        // reviewService가 review와 memberCode를 이용해 ReviewDTO를 생성하도록 모킹
        ReviewDTO expectedReviewDTO = new ReviewDTO();
        when(reviewService.getReviewDTO(review, member.getMemberCode())).thenReturn(expectedReviewDTO);

        // when
        ReviewDTO actualReviewDTO = storeService.findBestReviewByStore(storeCode, loginId);

        // then
        assertThat(actualReviewDTO).isEqualTo(expectedReviewDTO);
        verify(reviewLikeRepository).findBestReviewByStoreCode(storeCode);
        verify(userRepository).findById(loginId);
        verify(reviewRepository).findById(10);
        verify(reviewService).getReviewDTO(review, member.getMemberCode());
    }

    @Test
    void findBestReviewByStore_ShouldReturnReviewDTO_WhenReviewLikesFoundButReviewNotFound() {
        // given
        Integer storeCode = 1;
        String loginId = "user1";

        // reviewLike가 존재하는 경우
        ReviewLike reviewLike = new ReviewLike();
        reviewLike.setReviewCode(10);
        when(reviewLikeRepository.findBestReviewByStoreCode(storeCode)).thenReturn(List.of(reviewLike));

        // userRepository가 member를 리턴하도록 모킹
        Member member = new Member();
        member.setMemberCode(300);
        when(userRepository.findById(loginId)).thenReturn(Optional.of(member));

        // reviewRepository가 review를 찾지 못하도록 모킹 (Optional.empty() 리턴)
        when(reviewRepository.findById(10)).thenReturn(Optional.empty());

        // reviewService가 null review를 인자로 받아 ReviewDTO를 생성하도록 모킹
        ReviewDTO expectedReviewDTO = new ReviewDTO();
        when(reviewService.getReviewDTO(null, member.getMemberCode())).thenReturn(expectedReviewDTO);

        // when
        ReviewDTO actualReviewDTO = storeService.findBestReviewByStore(storeCode, loginId);

        // then
        assertThat(actualReviewDTO).isEqualTo(expectedReviewDTO);
        verify(reviewLikeRepository).findBestReviewByStoreCode(storeCode);
        verify(userRepository).findById(loginId);
        verify(reviewRepository).findById(10);
        verify(reviewService).getReviewDTO(null, member.getMemberCode());
    }
}
