package com.swcamp9th.bangflixbackend.domain.review.service;

import com.swcamp9th.bangflixbackend.domain.review.dto.CreateReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewCodeDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.UpdateReviewDTO;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ReviewService {

    void createReview(CreateReviewDTO newReview, List<MultipartFile> images) throws IOException;

    void updateReview(UpdateReviewDTO updateReview);

    void deleteReview(ReviewCodeDTO reviewCodeDTO);

    List<ReviewDTO> findReviewsWithFilters(Integer themeCode, String filter, Integer lastReviewCode);

    void likeReview(ReviewCodeDTO reviewCodeDTO);

    void deleteLikeReview(ReviewCodeDTO reviewCodeDTO);
}
