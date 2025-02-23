package com.swcamp9th.bangflixbackend.domain.store.controller;

import com.swcamp9th.bangflixbackend.domain.review.service.ReviewService;
import com.swcamp9th.bangflixbackend.shared.response.ResponseCode;
import com.swcamp9th.bangflixbackend.shared.response.SuccessResponse;
import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewDTO;
import com.swcamp9th.bangflixbackend.domain.store.dto.StoreDTO;
import com.swcamp9th.bangflixbackend.domain.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/stores")
public class StoreController {

    private final StoreService storeService;
    private final ReviewService reviewService;

    @Autowired
    public StoreController(
            StoreService storeService,
            ReviewService reviewService
    ) {
        this.storeService = storeService;
        this.reviewService = reviewService;
    }

    @GetMapping("/{storeCode}")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "특정 업체에 대한 정보를 반환하는 API.")
    public ResponseEntity<SuccessResponse<StoreDTO>> findStore(
        @PathVariable("storeCode") Integer storeCode
    ) {
        StoreDTO storeDTO = storeService.findStore(storeCode);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(ResponseCode.OK, storeDTO));
    }

    @GetMapping("/bestreview/{storeCode}")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "특정 업체에서 가장 좋아요 수가 많은 리뷰를 반환하는 API.")
    public ResponseEntity<SuccessResponse<ReviewDTO>> findBestReviewByStore(
        @PathVariable("storeCode") Integer storeCode
    ) {
        ReviewDTO storeBestReviewDTO  = reviewService.getBestReviewByStoreCode(storeCode);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(ResponseCode.OK, storeBestReviewDTO));
    }
}
