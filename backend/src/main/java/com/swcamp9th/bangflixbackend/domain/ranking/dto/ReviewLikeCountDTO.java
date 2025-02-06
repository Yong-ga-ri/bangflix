package com.swcamp9th.bangflixbackend.domain.ranking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReviewLikeCountDTO {
    private Integer reviewCode;
    private Long count;
}
