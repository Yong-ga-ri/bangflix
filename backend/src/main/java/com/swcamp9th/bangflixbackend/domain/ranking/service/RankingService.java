package com.swcamp9th.bangflixbackend.domain.ranking.service;

import com.swcamp9th.bangflixbackend.domain.ranking.dto.MemberRankingDTO;
import com.swcamp9th.bangflixbackend.domain.ranking.dto.ReviewRankingDTO;
import com.swcamp9th.bangflixbackend.domain.ranking.dto.ReviewRankingDateDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewDTO;
import java.util.List;
import org.springframework.data.domain.Pageable;


public interface RankingService {
    void createReviewRanking();

    ReviewRankingDateDTO findReviewRankingDate(Integer year);

    List<ReviewRankingDTO> findReviewRanking(String date, String loginId);

    List<ReviewDTO> findAllReviewRanking(Pageable pageable, String loginId);

    List<MemberRankingDTO> findAllMemberRanking(Pageable pageable);
}
