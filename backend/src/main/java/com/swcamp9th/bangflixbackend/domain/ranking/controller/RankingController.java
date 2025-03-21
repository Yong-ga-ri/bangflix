package com.swcamp9th.bangflixbackend.domain.ranking.controller;

import com.swcamp9th.bangflixbackend.shared.response.ResponseCode;
import com.swcamp9th.bangflixbackend.shared.response.SuccessResponse;
import com.swcamp9th.bangflixbackend.domain.ranking.dto.MemberRankingDTO;
import com.swcamp9th.bangflixbackend.domain.ranking.dto.ReviewRankingDTO;
import com.swcamp9th.bangflixbackend.domain.ranking.dto.ReviewRankingDateDTO;
import com.swcamp9th.bangflixbackend.domain.ranking.service.RankingService;
import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.swcamp9th.bangflixbackend.shared.filter.RequestFilter.SERVLET_REQUEST_ATTRIBUTE_KEY;

@RestController
@RequestMapping("api/v1/rankings")
public class RankingController {

    private final RankingService rankingService;

    @Autowired
    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    @PostMapping("/test")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "수동으로 리뷰 랭킹 선정하는 API. 테스트용이므로 프론트에서 사용 안하시면 됩니다")
    public ResponseEntity<SuccessResponse<Void>> testCreateTop5Review() {

        rankingService.createReviewRanking();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.empty(ResponseCode.CREATED));
    }

    @GetMapping("/reviews/dates/{year}")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "년도 별 베스트 리뷰가 선정된 일자 반환 API.")
    public ResponseEntity<SuccessResponse<ReviewRankingDateDTO>> findReviewRankingDate(
        @PathVariable Integer year) {

        ReviewRankingDateDTO reviewRankingDateDTO = rankingService.findReviewRankingDate(year);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(ResponseCode.OK, reviewRankingDateDTO));
    }

    @GetMapping("/reviews/date")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "선정일 입력하면 해당 일에 해당하는 베스트 리뷰 반환 API. 최대 5개의 리뷰가 반환됨. (만약 date값이 없다면 가장 최신 선정된 베스트 리뷰를 반환합니다)")
    public ResponseEntity<SuccessResponse<List<ReviewRankingDTO>>> findReviewRanking(
        @RequestParam(required = false) String date,
        @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId) {

        List<ReviewRankingDTO> reviewRankingDTOList = rankingService.findReviewRanking(date, loginId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(ResponseCode.OK, reviewRankingDTOList));
    }

    @GetMapping("/reviews")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "좋아요가 많은 순으로 리뷰를 정렬해 반환하는 API.")
    public ResponseEntity<SuccessResponse<List<ReviewDTO>>> findReviewRanking(
        @PageableDefault(size = 10, page = 0) Pageable pageable,
        @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId
    ) {

        List<ReviewDTO> reviewDTOList = rankingService.findAllReviewRanking(pageable, loginId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(ResponseCode.OK, reviewDTOList));
    }

    @GetMapping("/members")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "포인트가 높은 순으로 유저를 정렬해 반환하는 API.")
    public ResponseEntity<SuccessResponse<List<MemberRankingDTO>>> findMemberRanking(
        @PageableDefault(size = 100, page = 0) Pageable pageable
    ) {

        List<MemberRankingDTO> memberRankingDTOList = rankingService.findAllMemberRanking(pageable);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(ResponseCode.OK, memberRankingDTOList));
    }
}
