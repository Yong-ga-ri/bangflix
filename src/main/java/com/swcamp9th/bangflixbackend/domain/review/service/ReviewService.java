package com.swcamp9th.bangflixbackend.domain.review.service;

import com.swcamp9th.bangflixbackend.domain.review.dto.CreateReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewCodeDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewReportDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.StatisticsReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.UpdateReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.entity.Review;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ReviewService {

    void createReview(CreateReviewDTO newReview, List<MultipartFile> images, String loginId)
        throws IOException, URISyntaxException;

    void updateReview(UpdateReviewDTO updateReview, String loginId);

    void deleteReview(ReviewCodeDTO reviewCodeDTO, String loginId);

    List<ReviewDTO> findReviewsWithFilters(Integer themeCode, String filter, Pageable pageable);

    void likeReview(ReviewCodeDTO reviewCodeDTO, String loginId);

    void deleteLikeReview(ReviewCodeDTO reviewCodeDTO, String loginId);

    StatisticsReviewDTO findReviewStatistics(Integer themeCode);

    List<ReviewDTO> getReviewDTOS(List<Review> sublist);

    ReviewDTO getReviewDTO(Review review);

    ReviewReportDTO findReviewReposrt(String loginId);

    List<ReviewDTO> findReviewByMember(String loginId);
}
