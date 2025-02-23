package com.swcamp9th.bangflixbackend.domain.comment.controller;

import com.swcamp9th.bangflixbackend.shared.response.SuccessResponse;
import com.swcamp9th.bangflixbackend.domain.comment.dto.CommentDTO;
import com.swcamp9th.bangflixbackend.domain.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.swcamp9th.bangflixbackend.shared.filter.RequestFilter.SERVLET_REQUEST_ATTRIBUTE_KEY;


@Slf4j
@RestController
@RequestMapping("api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /* 사용자별 댓글 목록 조회 */
    @GetMapping
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "특정 사용자가 작성한 댓글 리스트 조회")
    public ResponseEntity<SuccessResponse<List<CommentDTO>>> getCommentsByMe(
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId
    ) {
        List<CommentDTO> foundComments = commentService.getCommentsById(loginId);

        if (foundComments.isEmpty()) foundComments = null;

        return ResponseEntity.ok(new SuccessResponse<>(200, "댓글 조회 성공", foundComments));
    }
}
