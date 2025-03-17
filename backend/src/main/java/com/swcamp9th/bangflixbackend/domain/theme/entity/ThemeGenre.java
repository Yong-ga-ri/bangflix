package com.swcamp9th.bangflixbackend.domain.theme.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "theme_genre")
@IdClass(ThemeGenre.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ThemeGenre {

    @Id
    @Column(name = "genre_code", nullable = false)
    private Integer genreCode;

    @Id
    @Column(name = "theme_code", nullable = false)
    private Integer themeCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genre_code", insertable = false, updatable = false)
    private Genre genre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_code", insertable = false, updatable = false)
    private Theme theme;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ThemeGenre that = (ThemeGenre) o;
        return Objects.equals(genreCode, that.genreCode) && Objects.equals(themeCode, that.themeCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(genreCode, themeCode);
    }
}
