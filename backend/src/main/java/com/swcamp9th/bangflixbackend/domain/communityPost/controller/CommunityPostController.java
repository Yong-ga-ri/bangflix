package com.swcamp9th.bangflixbackend.domain.communitypost.controller;

import com.swcamp9th.bangflixbackend.shared.response.ResponseCode;
import com.swcamp9th.bangflixbackend.shared.response.SuccessResponse;
import com.swcamp9th.bangflixbackend.domain.comment.dto.CommentCountDTO;
import com.swcamp9th.bangflixbackend.domain.comment.dto.CommentDTO;
import com.swcamp9th.bangflixbackend.domain.comment.dto.CommentCreateDTO;
import com.swcamp9th.bangflixbackend.domain.comment.dto.CommentUpdateDTO;
import com.swcamp9th.bangflixbackend.domain.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.swcamp9th.bangflixbackend.shared.filter.RequestFilter.SERVLET_REQUEST_ATTRIBUTE_KEY;

@Slf4j
@RestController
@RequestMapping("api/v1/community/post/{communityPostCode}/comments")
public class CommunityPostController {

    private final CommentService commentService;

    @Autowired
    public CommunityPostController(CommentService commentService) {
        this.commentService = commentService;
    }

    /* 댓글 등록 */
    @PostMapping
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "커뮤니티 게시글의 댓글 등록 API")
    public ResponseEntity<SuccessResponse<Void>> createComment(
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId,
            @PathVariable("communityPostCode") Integer communityPostCode,
            @RequestBody CommentCreateDTO newComment
    ) {
        commentService.createComment(loginId, communityPostCode, newComment);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(SuccessResponse.empty(ResponseCode.CREATED));
    }

    /* 댓글 수정 */
    @PutMapping("/{commentCode}")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "커뮤니티 게시글의 댓글 수정 API")
    public ResponseEntity<SuccessResponse<Void>> updateComment(
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId,
            @PathVariable("communityPostCode") Integer communityPostCode,
            @PathVariable("commentCode") Integer commentCode,
            @RequestBody CommentUpdateDTO modifiedComment
    ) {
        commentService.updateComment(loginId, communityPostCode, commentCode, modifiedComment);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(SuccessResponse.empty(ResponseCode.UPDATED));
    }

    /* 댓글 삭제 */
    @DeleteMapping("/{commentCode}")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "커뮤니티 게시글의 댓글 삭제 API")
    public ResponseEntity<SuccessResponse<Void>> deleteComment(
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId,
            @PathVariable("communityPostCode") Integer communityPostCode,
            @PathVariable("commentCode") Integer commentCode
    ) {
        commentService.deleteComment(loginId, communityPostCode, commentCode);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(SuccessResponse.empty(ResponseCode.DELETED));
    }

    /* 게시글의 댓글 목록 조회 */
    @GetMapping
    @Operation(summary = "커뮤니티 게시글의 댓글 목록 조회 API")
    public ResponseEntity<SuccessResponse<List<CommentDTO>>> getAllComments(
            @PathVariable("communityPostCode") Integer communityPostCode
    ) {
        List<CommentDTO> commentDTOList = commentService.getAllCommentsOfPost(communityPostCode);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(ResponseCode.OK, commentDTOList));
    }

    /* 게시글의 댓글 개수 조회 */
    @GetMapping("/count")
    @Operation(summary = "커뮤니티 게시글의 댓글 개수 조회 API")
    public ResponseEntity<SuccessResponse<CommentCountDTO>> getCommentCount(
            @PathVariable("communityPostCode") Integer communityPostCode
    ) {
        CommentCountDTO commentCount = commentService.getCommentCount(communityPostCode);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.of(ResponseCode.OK, commentCount));
    }
}
