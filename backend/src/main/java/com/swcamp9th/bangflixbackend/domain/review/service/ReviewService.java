package com.swcamp9th.bangflixbackend.domain.review.service;

import com.swcamp9th.bangflixbackend.domain.review.dto.CreateReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewCodeDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewReportDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.StatisticsReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.entity.Review;
import java.util.List;

import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ReviewService {

    void createReview(CreateReviewDTO newReview, List<MultipartFile> images, Member member);

    void deleteReview(ReviewCodeDTO reviewCodeDTO, int memberCode);

    List<ReviewDTO> findReviewsWithFilters(Integer themeCode, String filter, Pageable pageable);
    List<ReviewDTO> findReviewsWithFilters(Integer themeCode, String filter, Pageable pageable, int memberCode);

    void likeReview(ReviewCodeDTO reviewCodeDTO, String loginId);

    void deleteLikeReview(ReviewCodeDTO reviewCodeDTO, String loginId);

    StatisticsReviewDTO findReviewStatistics(Integer themeCode);

    List<ReviewDTO> getReviewDTOS(List<Review> sublist);
    List<ReviewDTO> getReviewDTOS(List<Review> sublist, int memberCode);

    ReviewDTO getReviewDTO(Review review, Integer memberCode);

    ReviewReportDTO findReviewReport(String loginId);

    List<ReviewDTO> findReviewByMember(String loginId, Pageable pageable);

    ReviewDTO findReviewDetail(String loginId, Integer reviewCode);
}
