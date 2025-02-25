package com.swcamp9th.bangflixbackend.domain.theme.dto.mapper;

import com.swcamp9th.bangflixbackend.domain.theme.dto.ThemeDTO;
import com.swcamp9th.bangflixbackend.domain.theme.entity.ReactionType;

public class ReactionMapper {

    public static void applyReaction(ThemeDTO themeDto, ReactionType reactionType) {
        if (reactionType == ReactionType.LIKE || reactionType == ReactionType.SCRAPLIKE) {
            themeDto.setIsLike(true);
        }
        if (reactionType == ReactionType.SCRAP || reactionType == ReactionType.SCRAPLIKE) {
            themeDto.setIsScrap(true);
        }
    }
}
