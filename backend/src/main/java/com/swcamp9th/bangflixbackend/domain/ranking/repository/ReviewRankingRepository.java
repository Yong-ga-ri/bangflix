package com.swcamp9th.bangflixbackend.domain.ranking.repository;

import com.swcamp9th.bangflixbackend.domain.ranking.entity.ReviewRanking;
import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
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
    Optional<List<String>> findDistinctDatesByYear(@Param("year") int year);

    @Query(value = "SELECT rr "
        + "FROM ReviewRanking rr JOIN FETCH rr.review JOIN FETCH rr.review.member " +
        "WHERE FUNCTION('DATE', rr.createdAt) = FUNCTION('STR_TO_DATE', :date, '%Y-%m-%d') AND rr.active = true")
    Optional<List<ReviewRanking>> findReviewByCreatedAtDate(@Param("date") String date);


    @Query(value = "SELECT m FROM Member m WHERE m.active = true ORDER BY m.point desc")
    List<Member> findTopRankingMember(Pageable pageable);
}
