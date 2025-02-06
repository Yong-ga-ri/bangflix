package com.swcamp9th.bangflixbackend.domain.ranking.dto;

import java.util.List;
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
public class ReviewRankingDateDTO {
    List<String> ReviewRankingDates;

}
