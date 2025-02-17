package com.swcamp9th.bangflixbackend.domain.review.controller;

import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import com.swcamp9th.bangflixbackend.domain.user.service.UserService;
import com.swcamp9th.bangflixbackend.shared.response.ResponseMessage;
import com.swcamp9th.bangflixbackend.domain.review.dto.CreateReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewCodeDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewReportDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.StatisticsReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import static com.swcamp9th.bangflixbackend.shared.filter.RequestFilter.SERVLET_REQUEST_ATTRIBUTE_KEY;

@RestController
@Slf4j
@RequestMapping("api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    @Autowired
    public ReviewController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    @PostMapping
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "리뷰를 생성하는 API. form-data 형태로 2가지의 키 값을 가집니다. review (DTO 값으로 리뷰 작성 필요 데이터 묶음), images (multiaprt 데이터로 images이름을 키값으로 중복해 여러장의 파일을 첨부 할 수 있습니다)")
    public ResponseEntity<ResponseMessage<Object>> createReview(
        @RequestPart("review") CreateReviewDTO newReview,
        @RequestPart(value = "images", required = false) List<MultipartFile> images,
        @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId
    ) {
        Member member = userService.findMemberByLoginId(loginId);
        reviewService.createReview(newReview, images, member);
        return ResponseEntity.ok(new ResponseMessage<>(200, "리뷰 작성 성공", null));
    }

    @DeleteMapping
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "리뷰 삭제 API.")
    public ResponseEntity<ResponseMessage<Object>> deleteReview(
            @RequestBody ReviewCodeDTO reviewCodeDTO,
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId
    ) {
        int memberCode = userService.findMemberCodeByLoginId(loginId);
        reviewService.deleteReview(reviewCodeDTO, memberCode);
        return ResponseEntity.ok(new ResponseMessage<>(200, "리뷰 삭제 성공", null));
    }

    /*
        필터 값은 필수 X.
        필터 값이 없다면 기본 최신순 리뷰 정렬
        highScore, lowScore 값 중 하나를 string 형태로 주면
        점수 별 정렬. 점수가 같다면 날짜 순 정렬

        화면을 참고해보니 더보기 버튼 클릭. 즉, 페이지네이션 X.
        요청 시, 초기에 10개씩 보냄. 이후, lastReviewCode를 주면 전체 정렬 중 그 이후 10개를 보내줌
    */
    @GetMapping("/{themeCode}")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "테마 별 리뷰 조회 API. filter 값에는 highScore, lowScore를 넣어주시면 됩니다. 만약 filter 값을 첨부하지 않으면 최신순 정렬입니다")
    public ResponseEntity<ResponseMessage<List<ReviewDTO>>> findReviewList(
        @PathVariable("themeCode") Integer themeCode,
        @PageableDefault(size = 10, page = 0) Pageable pageable,
        @RequestParam(required = false) String filter,
        @RequestAttribute(value = SERVLET_REQUEST_ATTRIBUTE_KEY, required = false) String loginId
    ) {
        List<ReviewDTO> reviews;

        if (loginId == null) {  // for guests
            reviews = reviewService.findReviewsWithFilters(themeCode, filter, pageable);
        } else {    // for members
            int memberCode = userService.findMemberCodeByLoginId(loginId);
            reviews = reviewService.findReviewsWithFilters(themeCode, filter, pageable, memberCode);
        }

        // 서비스에서 필터를 사용해 조회
        return ResponseEntity.ok(new ResponseMessage<>(200, "리뷰 조회 성공", reviews));
    }

    @GetMapping("/statistics/{themeCode}")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "테마 별 리뷰 통계 반환 API. response에 필드값이 많습니다. 맵핑해서 사용해야 할 거에요... ONE이 화면 상 가장 좌측 항목(ex. 재미 없어요~)")
    public ResponseEntity<ResponseMessage<StatisticsReviewDTO>> findReviewStatistics(
        @PathVariable("themeCode") Integer themeCode
    ) {

        // 서비스에서 필터를 사용해 조회
        StatisticsReviewDTO reviewStatistics = reviewService.findReviewStatistics(themeCode);
        return ResponseEntity.ok(new ResponseMessage<>(200, "리뷰 통계 조회 성공", reviewStatistics));
    }

    @PostMapping("/likes")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "리뷰 별 좋아요 APi.")
    public ResponseEntity<ResponseMessage<Object>> likeReview(
            @RequestBody ReviewCodeDTO reviewCodeDTO,
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId
    ) {
        int memberCode = userService.findMemberCodeByLoginId(loginId);
        reviewService.likeReview(reviewCodeDTO, memberCode);
        return ResponseEntity.ok(new ResponseMessage<>(200, "리뷰 좋아요 성공", null));
    }

    @DeleteMapping("/likes")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "리뷰 별 좋아요 취소 API.")
    public ResponseEntity<ResponseMessage<Object>> deleteLikeReview(
            @RequestBody ReviewCodeDTO reviewCodeDTO,
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId
    ) {
        int memberCode = userService.findMemberCodeByLoginId(loginId);
        reviewService.deleteLikeReview(reviewCodeDTO, memberCode);
        return ResponseEntity.ok(new ResponseMessage<>(200, "리뷰 좋아요 취소 성공", null));
    }

    @GetMapping("/user/report")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "유저 별 review의 report를 생성하는 API.")
    public ResponseEntity<ResponseMessage<ReviewReportDTO>> findReviewReport(
        @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId
    ) {
        int memberCode = userService.findMemberCodeByLoginId(loginId);

        // 서비스에서 필터를 사용해 조회
        ReviewReportDTO reviewReportDTO = reviewService.findReviewReport(memberCode);
        return ResponseEntity.ok(new ResponseMessage<>(200, "유저 리뷰 report 조회 성공", reviewReportDTO));
    }

    @GetMapping("/user")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "유저 별 작성한 리뷰를 반환하는 API. 최근 작성한 리뷰순으로 정렬해서 보내줍니다")
    public ResponseEntity<ResponseMessage<List<ReviewDTO>>> findReviewByMember(
        @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId,
        @PageableDefault(size = 10, page = 0) Pageable pageable
    ) {
        int memberCode = userService.findMemberCodeByLoginId(loginId);

        // 서비스에서 필터를 사용해 조회
        List<ReviewDTO> reviews = reviewService.findReviewByMemberCode(memberCode, pageable);
        return ResponseEntity.ok(new ResponseMessage<>(200, "유저가 작성한 리뷰 조회 성공", reviews));
    }
}
