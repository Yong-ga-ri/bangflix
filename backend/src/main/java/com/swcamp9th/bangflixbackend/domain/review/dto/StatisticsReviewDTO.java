package com.swcamp9th.bangflixbackend.domain.review.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StatisticsReviewDTO {
    private Double avgTotalScore;
    private Long fiveScorePercent;
    private Long fourScorePercent;
    private Long threeScorePercent;
    private Long twoScorePercent;
    private Long oneScorePercent;

    private Long oneLevelPercent;
    private Long twoLevelPercent;
    private Long threeLevelPercent;
    private Long fourLevelPercent;
    private Long fiveLevelPercent;

    private Long oneHorrorLevelPercent;
    private Long twoHorrorLevelPercent;
    private Long threeHorrorLevelPercent;
    private Long fourHorrorLevelPercent;
    private Long fiveHorrorLevelPercent;

    private Long oneActivePercent;
    private Long twoActivePercent;
    private Long threeActivePercent;
    private Long fourActivePercent;
    private Long fiveActivePercent;

    private Long oneInteriorPercent;
    private Long twoInteriorPercent;
    private Long threeInteriorPercent;
    private Long fourInteriorPercent;
    private Long fiveInteriorPercent;

    private Long oneProbabilityPercent;
    private Long twoProbabilityPercent;
    private Long threeProbabilityPercent;
    private Long fourProbabilityPercent;
    private Long fiveProbabilityPercent;

    private Long oneCompositionPercent;
    private Long twoCompositionPercent;
    private Long threeCompositionPercent;
    private Long fourCompositionPercent;
    private Long fiveCompositionPercent;
}
