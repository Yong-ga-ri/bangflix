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

/**
 * ReviewController는 리뷰 생성, 삭제, 조회 등 리뷰 관련 API를 제공
 */
@RestController
@RequestMapping("api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    @Autowired
    public ReviewController(ReviewService reviewService, UserService userService) {
        this.reviewService = reviewService;
        this.userService = userService;
    }

    /**
     * 리뷰 생성 API.
     * <p>
     * 요청은 multipart/form-data 형식으로 이루어지며, 아래 두 개의 키를 포함합니다.
     * <ul>
     *   <li><b>review</b>: 리뷰 작성에 필요한 데이터를 담은 DTO (JSON 형식)</li>
     *   <li><b>images</b>: 첨부할 이미지 파일들을 담은 MultipartFile 리스트 (선택적)</li>
     * </ul>
     * 토큰에서 추출한 loginId를 이용하여 로그인한 회원 정보를 확인한 후 리뷰를 생성합니다.
     *
     * @param newReview DTO 형식의 리뷰 작성 데이터 (review 키)
     * @param images    첨부할 이미지 파일 리스트 (images 키, 선택적)
     * @param loginId   인증 토큰에서 추출한 로그인 아이디
     * @return 리뷰 작성 성공 메시지를 포함한 응답
     */
    @PostMapping
    @SecurityRequirement(name = "Authorization")
    @Operation(
            summary = "리뷰 생성 API",
            description = "multipart/form-data 형식으로 요청합니다. review 키에는 리뷰 작성에 필요한 DTO 데이터가, "
                    + "images 키에는 첨부할 이미지 파일(MultipartFile)이 포함될 수 있습니다."
    )
    public ResponseEntity<ResponseMessage<Object>> createReview(
        @RequestPart("review") CreateReviewDTO newReview,
        @RequestPart(value = "images", required = false) List<MultipartFile> images,
        @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId
    ) {
        Member member = userService.findMemberByLoginId(loginId);
        reviewService.createReview(newReview, images, member);
        return ResponseEntity.ok(new ResponseMessage<>(200, "리뷰 작성 성공", null));
    }


    /**
     * 리뷰 삭제 API.
     * <p>
     * 요청 본문에 리뷰 코드를 포함한 JSON 데이터를 전달받으며, 로그인한 회원의 권한을 확인한 후 리뷰를 삭제합니다.
     *
     * @param reviewCodeDTO 리뷰 코드가 담긴 DTO
     * @param loginId       인증 토큰에서 추출한 로그인 아이디
     * @return 리뷰 삭제 성공 메시지를 포함한 응답
     */
    @DeleteMapping
    @SecurityRequirement(name = "Authorization")
    @Operation(
            summary = "리뷰 삭제 API",
            description = "리뷰 코드를 포함한 JSON 데이터를 전달받습니다. 로그인한 회원의 정보(loginId)를 이용해 삭제 권한을 확인한 후 리뷰를 삭제합니다."
    )
    public ResponseEntity<ResponseMessage<Object>> deleteReview(
            @RequestBody ReviewCodeDTO reviewCodeDTO,
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId
    ) {
        int memberCode = userService.findMemberCodeByLoginId(loginId);
        reviewService.deleteReview(reviewCodeDTO, memberCode);
        return ResponseEntity.ok(new ResponseMessage<>(200, "리뷰 삭제 성공", null));
    }

    /**
     * 테마별 리뷰 조회 API.
     * <p>
     * 경로 변수로 테마 코드를, 쿼리 파라미터로 정렬 필터와 페이징 정보를 전달받습니다.
     * 정렬 필터는 "highScore" 또는 "lowScore" 값을 사용하며, 지정하지 않을 경우 최신순(생성일 내림차순)으로 정렬됩니다.
     * 로그인 여부에 따라 회원용과 게스트용 조회 로직이 분기됩니다.
     *
     * @param themeCode 테마 코드 (경로 변수)
     * @param pageable  페이징 정보 (기본적으로 10개씩 반환)
     * @param filter    정렬 필터 (optional, "highScore" 또는 "lowScore")
     * @param loginId   인증 토큰에서 추출한 로그인 아이디 (optional, 게스트의 경우 null)
     * @return 해당 테마의 리뷰 목록을 포함한 응답
     */
    @GetMapping("/{themeCode}")
    @SecurityRequirement(name = "Authorization")
    @Operation(
            summary = "테마별 리뷰 조회 API",
            description = "테마 코드를 경로 변수로 전달하며, 정렬 필터로 highScore 또는 lowScore를 지정할 수 있습니다. "
                    + "필터가 없으면 기본적으로 최신순(생성일 내림차순)으로 정렬됩니다. "
                    + "로그인 여부에 따라 회원용과 게스트용 조회 로직이 분기됩니다."
    )
    public ResponseEntity<ResponseMessage<List<ReviewDTO>>> findReviewList(
        @PathVariable("themeCode") Integer themeCode,
        @PageableDefault(size = 10, page = 0) Pageable pageable,
        @RequestParam(required = false) String filter,
        @RequestAttribute(value = SERVLET_REQUEST_ATTRIBUTE_KEY, required = false) String loginId
    ) {
        List<ReviewDTO> reviews;

        if (loginId == null) {  // for guests
            reviews = reviewService.findReviewsBy(themeCode, filter, pageable);
        } else {    // for members
            int memberCode = userService.findMemberCodeByLoginId(loginId);
            reviews = reviewService.findReviewsBy(themeCode, filter, pageable, memberCode);
        }

        return ResponseEntity.ok(new ResponseMessage<>(200, "리뷰 조회 성공", reviews));
    }

    /**
     * 테마별 리뷰 통계 조회 API.
     * <p>
     * 특정 테마에 대한 리뷰 통계 정보를 반환합니다.
     * 응답 데이터에는 점수 분포, 리뷰 개수 등 다양한 통계 항목이 포함되어 있습니다.
     *
     * @param themeCode 테마 코드 (경로 변수)
     * @return 해당 테마의 리뷰 통계 데이터를 포함한 응답
     */
    @GetMapping("/statistics/{themeCode}")
    @SecurityRequirement(name = "Authorization")
    @Operation(
            summary = "테마별 리뷰 통계 조회 API",
            description = "특정 테마에 대한 리뷰 통계 정보를 반환합니다. 응답 데이터는 다양한 통계 항목을 포함하므로, 클라이언트에서 필요한 데이터만 매핑하여 사용하세요."
    )
    public ResponseEntity<ResponseMessage<StatisticsReviewDTO>> findReviewStatistics(
        @PathVariable("themeCode") Integer themeCode
    ) {
        StatisticsReviewDTO reviewStatistics = reviewService.findReviewStatistics(themeCode);
        return ResponseEntity.ok(new ResponseMessage<>(200, "리뷰 통계 조회 성공", reviewStatistics));
    }

    /**
     * 리뷰 좋아요 API.
     * <p>
     * 요청 본문에 리뷰 코드를 포함한 JSON 데이터를 전달받으며, 로그인한 회원이 해당 리뷰에 좋아요를 등록합니다.
     *
     * @param reviewCodeDTO 리뷰 코드가 담긴 DTO
     * @param loginId       인증 토큰에서 추출한 로그인 아이디
     * @return 리뷰 좋아요 등록 성공 메시지를 포함한 응답
     */
    @PostMapping("/likes")
    @SecurityRequirement(name = "Authorization")
    @Operation(
            summary = "리뷰 좋아요 API",
            description = "리뷰 코드를 포함한 JSON 데이터를 전달받아, 로그인한 회원이 해당 리뷰에 좋아요를 등록합니다."
    )
    public ResponseEntity<ResponseMessage<Object>> likeReview(
            @RequestBody ReviewCodeDTO reviewCodeDTO,
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId
    ) {
        int memberCode = userService.findMemberCodeByLoginId(loginId);
        reviewService.likeReview(reviewCodeDTO, memberCode);
        return ResponseEntity.ok(new ResponseMessage<>(200, "리뷰 좋아요 성공", null));
    }

    /**
     * 리뷰 좋아요 취소 API.
     * <p>
     * 요청 본문에 리뷰 코드를 포함한 JSON 데이터를 전달받으며, 로그인한 회원이 해당 리뷰의 좋아요를 취소합니다.
     *
     * @param reviewCodeDTO 리뷰 코드가 담긴 DTO
     * @param loginId       인증 토큰에서 추출한 로그인 아이디
     * @return 리뷰 좋아요 취소 성공 메시지를 포함한 응답
     */
    @DeleteMapping("/likes")
    @SecurityRequirement(name = "Authorization")
    @Operation(
            summary = "리뷰 좋아요 취소 API",
            description = "리뷰 코드를 포함한 JSON 데이터를 전달받아, 로그인한 회원이 해당 리뷰의 좋아요를 취소합니다."
    )
    public ResponseEntity<ResponseMessage<Object>> deleteLikeReview(
            @RequestBody ReviewCodeDTO reviewCodeDTO,
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId
    ) {
        int memberCode = userService.findMemberCodeByLoginId(loginId);
        reviewService.deleteLikeReview(reviewCodeDTO, memberCode);
        return ResponseEntity.ok(new ResponseMessage<>(200, "리뷰 좋아요 취소 성공", null));
    }

    /**
     * 유저 리뷰 리포트 조회 API.
     * <p>
     * 로그인한 회원의 작성한 리뷰에 대한 리포트를 생성하여 반환합니다.
     *
     * @param loginId 인증 토큰에서 추출한 로그인 아이디
     * @return 유저 리뷰 리포트 데이터를 포함한 응답
     */
    @GetMapping("/user/report")
    @SecurityRequirement(name = "Authorization")
    @Operation(
            summary = "유저 리뷰 리포트 조회 API",
            description = "로그인한 회원의 리뷰 리포트를 생성하여 반환합니다. 응답 데이터는 다양한 통계 정보를 포함하므로, 클라이언트에서 필요한 데이터만 추출하여 사용하세요."
    )
    public ResponseEntity<ResponseMessage<ReviewReportDTO>> findReviewReport(
        @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId
    ) {
        int memberCode = userService.findMemberCodeByLoginId(loginId);

        ReviewReportDTO reviewReportDTO = reviewService.findReviewReport(memberCode);
        return ResponseEntity.ok(new ResponseMessage<>(200, "유저 리뷰 report 조회 성공", reviewReportDTO));
    }

    /**
     * 유저 작성 리뷰 조회 API.
     * <p>
     * 로그인한 회원이 작성한 리뷰를 최신순(생성일 내림차순)으로 페이징 처리하여 반환합니다.
     *
     * @param loginId  인증 토큰에서 추출한 로그인 아이디
     * @param pageable 페이징 정보 (기본적으로 10개씩 반환)
     * @return 유저가 작성한 리뷰 목록을 포함한 응답
     */
    @GetMapping("/user")
    @SecurityRequirement(name = "Authorization")
    @Operation(
            summary = "유저 작성 리뷰 조회 API",
            description = "로그인한 회원이 작성한 리뷰를 최신순(생성일 내림차순)으로 정렬하여 페이징 처리된 결과를 반환합니다."
    )
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
