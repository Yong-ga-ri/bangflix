package com.swcamp9th.bangflixbackend.domain.ranking.repository;

import com.swcamp9th.bangflixbackend.domain.ranking.entity.ReviewRanking;
import com.swcamp9th.bangflixbackend.domain.review.entity.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRankingRepository extends JpaRepository<ReviewRanking, Integer> {

    @Query("SELECT DISTINCT FUNCTION('DATE_FORMAT', rr.createdAt, '%Y-%m-%d') " +
        "FROM ReviewRanking rr " +
        "WHERE YEAR(rr.createdAt) = :year "
        + "ORDER BY rr.createdAt DESC")
    List<String> findDistinctDatesByYear(@Param("year") int year);

    @Query(value = "SELECT rr "
        + "FROM ReviewRanking rr JOIN FETCH rr.review JOIN FETCH rr.review.member " +
        "WHERE FUNCTION('DATE', rr.createdAt) = FUNCTION('STR_TO_DATE', :date, '%Y-%m-%d') AND rr.active = true")
    List<ReviewRanking> findReviewByCreatedAtDate(@Param("date") String date);

}
