package com.swcamp9th.bangflixbackend.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StatisticsReviewDTO {
    private Double avgTotalScore;
    private Integer fiveScorePercent;
    private Integer fourScorePercent;
    private Integer threeScorePercent;
    private Integer twoScorePercent;
    private Integer oneScorePercent;

    private Integer oneLevelPercent;
    private Integer twoLevelPercent;
    private Integer threeLevelPercent;
    private Integer fourLevelPercent;
    private Integer fiveLevelPercent;

    private Integer oneHorrorLevelPercent;
    private Integer twoHorrorLevelPercent;
    private Integer threeHorrorLevelPercent;
    private Integer fourHorrorLevelPercent;
    private Integer fiveHorrorLevelPercent;

    private Integer oneActivePercent;
    private Integer twoActivePercent;
    private Integer threeActivePercent;
    private Integer fourActivePercent;
    private Integer fiveActivePercent;

    private Integer oneInteriorPercent;
    private Integer twoInteriorPercent;
    private Integer threeInteriorPercent;
    private Integer fourInteriorPercent;
    private Integer fiveInteriorPercent;

    private Integer oneProbabilityPercent;
    private Integer twoProbabilityPercent;
    private Integer threeProbabilityPercent;
    private Integer fourProbabilityPercent;
    private Integer fiveProbabilityPercent;

    private Integer oneCompositionPercent;
    private Integer twoCompositionPercent;
    private Integer threeCompositionPercent;
    private Integer fourCompositionPercent;
    private Integer fiveCompositionPercent;
}
