package com.swcamp9th.bangflixbackend.domain.theme.repository;

import com.swcamp9th.bangflixbackend.domain.theme.entity.Theme;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ThemeRepository extends JpaRepository<Theme, Integer> {

    @Query("SELECT " +
                  "DISTINCT t " +
             "FROM Theme t " +
             "JOIN ThemeGenre tg " +
             "JOIN Genre g " +
            "WHERE g.name IN :genres " +
              "AND t.active = true " +
              "AND (:search IS NULL OR t.name LIKE CONCAT('%', :search, '%')) ")
    List<Theme> findThemesByAllGenresAndSearch(
            List<String> genres,
            String search
    );

    @Query("SELECT " +
                  "COUNT(r) " +
             "FROM Review r " +
            "WHERE r.active = true " +
              "AND r.theme.themeCode = :themeCode")
    Integer countReviewsByThemeCode(int themeCode);

    @Query("SELECT " +
                  "COUNT(r) " +
             "FROM ThemeReaction r " +
            "WHERE r.theme.themeCode = :themeCode " +
              "AND r.active = true " +
              "AND r.reaction IN ('LIKE', 'SCRAPLIKE')")
    Integer countLikesByThemeCode(@Param("themeCode") int themeCode);

    @Query("SELECT " +
                  "COUNT(r) " +
             "FROM ThemeReaction r " +
            "WHERE r.theme.themeCode = :themeCode " +
              "AND r.active = true " +
              "AND r.reaction IN ('SCRAP', 'SCRAPLIKE')")
    Integer countScrapsByThemeCode(@Param("themeCode") int themeCode);

    @Query("SELECT " +
                  "DISTINCT t " +
             "FROM Theme t " +
             "JOIN ThemeGenre tg " +
             "JOIN Genre g " +
            "WHERE t.active = true " +
              "AND g.name IN :genres ")
    List<Theme> findThemesByAllGenres(List<String> genres);

    @Query("SELECT t " +
             "FROM Theme t " +
            "WHERE t.active = true " +
              "AND (:search IS NULL OR t.name LIKE CONCAT('%', :search, '%'))")
    List<Theme> findThemesBySearch(@Param("search") String search);

    @Query("SELECT t " +
             "FROM Theme t " +
             "JOIN Store s " +
            "WHERE t.active = true " +
              "AND s.storeCode = :storeCode ")
    List<Theme> findThemeListByStoreCode(int storeCode);

    @Query("SELECT " +
                  "DISTINCT t " +
             "FROM Theme t " +
             "JOIN ThemeReaction tr " +
            "WHERE tr.createdAt > :oneWeekAgo " +
              "AND tr.active = true " +
              "AND t.active = true " +
            "ORDER BY " +
                  "COUNT(tr) DESC, " +
                  "t.themeCode DESC")
    List<Theme> findByWeekOrderByLikes(
            @Param("oneWeekAgo") LocalDateTime oneWeekAgo,
            Pageable pageable
    );

    @Query("SELECT tg.genreCode " +
             "FROM ThemeGenre tg " +
             "JOIN Theme t " +
            "WHERE tg.themeCode IN :themeCodes")
    List<Integer> findGenresByThemeCode(
            @Param("themeCodes") List<Integer> themeCodes
    );

    @Query("SELECT t " +
             "FROM Theme t " +
            "WHERE t.active = true " +
              "AND t.themeCode IN :themeCodes " +
            "ORDER BY t.createdAt DESC")
    List<Theme> findByThemeCodes(
            @Param("themeCodes") List<Integer> themeCodes
    );
}
