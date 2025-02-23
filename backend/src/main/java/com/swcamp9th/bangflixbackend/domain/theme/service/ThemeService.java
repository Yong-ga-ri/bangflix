package com.swcamp9th.bangflixbackend.domain.theme.service;

import com.swcamp9th.bangflixbackend.domain.theme.dto.FindThemeByReactionDTO;
import com.swcamp9th.bangflixbackend.domain.theme.dto.ThemeReactionDTO;
import com.swcamp9th.bangflixbackend.domain.theme.dto.GenreDTO;
import com.swcamp9th.bangflixbackend.domain.theme.dto.ThemeDTO;
import java.util.List;

import com.swcamp9th.bangflixbackend.domain.theme.entity.Theme;
import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import org.springframework.data.domain.Pageable;


public interface ThemeService {

    void createThemeReaction(Member member, ThemeReactionDTO themeReactionDTO);

    void deleteThemeReaction(int memberCode, ThemeReactionDTO themeReactionDTO);

    List<ThemeDTO> getScrapedThemeByMemberCode(int memberCode);

    List<ThemeDTO> recommendTheme(List<Integer> themeCodes);

    ThemeDTO findTheme(int themeCode, int memberCode);

    ThemeDTO findThemeDTOByThemeCode(int themeCode);

    Theme findThemeByThemeCode(int themeCode);

    List<ThemeDTO> findThemeByGenresAndSearchOrderBySort(Pageable pageable, String sort, List<String> genres, String search, int memberCode);

    List<ThemeDTO> findThemeByGenresAndSearchOrderBySort(Pageable pageable, String sort, List<String> genres, String search);

    List<ThemeDTO> findThemeDTOListByStoreCode(Pageable pageable, String sort, int storeCode, int memberCode);

    List<ThemeDTO> findThemeDTOListByStoreCode(Pageable pageable, String sort, int storeCode);

    List<FindThemeByReactionDTO> findThemeByMemberReaction(Pageable pageable, int loginId, String reaction);

    List<ThemeDTO> findThemeByWeek();

    List<ThemeDTO> findThemeByWeek(int memberCode);

    List<GenreDTO> findGenres();
}
