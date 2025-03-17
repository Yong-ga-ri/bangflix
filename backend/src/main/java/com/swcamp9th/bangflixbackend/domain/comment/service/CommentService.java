package com.swcamp9th.bangflixbackend.domain.comment.service;

import com.swcamp9th.bangflixbackend.domain.comment.dto.CommentCountDTO;
import com.swcamp9th.bangflixbackend.domain.comment.dto.CommentDTO;
import com.swcamp9th.bangflixbackend.domain.comment.dto.CommentCreateDTO;
import com.swcamp9th.bangflixbackend.domain.comment.dto.CommentUpdateDTO;

import java.util.List;

public interface CommentService {

    void createComment(String loginId, Integer communityPostCode, CommentCreateDTO newComment);

    void updateComment(String loginId, Integer communityPostCode, Integer commentCode, CommentUpdateDTO modifiedComment);

    void deleteComment(String loginId, Integer communityPostCode, Integer commentCode);

    List<CommentDTO> getAllCommentsOfPost(Integer communityPostCode);

    CommentCountDTO getCommentCount(Integer communityPostCode);

    List<CommentDTO> getCommentsById(String loginId);
}
