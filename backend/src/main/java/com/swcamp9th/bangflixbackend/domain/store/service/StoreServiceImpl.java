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
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoreServiceImpl implements StoreService {

    private final ModelMapper modelMapper;
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewService reviewService;
    private final StoreRepository storeRepository;

    @Autowired
    public StoreServiceImpl(
            ModelMapper modelMapper,
            ReviewLikeRepository reviewLikeRepository,
            ReviewRepository reviewRepository,
            ReviewService reviewService,
            StoreRepository storeRepository
    ) {
        this.modelMapper = modelMapper;
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
    public ReviewDTO findBestReviewByStore(Integer storeCode) {
        List<ReviewLike> reviewLike = reviewLikeRepository.findBestReviewByStoreCode(storeCode);

        if(reviewLike.isEmpty())
            return null;

        Review review = reviewRepository.findById(reviewLike.get(0).getReviewCode()).orElse(null);

        return reviewService.getReviewDTO(review);
    }
}
