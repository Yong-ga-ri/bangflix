package com.swcamp9th.bangflixbackend.domain.store.service;

import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.service.ReviewService;
import com.swcamp9th.bangflixbackend.domain.store.dto.StoreDTO;
import com.swcamp9th.bangflixbackend.domain.store.exception.StoreNotFoundException;
import com.swcamp9th.bangflixbackend.domain.store.repository.StoreRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoreServiceImpl implements StoreService {

    private final ModelMapper modelMapper;
    private final ReviewService reviewService;
    private final StoreRepository storeRepository;

    @Autowired
    public StoreServiceImpl(
            ModelMapper modelMapper,
            ReviewService reviewService,
            StoreRepository storeRepository
    ) {
        this.modelMapper = modelMapper;
        this.reviewService = reviewService;
        this.storeRepository = storeRepository;
    }

    @Override
    @Transactional
    public StoreDTO findStore(Integer storeCode) {
        return modelMapper.map(
                storeRepository.findById(storeCode)
                        .orElseThrow(StoreNotFoundException::new),
                StoreDTO.class);
    }

    @Override
    @Transactional
    public ReviewDTO findBestReviewByStore(Integer storeCode) {
         return reviewService.getBestReviewByStoreCode(storeCode);
    }
}
