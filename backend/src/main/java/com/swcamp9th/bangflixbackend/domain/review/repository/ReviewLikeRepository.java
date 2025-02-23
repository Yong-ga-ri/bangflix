package com.swcamp9th.bangflixbackend.domain.review.repository;

import com.swcamp9th.bangflixbackend.domain.ranking.dto.ReviewLikeCountDTO;
import com.swcamp9th.bangflixbackend.domain.review.entity.ReviewLike;
import com.swcamp9th.bangflixbackend.domain.review.entity.ReviewLikeId;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, ReviewLikeId> {

    @Query("SELECT rl " +
             "FROM ReviewLike rl " +
             "JOIN FETCH rl.review r " +
             "JOIN FETCH rl.member m " +
            "WHERE rl.active = true " +
              "AND r.active = true " +
              "AND m.active = true " +
              "AND r.reviewCode = :reviewCode " +
              "AND m.memberCode = :memberCode ")
    Optional<ReviewLike> findByMemberCodeAndReviewCode(
            @Param("memberCode") int memberCode,
            @Param("reviewCode") int reviewCode
    );

    @Query("SELECT COUNT(rl) " +
             "FROM ReviewLike rl " +
            "WHERE rl.active = true " +
              "AND rl.review.reviewCode = :reviewCode ")
    Integer countReviewLikesByReviewCode(
            @Param("reviewCode") int reviewCode
    );

    @Query("SELECT new com.swcamp9th.bangflixbackend.domain.ranking.dto.ReviewLikeCountDTO(rl.reviewCode, COUNT(rl)) " +
             "FROM ReviewLike rl " +
            "WHERE rl.createdAt > :oneWeekAgo " +
              "AND rl.active = true " +
            "GROUP BY rl.reviewCode " +
            "ORDER BY COUNT(rl) DESC ")
    List<ReviewLikeCountDTO> findTop5ReviewCodes(
            @Param("oneWeekAgo") LocalDateTime oneWeekAgo
    );

    @Query("SELECT DISTINCT rl " +
             "FROM ReviewLike rl " +
             "JOIN FETCH rl.review " +
             "JOIN FETCH rl.review.member " +
            "WHERE rl.active = true " +
            "ORDER BY COUNT(rl) DESC")
    Page<ReviewLike> findReviewByReviewLikes(Pageable pageable);

    @Query("SELECT rl " +
             "FROM ReviewLike rl " +
             "JOIN FETCH rl.review r " +
             "JOIN FETCH r.theme t " +
             "JOIN FETCH t.store s " +
            "WHERE rl.active = true " +
              "AND r.active = true " +
              "AND t.active = true " +
              "AND s.active = true " +
              "AND s.storeCode = :storeCode " +
            "GROUP BY rl.review " +
            "ORDER BY " +
                  "COUNT(rl) DESC, " +
                  "r.createdAt DESC")
    Optional<ReviewLike> findBestReviewByStoreCode(
            @Param("storeCode") int storeCode
    );

    @Query("SELECT " +
                  "CASE WHEN (COUNT(rl) > 0) THEN true ELSE false END " +
             "FROM ReviewLike rl " +
            "WHERE rl.active = true " +
              "AND rl.review.active = true " +
              "AND rl.review.member.active = true " +
              "AND rl.review.reviewCode = :reviewCode " +
              "AND rl.member.memberCode = :memberCode ")
    boolean existReviewLikeByReviewCodeAndMemberCode (
            @Param("reviewCode") int reviewCode,
            @Param("memberCode") int memberCode
    );
}