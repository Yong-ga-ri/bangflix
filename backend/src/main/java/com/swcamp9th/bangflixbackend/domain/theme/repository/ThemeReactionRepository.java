package com.swcamp9th.bangflixbackend.domain.theme.repository;

import com.swcamp9th.bangflixbackend.domain.theme.entity.ReactionType;
import com.swcamp9th.bangflixbackend.domain.theme.entity.ThemeReaction;
import com.swcamp9th.bangflixbackend.domain.theme.entity.ThemeReactionId;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ThemeReactionRepository extends JpaRepository<ThemeReaction, ThemeReactionId> {

    @Query("SELECT tr " +
             "FROM ThemeReaction tr " +
             "JOIN FETCH tr.theme " +
             "JOIN FETCH tr.member " +
            "WHERE tr.active = true " +
              "AND tr.theme.themeCode = :themeCode " +
              "AND tr.member.memberCode = :memberCode ")
    Optional<ThemeReaction> findReactionByThemeCodeAndMemberCode(
            int themeCode,
            int memberCode
    );

    @Query("SELECT tr " +
             "FROM ThemeReaction tr " +
             "JOIN FETCH tr.member " +
             "JOIN FETCH tr.theme " +
            "WHERE tr.theme.active = true " +
              "AND tr.memberCode = :memberCode " +
              "AND tr.reaction IN ('LIKE', 'SCRAPLIKE') " +
            "ORDER BY tr.createdAt desc")
    List<ThemeReaction> findLikeReactionsByMemberCode(
            Pageable pageable,
            @Param("memberCode") int memberCode
    );

    @Query("SELECT tr " +
             "FROM ThemeReaction tr " +
             "JOIN FETCH tr.member " +
             "JOIN FETCH tr.theme " +
            "WHERE tr.theme.active = true " +
              "AND tr.memberCode = :memberCode " +
              "AND tr.reaction IN ('SCRAP', 'SCRAPLIKE') " +
            "ORDER BY tr.createdAt desc")
    List<ThemeReaction> findScrapReactionsByMemberCode(
            Pageable pageable,
            @Param("memberCode") int memberCode
    );

    @Query("SELECT tr " +
             "FROM ThemeReaction tr " +
             "JOIN FETCH tr.member " +
             "JOIN FETCH tr.theme " +
            "WHERE tr.theme.active = true " +
              "AND tr.memberCode = :memberCode " +
              "AND tr.reaction IN :reactions " +
            "ORDER BY tr.createdAt desc")
    List<ThemeReaction> findThemeReactionsByMemberCodeAndReactionType(
            @Param("memberCode") int memberCode,
            @Param("reactions") List<ReactionType> reactions
    );
}
