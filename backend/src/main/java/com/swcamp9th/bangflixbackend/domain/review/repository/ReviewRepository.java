package com.swcamp9th.bangflixbackend.domain.review.repository;

import com.swcamp9th.bangflixbackend.domain.review.dto.StatisticsReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.entity.Review;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ReviewRepository extends JpaRepository<Review, Integer> {

    @Query("SELECT r " +
             "FROM Review r " +
             "JOIN FETCH r.member " +
             "JOIN FETCH r.theme " +
            "WHERE r.theme.themeCode = :themeCode " +
              "AND r.active = true " +
            "ORDER BY r.createdAt desc ")
    List<Review> findReviewListByThemeCode(
            Pageable pageable,
            @Param("themeCode") int themeCode
    );

    @Query("SELECT new com.swcamp9th.bangflixbackend.domain.review.dto.StatisticsReviewDTO(" +

        // 평균 점수 계산
        "AVG(r.totalScore), " +

        // Score 비율 계산
        "COUNT(CASE WHEN r.totalScore = 5 THEN 1 END) / COUNT(r.totalScore) * 100, " +
        "COUNT(CASE WHEN r.totalScore = 4 THEN 1 END) / COUNT(r.totalScore) * 100, " +
        "COUNT(CASE WHEN r.totalScore = 3 THEN 1 END) / COUNT(r.totalScore) * 100, " +
        "COUNT(CASE WHEN r.totalScore = 2 THEN 1 END) / COUNT(r.totalScore) * 100, " +
        "COUNT(CASE WHEN r.totalScore = 1 THEN 1 END) / COUNT(r.totalScore) * 100, " +

        // Level 비율 계산
        "COUNT(CASE WHEN r.level = 'ONE' THEN 1  END) / COUNT(r) * 100, " +
        "COUNT(CASE WHEN r.level = 'TWO' THEN 1  END) / COUNT(r) * 100, " +
        "COUNT(CASE WHEN r.level = 'THREE' THEN 1  END) / COUNT(r) * 100, " +
        "COUNT(CASE WHEN r.level = 'FOUR' THEN 1  END) / COUNT(r) * 100, " +
        "COUNT(CASE WHEN r.level = 'FIVE' THEN 1  END) / COUNT(r) * 100, " +

        // HorrorLevel 비율 계산
        "COUNT(CASE WHEN r.horrorLevel = 'ONE' THEN 1  END) / COUNT(r) * 100, " +
        "COUNT(CASE WHEN r.horrorLevel = 'TWO' THEN 1  END) / COUNT(r) * 100, " +
        "COUNT(CASE WHEN r.horrorLevel = 'THREE' THEN 1  END) / COUNT(r) * 100, " +
        "COUNT(CASE WHEN r.horrorLevel = 'FOUR' THEN 1  END) / COUNT(r) * 100, " +
        "COUNT(CASE WHEN r.horrorLevel = 'FIVE' THEN 1  END) / COUNT(r) * 100, " +

        // Active 비율 계산
        "COUNT(CASE WHEN r.activity = 'ONE' THEN 1  END) / COUNT(r) * 100, " +
        "COUNT(CASE WHEN r.activity = 'TWO' THEN 1  END) / COUNT(r) * 100, " +
        "COUNT(CASE WHEN r.activity = 'THREE' THEN 1  END) / COUNT(r) * 100, " +
        "COUNT(CASE WHEN r.activity = 'FOUR' THEN 1  END) / COUNT(r) * 100, " +
        "COUNT(CASE WHEN r.activity = 'FIVE' THEN 1  END) / COUNT(r) * 100, " +

        // Interior 비율 계산
        "COUNT(CASE WHEN r.interior = 'ONE' THEN 1  END) / COUNT(r) * 100, " +
        "COUNT(CASE WHEN r.interior = 'TWO' THEN 1  END) / COUNT(r) * 100, " +
        "COUNT(CASE WHEN r.interior = 'THREE' THEN 1  END) / COUNT(r) * 100, " +
        "COUNT(CASE WHEN r.interior = 'FOUR' THEN 1  END) / COUNT(r) * 100, " +
        "COUNT(CASE WHEN r.interior = 'FIVE' THEN 1  END) / COUNT(r) * 100, " +

        // Probability 비율 계산
        "COUNT(CASE WHEN r.probability = 'ONE' THEN 1  END) / COUNT(r) * 100, " +
        "COUNT(CASE WHEN r.probability = 'TWO' THEN 1  END) / COUNT(r) * 100, " +
        "COUNT(CASE WHEN r.probability = 'THREE' THEN 1  END) / COUNT(r) * 100, " +
        "COUNT(CASE WHEN r.probability = 'FOUR' THEN 1  END) / COUNT(r) * 100, " +
        "COUNT(CASE WHEN r.probability = 'FIVE' THEN 1  END) / COUNT(r) * 100, " +

        // Composition 비율 계산
        "COUNT(CASE WHEN r.composition = 'ONE' THEN 1  END) / COUNT(r) * 100, " +
        "COUNT(CASE WHEN r.composition = 'TWO' THEN 1  END) / COUNT(r) * 100, " +
        "COUNT(CASE WHEN r.composition = 'THREE' THEN 1  END) / COUNT(r) * 100, " +
        "COUNT(CASE WHEN r.composition = 'FOUR' THEN 1  END) / COUNT(r) * 100, " +
        "COUNT(CASE WHEN r.composition = 'FIVE' THEN 1  END) / COUNT(r) * 100" +
            ") " +

        "FROM Review r " +
            "WHERE r.active = true " +
            "AND r.theme.themeCode = :themeCode "
    )
    Optional<StatisticsReviewDTO> findStatisticsByThemeCode(
            @Param("themeCode") int themeCode
    );

    @Query("SELECT AVG(r.totalScore) " +
             "FROM Review r " +
             "JOIN r.member " +
            "WHERE r.active = true " +
              "AND r.member.memberCode = :memberCode")
    Integer findAvgScoreByMemberCode(
            @Param("memberCode") int memberCode
    );

    @Query("SELECT g.name " +
            "FROM Review r " +
            "INNER JOIN r.theme t " +
            "INNER JOIN ThemeGenre tg ON t.themeCode = tg.theme.themeCode " +
            "INNER JOIN Genre g ON tg.genre.genreCode = g.genreCode " +
            "WHERE r.member.memberCode = :memberCode AND t.active = true " +
            "GROUP BY g.name " +
            "ORDER BY COUNT(g) DESC")
    List<String> findTopGenresByMemberCode(
            Pageable pageable,
            @Param("memberCode") int memberCode
    );

    @Query("SELECT r " +
             "FROM Review r " +
             "JOIN FETCH r.member " +
            "WHERE r.active = true " +
              "AND r.member.memberCode = :memberCode " +
            "ORDER BY r.createdAt DESC ")
    List<Review> findByMemberCode(
            Pageable pageable,
            @Param("memberCode") int memberCode
    );
}
