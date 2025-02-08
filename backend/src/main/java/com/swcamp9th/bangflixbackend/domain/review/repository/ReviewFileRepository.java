package com.swcamp9th.bangflixbackend.domain.review.repository;

import com.swcamp9th.bangflixbackend.domain.review.entity.ReviewFile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewFileRepository extends JpaRepository<ReviewFile, Integer> {

    @Query("SELECT " +
                "r " +
             "FROM ReviewFile r " +
             "JOIN FETCH r.review " +
             "WHERE r.review.reviewCode = :reviewCode AND r.active = true")
    List<ReviewFile> findByReview_ReviewCode(@Param("reviewCode") Integer reviewCode);
}
