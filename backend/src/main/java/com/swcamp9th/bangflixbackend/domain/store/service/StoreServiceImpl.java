package com.swcamp9th.bangflixbackend.domain.store.service;

import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.entity.Review;
import com.swcamp9th.bangflixbackend.domain.review.entity.ReviewLike;
import com.swcamp9th.bangflixbackend.domain.review.repository.ReviewLikeRepository;
import com.swcamp9th.bangflixbackend.domain.review.repository.ReviewRepository;
import com.swcamp9th.bangflixbackend.domain.review.service.ReviewService;
import com.swcamp9th.bangflixbackend.domain.store.dto.StoreDTO;
import com.swcamp9th.bangflixbackend.domain.store.entity.Store;
import com.swcamp9th.bangflixbackend.domain.store.repository.StoreRepository;
import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import com.swcamp9th.bangflixbackend.domain.user.repository.UserRepository;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoreServiceImpl implements StoreService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewService reviewService;
    private final StoreRepository storeRepository;

    @Autowired
    public StoreServiceImpl(
            ModelMapper modelMapper,
            UserRepository userRepository,
            ReviewLikeRepository reviewLikeRepository,
            ReviewRepository reviewRepository,
            ReviewService reviewService,
            StoreRepository storeRepository
    ) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.reviewLikeRepository = reviewLikeRepository;
        this.reviewRepository = reviewRepository;
        this.reviewService = reviewService;
        this.storeRepository = storeRepository;
    }

    @Override
    @Transactional
    public StoreDTO findStore(Integer storeCode) {
        Store store = storeRepository.findById(storeCode).orElseThrow();
        return modelMapper.map(store, StoreDTO.class);
    }

    @Override
    @Transactional
    public ReviewDTO findBestReviewByStore(Integer storeCode, String loginId) {
        List<ReviewLike> reviewLike = reviewLikeRepository.findBestReviewByStoreCode(storeCode);
        Member member = userRepository.findById(loginId).orElseThrow();

        if(reviewLike.isEmpty())
            return null;

        Review review = reviewRepository.findById(reviewLike.get(0).getReviewCode()).orElse(null);

        return reviewService.getReviewDTO(review, member.getMemberCode());
    }
}
