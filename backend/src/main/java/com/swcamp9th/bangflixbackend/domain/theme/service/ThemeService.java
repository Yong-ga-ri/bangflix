package com.swcamp9th.bangflixbackend.domain.theme.service;

import com.swcamp9th.bangflixbackend.domain.theme.dto.FindThemeByReactionDTO;
import com.swcamp9th.bangflixbackend.domain.theme.dto.ThemeReactionDTO;
import com.swcamp9th.bangflixbackend.domain.theme.dto.GenreDTO;
import com.swcamp9th.bangflixbackend.domain.theme.dto.ThemeDTO;
import java.util.List;

import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import org.springframework.data.domain.Pageable;

public interface ThemeService {

    void createThemeReaction(Member member, ThemeReactionDTO themeReactionDTO);

    void deleteThemeReaction(int memberCode, ThemeReactionDTO themeReactionDTO);

    List<ThemeDTO> getScrapedThemeByMemberCode(int memberCode);

    List<ThemeDTO> recommendTheme(List<Integer> themeCodes);

    ThemeDTO findTheme(Integer themeCode, int memberCode);
    ThemeDTO findTheme(Integer themeCode);

    List<ThemeDTO> findThemeByGenresAndSearchOrderBySort(Pageable pageable, String filter, List<String> genres, String content, int memberCode);
    List<ThemeDTO> findThemeByGenresAndSearchOrderBySort(Pageable pageable, String filter, List<String> genres, String content);

    List<ThemeDTO> findThemeByStoreOrderBySort(Pageable pageable, String filter, Integer storeCode, int memberCode);
    List<ThemeDTO> findThemeByStoreOrderBySort(Pageable pageable, String filter, Integer storeCode);

    List<FindThemeByReactionDTO> findThemeByMemberReaction(Pageable pageable, int loginId, String reaction);

    List<ThemeDTO> findThemeByWeek();
    List<ThemeDTO> findThemeByWeek(int memberCode);

    List<GenreDTO> findGenres();

}
