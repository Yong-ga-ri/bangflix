package com.swcamp9th.bangflixbackend.domain.communitypost.controller;

import com.swcamp9th.bangflixbackend.shared.response.ResponseCode;
import com.swcamp9th.bangflixbackend.shared.response.SuccessResponse;
import com.swcamp9th.bangflixbackend.domain.communitypost.dto.CommunityLikeCreateDTO;
import com.swcamp9th.bangflixbackend.domain.communitypost.dto.CommunityLikeCountDTO;
import com.swcamp9th.bangflixbackend.domain.communitypost.service.CommunityLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.swcamp9th.bangflixbackend.shared.filter.RequestFilter.SERVLET_REQUEST_ATTRIBUTE_KEY;

@RestController("communityLikeController")
@Slf4j
@RequestMapping("api/v1/community-like")
public class CommunityLikeController {

    private final CommunityLikeService communityLikeService;

    @Autowired
    public CommunityLikeController(CommunityLikeService communityLikeService) {
        this.communityLikeService = communityLikeService;
    }

    /* 좋아요 등록 및 취소 */
    @PostMapping
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "커뮤니티 게시글 좋아요 / 좋아요 취소 API")
    public ResponseEntity<SuccessResponse<Void>> addLike(
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId,
            @RequestBody CommunityLikeCreateDTO newLike
    ) {
        communityLikeService.addLike(loginId, newLike);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.empty(ResponseCode.DELETED));
    }

    /* 좋아요 개수 조회 */
    @GetMapping("/{communityPostCode}")
    @Operation(summary = "좋아요 개수 조회 API")
    public ResponseEntity<SuccessResponse<CommunityLikeCountDTO>> countLike(@PathVariable int communityPostCode) {
        CommunityLikeCountDTO likeCountDTO = communityLikeService.countLike(communityPostCode);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(ResponseCode.OK, likeCountDTO));
    }
}
