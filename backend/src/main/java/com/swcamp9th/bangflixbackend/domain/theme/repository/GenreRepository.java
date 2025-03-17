package com.swcamp9th.bangflixbackend.domain.theme.repository;

import com.swcamp9th.bangflixbackend.domain.theme.entity.Genre;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {

    @Query("SELECT g.name " +
             "FROM Genre g " +
            "INNER JOIN ThemeGenre tg ON  g.genreCode = tg.genreCode " +
            "WHERE g.genreCode IN :genreCodes")
    List<String> findGenreNames(@Param("genreCodes") List<Integer> genreCodes);

}
