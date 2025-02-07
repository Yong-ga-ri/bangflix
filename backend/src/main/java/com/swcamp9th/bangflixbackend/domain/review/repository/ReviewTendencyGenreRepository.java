package com.swcamp9th.bangflixbackend.domain.review.repository;

import com.swcamp9th.bangflixbackend.domain.review.entity.ReviewTendencyGenre;
import com.swcamp9th.bangflixbackend.domain.review.entity.ReviewTendencyGenreId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewTendencyGenreRepository extends JpaRepository<ReviewTendencyGenre, ReviewTendencyGenreId> {

    @Query("SELECT t FROM ReviewTendencyGenre t JOIN FETCH t.tendency JOIN FETCH t.genre "
        + "WHERE t.tendency.tendencyCode = :tendencyCode")
    List<ReviewTendencyGenre> findByTendency_TendencyCode(@Param("tendencyCode") Integer tendencyCode);

    @Query("SELECT t FROM ReviewTendencyGenre t JOIN FETCH t.tendency JOIN FETCH t.genre "
        + "WHERE t.tendency.member.memberCode = :memberCode")
    List<ReviewTendencyGenre> findMemberGenreByMemberCode(@Param("memberCode") Integer memberCode);
}
