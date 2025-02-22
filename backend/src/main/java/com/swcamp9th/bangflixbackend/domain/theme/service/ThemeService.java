package com.swcamp9th.bangflixbackend.domain.theme.service;

import com.swcamp9th.bangflixbackend.domain.theme.dto.FindThemeByReactionDTO;
import com.swcamp9th.bangflixbackend.domain.theme.dto.ThemeReactionDTO;
import com.swcamp9th.bangflixbackend.domain.theme.dto.GenreDTO;
import com.swcamp9th.bangflixbackend.domain.theme.dto.ThemeDTO;
import java.util.List;

import com.swcamp9th.bangflixbackend.domain.theme.entity.Theme;
import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import org.springframework.data.domain.Pageable;

/**
 * ThemeService 인터페이스는 테마 관련 비즈니스 로직을 정의합니다.
 * <p>
 * 제공 기능:
 * <ul>
 *     <li>테마 반응(좋아요/스크랩) 등록 및 취소</li>
 *     <li>회원별 스크랩 테마 조회</li>
 *     <li>테마 추천</li>
 *     <li>테마 상세 조회 (회원용, 게스트용)</li>
 *     <li>장르 및 검색 조건에 따른 테마 조회</li>
 *     <li>업체별 테마 조회</li>
 *     <li>회원별 테마 반응 조회</li>
 *     <li>최근 1주일 간 베스트 테마 조회 (회원용, 게스트용)</li>
 *     <li>전체 장르 조회</li>
 * </ul>
 */
public interface ThemeService {

    /**
     * 로그인한 회원이 특정 테마에 대해 좋아요 또는 스크랩 반응을 등록합니다.
     *
     * @param member           반응을 등록하는 회원 엔티티
     * @param themeReactionDTO 테마 반응 정보를 담은 DTO. reaction 값은 "like" 또는 "scrap"이어야 합니다.
     */
    void createThemeReaction(Member member, ThemeReactionDTO themeReactionDTO);

    /**
     * 로그인한 회원이 등록한 특정 테마의 좋아요 또는 스크랩 반응을 취소합니다.
     *
     * @param memberCode       반응을 취소하는 회원의 고유 코드
     * @param themeReactionDTO 테마 반응 정보를 담은 DTO. reaction 값은 "like" 또는 "scrap"이어야 합니다.
     */
    void deleteThemeReaction(int memberCode, ThemeReactionDTO themeReactionDTO);

    /**
     * 회원이 스크랩한 테마 목록을 조회합니다.
     *
     * @param memberCode 조회할 회원의 고유 코드
     * @return 해당 회원이 스크랩한 테마 정보를 담은 ThemeDTO 리스트
     */
    List<ThemeDTO> getScrapedThemeByMemberCode(int memberCode);

    /**
     * 선택적 테마 코드 리스트를 기준으로 추천 테마를 조회합니다.
     * <p>
     * 테마 코드가 제공되지 않으면 기본 추천 로직에 따라 결과를 반환합니다.
     *
     * @param themeCodes (선택적) 추천 기준이 되는 테마 코드 리스트. null 또는 빈 리스트일 경우 기본 추천 로직 적용.
     * @return 추천 테마 정보를 담은 ThemeDTO 리스트
     */
    List<ThemeDTO> recommendTheme(List<Integer> themeCodes);

    /**
     * 회원용 테마 상세 조회 API.
     * <p>
     * 로그인한 회원의 개인화 정보가 반영된 테마 상세 정보를 조회합니다.
     *
     * @param themeCode  조회할 테마의 고유 코드
     * @param memberCode 로그인한 회원의 고유 코드
     * @return 해당 테마의 상세 정보를 담은 ThemeDTO
     */
    ThemeDTO findTheme(Integer themeCode, int memberCode);

    /**
     * 게스트용 테마 상세 조회 API.
     *
     * @param themeCode 조회할 테마의 고유 코드
     * @return 해당 테마의 상세 정보를 담은 ThemeDTO
     */
    ThemeDTO findThemeDTOByThemeCode(Integer themeCode);

    /**
     * 게스트용 테마 상세 조회 API.
     *
     * @param themeCode 조회할 테마의 고유 코드
     * @return 해당 테마의 상세 정보를 담은 ThemeDTO
     */
    Theme findThemeByThemeCode(Integer themeCode);

    /**
     * 회원용 테마 조회 API (필터링 및 검색).
     * <p>
     * 페이징, 정렬 필터, 선택적 장르, 및 검색어를 기반으로 회원에게 개인화된 테마 목록을 조회합니다.
     *
     * @param pageable   페이징 정보
     * @param sort     정렬 기준 (예: "like", "scrap", "review"). 값이 없으면 최신 순으로 정렬됩니다.
     * @param genres     선택적 장르 리스트 (여러 개 가능)
     * @param search    검색어 (테마 이름에 포함된 문자열)
     * @param memberCode 로그인한 회원의 고유 코드
     * @return 조회된 테마 목록을 담은 ThemeDTO 리스트
     */
    List<ThemeDTO> findThemeByGenresAndSearchOrderBySort(Pageable pageable, String sort, List<String> genres, String search, int memberCode);

    /**
     * 게스트용 테마 조회 API (필터링 및 검색).
     * <p>
     * 페이징, 정렬 필터, 선택적 장르, 및 검색어를 기반으로 테마 목록을 조회합니다.
     *
     * @param pageable 페이징 정보
     * @param sort   정렬 (예: "like", "scrap", "review"). 값이 없으면 최신 순으로 정렬됩니다.
     * @param genres   선택적 장르 리스트 (여러 개 가능)
     * @param search  검색어 (테마 이름에 포함된 문자열)
     * @return 조회된 테마 목록을 담은 ThemeDTO 리스트
     */
    List<ThemeDTO> findThemeByGenresAndSearchOrderBySort(Pageable pageable, String sort, List<String> genres, String search);

    /**
     * 회원용 업체별 테마 조회 API.
     * <p>
     * 업체 고유 코드(storeCode)를 기준으로, 회원의 개인화 정보가 반영된 업체 테마 목록을 조회합니다.
     *
     * @param pageable   페이징 정보
     * @param sort     정렬 (예: "like", "scrap", "review"). 값이 없으면 기본적으로 최신순 정렬됩니다.
     * @param storeCode  업체 고유 코드
     * @param memberCode 로그인한 회원의 고유 코드
     * @return 해당 업체의 테마 정보를 담은 ThemeDTO 리스트
     */
    List<ThemeDTO> findThemeDTOListByStoreCode(Pageable pageable, String sort, int storeCode, int memberCode);

    /**
     * 게스트용 업체별 테마 조회 API.
     *
     * @param pageable  페이징 정보
     * @param sort    정렬 (예: "like", "scrap", "review"). 값이 없으면 기본적으로 최신순 정렬됩니다.
     * @param storeCode 업체 고유 코드
     * @return 해당 업체의 테마 정보를 담은 ThemeDTO 리스트
     */
    List<ThemeDTO> findThemeDTOListByStoreCode(Pageable pageable, String sort, int storeCode);

    /**
     * 회원용 테마 반응 조회 API.
     * <p>
     * 로그인한 회원이 좋아요 또는 스크랩한 테마 목록을 조회합니다.
     *
     * @param pageable   페이징 정보
     * @param loginId    로그인한 회원의 고유 코드 (여기서는 int 형태로 처리됨)
     * @param reaction   조회할 반응 종류 ("like" 또는 "scrap")
     * @return 회원의 테마 반응 정보를 담은 FindThemeByReactionDTO 리스트
     */
    List<FindThemeByReactionDTO> findThemeByMemberReaction(Pageable pageable, int loginId, String reaction);

    /**
     * 게스트용 주간 베스트 테마 조회 API.
     * <p>
     * 최근 1주일 간 좋아요 수 기준 상위 테마를 조회합니다.
     *
     * @return 조회된 테마 목록(상위 베스트 테마)을 담은 ThemeDTO 리스트
     */
    List<ThemeDTO> findThemeByWeek();

    /**
     * 회원용 주간 베스트 테마 조회 API.
     * <p>
     * 로그인한 회원의 개인화 정보가 반영된, 최근 1주일 간 좋아요 수 기준 상위 테마를 조회합니다.
     *
     * @param memberCode 로그인한 회원의 고유 코드
     * @return 조회된 테마 목록(상위 베스트 테마)을 담은 ThemeDTO 리스트
     */
    List<ThemeDTO> findThemeByWeek(int memberCode);

    /**
     * 전체 장르 조회 API.
     * <p>
     * 데이터베이스에 저장된 모든 장르 정보를 조회합니다.
     *
     * @return 모든 장르 정보를 담은 GenreDTO 리스트
     */
    List<GenreDTO> findGenres();

}
