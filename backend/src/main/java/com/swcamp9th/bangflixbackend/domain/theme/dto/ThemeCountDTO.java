package com.swcamp9th.bangflixbackend.domain.theme.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThemeCountDTO {
    private Long likeCount;
    private Long scrapCount;
    private Long reviewCount;
}


