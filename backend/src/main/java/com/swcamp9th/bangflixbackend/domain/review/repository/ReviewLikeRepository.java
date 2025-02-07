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
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, ReviewLikeId> {

    @Query("SELECT " +
                "r " +
             "FROM ReviewLike r " +
             "JOIN FETCH r.review " +
             "JOIN FETCH r.member " +
            "WHERE r.review.reviewCode = :reviewCode AND r.member.memberCode = :memberCode AND r.active = true")
    ReviewLike findByMemberCodeAndReviewCode(@Param("memberCode") Integer memberCode, @Param("reviewCode") Integer reviewCode);

    @Query("SELECT " +
                "r " +
             "FROM ReviewLike r " +
             "JOIN FETCH r.review " +
             "JOIN FETCH r.member " +
            "WHERE r.review.reviewCode = :reviewCode AND r.active = true")
    List<ReviewLike> findByReviewCode(@Param("reviewCode")Integer reviewCode);

    @Query("SELECT new com.swcamp9th.bangflixbackend.domain.ranking.dto.ReviewLikeCountDTO(rl.reviewCode, COUNT(rl)) "
        + "FROM ReviewLike rl "
        + "WHERE rl.createdAt > :oneWeekAgo AND rl.active = true "
        + "GROUP BY rl.reviewCode ORDER BY COUNT(rl) DESC")
    List<ReviewLikeCountDTO> findTop5ReviewCodes(@Param("oneWeekAgo") LocalDateTime oneWeekAgo);

    @Query("SELECT rl FROM ReviewLike rl "
        + "JOIN FETCH rl.review JOIN FETCH rl.review.member "
        + "WHERE rl.active = true "
        + "GROUP BY rl.reviewCode "
        + "ORDER BY COUNT(rl) DESC")
    Page<ReviewLike> findReviewByReviewLikes(Pageable pageable);

    @Query("SELECT rl FROM ReviewLike rl "
        + "JOIN FETCH rl.review JOIN FETCH rl.review.theme JOIN FETCH rl.review.theme.store "
        + "WHERE rl.active = true AND rl.review.theme.store.storeCode = :storeCode "
        + "GROUP BY rl.reviewCode "
        + "ORDER BY COUNT(rl) DESC, rl.review.createdAt DESC")
    List<ReviewLike> findBestReviewByStoreCode(@Param("storeCode")Integer storeCode);

    @Query("SELECT r FROM ReviewLike r JOIN FETCH r.review JOIN FETCH r.member "
        + "WHERE r.review.reviewCode = :reviewCode AND r.member.memberCode = :memberCode AND r.active = true")
    Optional<ReviewLike> findByReviewCodeAndMemberCode(@Param("reviewCode") Integer reviewCode, @Param("memberCode") Integer memberCode);
}