package com.swcamp9th.bangflixbackend.domain.theme.repository;

import com.swcamp9th.bangflixbackend.domain.theme.entity.Theme;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Integer> {

    @Query("SELECT t " +
             "FROM Theme t " +
             "JOIN ThemeGenre tg " +
//                   "ON t.themeCode = tg.theme.themeCode " +
             "JOIN Genre g " +
//                   "ON tg.genreCode = g.genreCode " +
            "WHERE g.name IN :genres " +
              "AND t.active = true " +
              "AND (:search IS NULL OR t.name LIKE CONCAT('%', :search, '%')) ")
    Optional<List<Theme>> findThemesByAllGenresAndSearch(List<String> genres, String search);

    // 추가적인 쿼리 메소드로 리뷰 개수, 좋아요 개수, 스크랩 개수를 구하는 메소드
    @Query("SELECT " +
            "COUNT(r) " +
            "FROM Review r " +
            "WHERE r.theme.themeCode = :themeCode " +
            "AND r.active = true")
    Integer countReviewsByThemeCode(int themeCode);

    @Query("SELECT " +
            "COUNT(r) " +
            "FROM ThemeReaction r " +
            "WHERE r.theme.themeCode = :themeCode " +
            "AND (r.reaction = 'LIKE' OR r.reaction = 'SCRAPLIKE') " +
            "AND r.active = true")
    Integer countLikesByThemeCode(int themeCode);

    @Query("SELECT COUNT(r) " +
            "FROM ThemeReaction r " +
            "WHERE r.theme.themeCode = :themeCode " +
            "AND (r.reaction = 'SCRAP' OR r.reaction = 'SCRAPLIKE') " +
            "AND r.active = true")
    Integer countScrapsByThemeCode(int themeCode);

    @Query("SELECT t " +
            "FROM Theme t " +
            "INNER JOIN ThemeGenre tg " +
            "ON t.themeCode = tg.theme.themeCode " +
            "INNER JOIN Genre g " +
            "ON tg.genreCode = g.genreCode " +
            "WHERE g.name IN :genres AND t.active = true " + // 장르 필터
            "GROUP BY t.themeCode ")
    List<Theme> findThemesByAllGenres(List<String> genres);

    @Query("SELECT t " +
            "FROM Theme t " +
            "WHERE (t.name LIKE %:search% OR :search IS NULL) " +
            "AND t.active = true")
    List<Theme> findThemesBySearch(String search);

    @Query("SELECT t " +
            "FROM Theme t " +
            "INNER JOIN Store s ON t.store.storeCode = s.storeCode " +
            "WHERE s.storeCode = :storeCode " +
            "AND t.active = true")
    List<Theme> findByStoreCode(int storeCode);

    @Query("SELECT t " +
            "FROM Theme t " +
            "INNER JOIN ThemeReaction tr " +
            "ON t.themeCode = tr.themeCode " +
            "WHERE tr.createdAt > :oneWeekAgo " +
            "AND tr.active = true " +
            "AND t.active = true " +
            "GROUP BY t.themeCode " +
            "ORDER BY COUNT(tr) DESC, t.themeCode DESC")
    Optional<List<Theme>> findByWeekOrderByLikes(@Param("oneWeekAgo") LocalDateTime oneWeekAgo, Pageable pageable);

    @Query("SELECT tg.genreCode " +
            "FROM ThemeGenre tg " +
            "INNER JOIN Theme t " +
            "ON tg.theme.themeCode = t.themeCode " +
            "WHERE tg.themeCode IN :themeCodes")
    List<Integer> findGenresByThemeCode(@Param("themeCodes") List<Integer> themeCodes);

    @Query("SELECT t " +
            "FROM Theme t " +
            "WHERE t.themeCode IN :themeCodes " +
            "ORDER BY t.createdAt DESC")
    List<Theme> findByThemeCodes(@Param("themeCodes") List<Integer> themeCodes);
}
