package com.swcamp9th.bangflixbackend.domain.theme.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ThemeGenreId implements Serializable{
    private Integer genreCode;
    private Integer themeCode;
}