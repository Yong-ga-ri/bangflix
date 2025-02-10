package com.swcamp9th.bangflixbackend.domain.review.dto;

import com.swcamp9th.bangflixbackend.domain.review.enums.Activity;
import com.swcamp9th.bangflixbackend.domain.review.enums.Composition;
import com.swcamp9th.bangflixbackend.domain.review.enums.HorrorLevel;
import com.swcamp9th.bangflixbackend.domain.review.enums.Interior;
import com.swcamp9th.bangflixbackend.domain.review.enums.Level;
import com.swcamp9th.bangflixbackend.domain.review.enums.Probability;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
public class UpdateReviewDTO {
    private Integer reviewCode;
    private Integer headcount;
    private Integer takenTime;
    private Integer totalScore;
    private Composition composition;
    private Level level;
    private HorrorLevel horrorLevel;
    private Activity activity;
    private Interior interior;
    private Probability probability;
    private String content;
}
