package com.swcamp9th.bangflixbackend.domain.theme.repository;

import com.swcamp9th.bangflixbackend.domain.theme.entity.Theme;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ThemeRepository extends JpaRepository<Theme, Integer> {

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
    Integer countLikesByThemeCode(
            @Param("themeCode") int themeCode
    );

    @Query("SELECT " +
                  "COUNT(r) " +
             "FROM ThemeReaction r " +
            "WHERE r.theme.themeCode = :themeCode " +
              "AND r.active = true " +
              "AND r.reaction IN ('SCRAP', 'SCRAPLIKE')")
    Integer countScrapsByThemeCode(
            @Param("themeCode") int themeCode
    );

    @Query("SELECT " +
                  "DISTINCT t " +
             "FROM Theme t " +
             "LEFT JOIN ThemeGenre tg " +
                  "ON tg.themeCode = t.themeCode " +
             "LEFT JOIN Genre g " +
                  "ON tg.genreCode = g.genreCode " +
            "WHERE t.active = true " +
              "AND ((:genres) IS NULL OR g.name IN :genres) " +
              "AND ((:search) IS NULL OR :search = '' OR t.name LIKE CONCAT('%', :search, '%'))")
    List<Theme> findThemesBy(
            @Param("genres") List<String> genres,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT t " +
             "FROM Theme t " +
             "JOIN Store s " +
            "WHERE t.active = true " +
              "AND s.storeCode = :storeCode ")
    List<Theme> findThemeListByStoreCode(
            int storeCode,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"store"})
    @Query("SELECT " +
                  "DISTINCT t " +
             "FROM Theme t " +
             "JOIN ThemeReaction tr ON t.themeCode = tr.themeCode " +
            "WHERE tr.createdAt > :oneWeekAgo " +
              "AND tr.active = true " +
              "AND t.active = true " +
            "GROUP BY t " +
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
