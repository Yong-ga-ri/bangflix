package com.swcamp9th.bangflixbackend.domain.theme.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ThemeDTO {
    private Integer themeCode;
    private Boolean active;
    private String createdAt;
    private String name;
    private Integer level;
    private Integer timeLimit;
    private String story;
    private Integer price;
    private String posterImage;
    private String headcount;
    private Integer storeCode;
    private Integer likeCount;
    private Integer scrapCount;
    private Integer reviewCount;


}
