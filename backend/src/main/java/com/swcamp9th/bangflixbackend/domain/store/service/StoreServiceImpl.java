package com.swcamp9th.bangflixbackend.domain.store.service;

import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.service.ReviewService;
import com.swcamp9th.bangflixbackend.domain.store.dto.StoreDTO;
import com.swcamp9th.bangflixbackend.domain.store.entity.Store;
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
        Store store = storeRepository.findById(storeCode).orElseThrow();
        return modelMapper.map(store, StoreDTO.class);
    }

    @Override
    @Transactional
    public ReviewDTO findBestReviewByStore(Integer storeCode) {
         return reviewService.getBestReviewByStoreCode(storeCode);
    }
}
