package com.swcamp9th.bangflixbackend.domain.review.controller;

import com.swcamp9th.bangflixbackend.common.ResponseMessage;
import com.swcamp9th.bangflixbackend.domain.review.dto.CreateReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewCodeDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.UpdateReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.service.ReviewService;
import java.io.IOException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }


    @PostMapping("")
    public ResponseEntity<ResponseMessage<Object>> createReview(
        @RequestPart("review") CreateReviewDTO newReview,
        @RequestPart(value = "images", required = false) List<MultipartFile> images)
        throws IOException {

        reviewService.createReview(newReview, images);

        return ResponseEntity.ok(new ResponseMessage<>(200, "리뷰 작성 성공", null));
    }

    @PutMapping("")
    public ResponseEntity<ResponseMessage<Object>> updateReview(@RequestBody UpdateReviewDTO updateReview)
        throws IOException {
        
        reviewService.updateReview(updateReview);

        return ResponseEntity.ok(new ResponseMessage<>(200, "리뷰 수정 성공", null));
    }

    @DeleteMapping("")
    public ResponseEntity<ResponseMessage<Object>> deleteReview(@RequestBody ReviewCodeDTO reviewCodeDTO) {

        reviewService.deleteReview(reviewCodeDTO);

        return ResponseEntity.ok(new ResponseMessage<>(200, "리뷰 삭제 성공", null));
    }

    @GetMapping("/{themeCode}")
    public ResponseEntity<ResponseMessage<Object>> findReviewList(
//        @RequestParam Integer themeCode,
        @PathVariable("themeCode") Integer themeCode,
        @RequestParam Integer lastReviewCode,
        @RequestParam(required = false) String filter) {
        /*
            필터 값은 필수 X.
            필터 값이 없다면 기본 최신순 리뷰 정렬
            highScore, lowScore 값 중 하나를 string 형태로 주면
            점수 별 정렬. 점수가 같다면 날짜 순 정렬

            화면을 참고해보니 더보기 버튼 클릭. 즉, 페이지네이션 X.
            요청 시, 초기에 10개씩 보냄. 이후, lastReviewCode를 주면 전체 정렬 중 그 이후 10개를 보내줌
        */

        // 서비스에서 필터를 사용해 조회
        List<ReviewDTO> reviews = reviewService.findReviewsWithFilters(themeCode, filter, lastReviewCode);

        return ResponseEntity.ok(new ResponseMessage<>(200, "리뷰 조회 성공", reviews));
    }

    @PostMapping("/likes")
    public ResponseEntity<ResponseMessage<Object>> likeReview(@RequestBody ReviewCodeDTO reviewCodeDTO) {

        reviewService.likeReview(reviewCodeDTO);

        return ResponseEntity.ok(new ResponseMessage<>(200, "리뷰 좋아요 성공", null));
    }

    @DeleteMapping("/likes")
    public ResponseEntity<ResponseMessage<Object>> deleteLikeReview(@RequestBody ReviewCodeDTO reviewCodeDTO) {

        reviewService.deleteLikeReview(reviewCodeDTO);

        return ResponseEntity.ok(new ResponseMessage<>(200, "리뷰 좋아요 취소 성공", null));
    }

}
