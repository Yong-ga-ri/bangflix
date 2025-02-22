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
            "WHERE tr.theme.themeCode = :themeCode " +
                  "AND tr.member.memberCode = :memberCode " +
                  "AND tr.active = true")
    Optional<ThemeReaction> findReactionByThemeCodeAndMemberCode(Integer themeCode, int memberCode);

    @Query("SELECT tr " +
             "FROM ThemeReaction tr " +
             "JOIN FETCH tr.member " +
             "JOIN FETCH tr.theme " +
            "WHERE tr.memberCode = :memberCode " +
                  "AND tr.theme.active = true " +
                  "AND tr.reaction IN ('LIKE', 'SCRAPLIKE') " +
            "ORDER BY tr.createdAt desc")
    Optional<List<ThemeReaction>> findLikeReactionsByMemberCode(Pageable pageable, @Param("memberCode") int memberCode);

    @Query("SELECT tr " +
             "FROM ThemeReaction tr " +
             "JOIN FETCH tr.member " +
             "JOIN FETCH tr.theme " +
            "WHERE tr.memberCode = :memberCode " +
                  "AND tr.theme.active = true " +
                  "AND (tr.reaction = com.swcamp9th.bangflixbackend.domain.theme.entity.ReactionType.SCRAP " +
                  "OR tr.reaction = com.swcamp9th.bangflixbackend.domain.theme.entity.ReactionType.SCRAPLIKE) " +
            "ORDER BY tr.createdAt desc")
    Optional<List<ThemeReaction>> findScrapReactionsByMemberCode(Pageable pageable, @Param("memberCode") int memberCode);

    @Query("SELECT tr " +
             "FROM ThemeReaction tr " +
             "JOIN FETCH tr.member " +
             "JOIN FETCH tr.theme " +
            "WHERE tr.memberCode = :memberCode " +
                  "AND tr.theme.active = true " +
                  "AND tr.reaction IN :reactions " +
            "ORDER BY tr.createdAt desc")
    List<ThemeReaction> findThemeReactionsByMemberCodeAndReactionType(@Param("memberCode") int memberCode, @Param("reactions") List<ReactionType> reactions);
}
