package com.swcamp9th.bangflixbackend.domain.theme.dto;


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
public class GenreDTO {
    private Integer genreCode;
    private Boolean active;
    private String createdAt;
    private String name;
}
