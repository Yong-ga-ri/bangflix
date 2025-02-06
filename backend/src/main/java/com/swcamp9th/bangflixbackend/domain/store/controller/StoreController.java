package com.swcamp9th.bangflixbackend.domain.store.controller;

import com.swcamp9th.bangflixbackend.shared.response.ResponseMessage;
import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewDTO;
import com.swcamp9th.bangflixbackend.domain.store.dto.StoreDTO;
import com.swcamp9th.bangflixbackend.domain.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.swcamp9th.bangflixbackend.shared.filter.RequestFilter.SERVLET_REQUEST_ATTRIBUTE_KEY;

@RestController
@RequestMapping("/api/v1/stores")
public class StoreController {

    private final StoreService storeService;

    @Autowired
    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/{storeCode}")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "특정 업체에 대한 정보를 반환하는 API.")
    public ResponseEntity<ResponseMessage<StoreDTO>> findStore(
        @PathVariable("storeCode") Integer storeCode) {

        // 서비스에서 필터를 사용해 조회
        StoreDTO store = storeService.findStore(storeCode);

        return ResponseEntity.ok(new ResponseMessage<>(200, storeCode + "번 업체 조회 성공", store));
    }

    @GetMapping("/bestreview/{storeCode}")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "특정 업체에서 가장 좋아요 수가 많은 리뷰를 반환하는 API.")
    public ResponseEntity<ResponseMessage<ReviewDTO>> findBestReviewByStore(
        @PathVariable("storeCode") Integer storeCode,
        @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId) {

        // 서비스에서 필터를 사용해 조회
        ReviewDTO storeBestReview  = storeService.findBestReviewByStore(storeCode, loginId);

        return ResponseEntity.ok(new ResponseMessage<>(200, storeCode + "번 업체 베스트 리뷰 조회 성공", storeBestReview));
    }
}
