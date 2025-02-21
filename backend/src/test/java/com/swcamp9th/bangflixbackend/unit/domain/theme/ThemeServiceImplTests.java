package com.swcamp9th.bangflixbackend.unit.domain.theme;


import com.swcamp9th.bangflixbackend.domain.store.entity.Store;
import com.swcamp9th.bangflixbackend.domain.store.repository.StoreRepository;
import com.swcamp9th.bangflixbackend.domain.theme.dto.FindThemeByReactionDTO;
import com.swcamp9th.bangflixbackend.domain.theme.dto.GenreDTO;
import com.swcamp9th.bangflixbackend.domain.theme.dto.ThemeDTO;
import com.swcamp9th.bangflixbackend.domain.theme.dto.ThemeReactionDTO;
import com.swcamp9th.bangflixbackend.domain.theme.entity.Genre;
import com.swcamp9th.bangflixbackend.domain.theme.entity.ReactionType;
import com.swcamp9th.bangflixbackend.domain.theme.entity.Theme;
import com.swcamp9th.bangflixbackend.domain.theme.entity.ThemeReaction;
import com.swcamp9th.bangflixbackend.domain.theme.exception.ThemeNotFoundException;
import com.swcamp9th.bangflixbackend.domain.theme.exception.UnexpectedReactionTypeException;
import com.swcamp9th.bangflixbackend.domain.theme.repository.GenreRepository;
import com.swcamp9th.bangflixbackend.domain.theme.repository.ThemeReactionRepository;
import com.swcamp9th.bangflixbackend.domain.theme.repository.ThemeRepository;
import com.swcamp9th.bangflixbackend.domain.theme.service.ThemeServiceImpl;
import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThemeServiceImplTests {

    @Mock
    private ModelMapper modelMapper;
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private GenreRepository genreRepository;
    @Mock
    private ThemeRepository themeRepository;
    @Mock
    private ThemeReactionRepository themeReactionRepository;

    @InjectMocks
    private ThemeServiceImpl themeService;

    private Theme theme;
    private ThemeDTO themeDTO;
    private Store store;
    private ThemeReaction themeReaction;
    private ThemeReactionDTO themeReactionDTO;

    @BeforeEach
    void setUp() {
        // 샘플 Store
        store = new Store();
        store.setStoreCode(100);
        store.setName("Test Store");

        // 샘플 Theme
        theme = new Theme();
        theme.setThemeCode(1);
        theme.setStore(store);

        // 샘플 ThemeDTO (ModelMapper 매핑 시 사용)
        themeDTO = new ThemeDTO();
        themeDTO.setStoreCode(store.getStoreCode());
        themeDTO.setStoreName(store.getName());
        themeDTO.setLikeCount(0);
        themeDTO.setScrapCount(0);
        themeDTO.setReviewCount(0);
        themeDTO.setIsLike(false);
        themeDTO.setIsScrap(false);

        // 샘플 ThemeReaction 및 DTO
        themeReaction = new ThemeReaction();
        themeReaction.setTheme(theme);
        themeReaction.setReaction(ReactionType.LIKE);

        themeReactionDTO = new ThemeReactionDTO();
        themeReactionDTO.setThemeCode(1);
        themeReactionDTO.setReaction("like");
    }

    @DisplayName("테마 조회 - 유효한 테마 코드")
    @Test
    void testFindTheme_validThemeCode() {

        // given
        when(themeRepository.findById(1)).thenReturn(Optional.of(theme));
        when(modelMapper.map(theme, ThemeDTO.class)).thenReturn(themeDTO);
        when(themeRepository.countLikesByThemeCode(1)).thenReturn(10);
        when(themeRepository.countScrapsByThemeCode(1)).thenReturn(5);
        when(themeRepository.countReviewsByThemeCode(1)).thenReturn(3);

        // when
        ThemeDTO result = themeService.findThemeDTOByThemeCode(1);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getStoreCode()).isEqualTo(100);
        assertThat(result.getStoreName()).isEqualTo("Test Store");
        assertThat(result.getLikeCount()).isEqualTo(10);
        assertThat(result.getScrapCount()).isEqualTo(5);
        assertThat(result.getReviewCount()).isEqualTo(3);
    }

    @DisplayName("테마 조회 - 존재하지 않는 테마 코드")
    @Test
    void testFindTheme_invalidThemeCodeThrowsException() {

        // given
        when(themeRepository.findById(1)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> themeService.findThemeDTOByThemeCode(1))
                .isInstanceOf(ThemeNotFoundException.class)
                .hasMessage("존재하지 않는 테마입니다.");
    }

    @DisplayName("테마 조회 - 회원 코드 포함 (반응 없음)")
    @Test
    void testFindTheme_withMemberCode_noReaction() {

        // given
        int memberCode = 999;
        when(themeRepository.findById(1)).thenReturn(Optional.of(theme));
        when(modelMapper.map(theme, ThemeDTO.class)).thenReturn(themeDTO);
        when(themeRepository.countLikesByThemeCode(1)).thenReturn(7);
        when(themeRepository.countScrapsByThemeCode(1)).thenReturn(2);
        when(themeRepository.countReviewsByThemeCode(1)).thenReturn(4);

        // reaction 조회 시 빈 Optional => isLike, isScrap remain false
        when(themeReactionRepository.findReactionByThemeCodeAndMemberCode(1, memberCode))
                .thenReturn(Optional.empty());

        // when
        ThemeDTO result = themeService.findTheme(1, memberCode);

        // then
        assertThat(result.getLikeCount()).isEqualTo(7);
        assertThat(result.getScrapCount()).isEqualTo(2);
        assertThat(result.getReviewCount()).isEqualTo(4);
        assertThat(result.getIsLike()).isFalse();
        assertThat(result.getIsScrap()).isFalse();
    }

    @DisplayName("테마 조회 - 회원 코드 포함 (반응 있음)")
    @Test
    void testFindTheme_withMemberCode_withReaction() {

        // given
        int memberCode = 999;

        // reaction가 존재하는 경우
        themeReaction.setReaction(ReactionType.SCRAPLIKE);
        when(themeRepository.findById(1)).thenReturn(Optional.of(theme));
        when(modelMapper.map(theme, ThemeDTO.class)).thenReturn(themeDTO);
        when(themeRepository.countLikesByThemeCode(1)).thenReturn(7);
        when(themeRepository.countScrapsByThemeCode(1)).thenReturn(2);
        when(themeRepository.countReviewsByThemeCode(1)).thenReturn(4);
        when(themeReactionRepository.findReactionByThemeCodeAndMemberCode(1, memberCode))
                .thenReturn(Optional.of(themeReaction));

        // when
        ThemeDTO result = themeService.findTheme(1, memberCode);

        // then
        // SCRAPLIKE => isLike, isScrap true
        assertThat(result.getIsLike()).isTrue();
        assertThat(result.getIsScrap()).isTrue();
    }

    @DisplayName("장르 조회")
    @Test
    void testFindGenres() {

        // given
        Genre genre = new Genre();
        genre.setName("Comedy");
        List<Genre> genres = List.of(genre);
        GenreDTO genreDTO = new GenreDTO();
        genreDTO.setName("Comedy");

        when(genreRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))).thenReturn(genres);
        when(modelMapper.map(genre, GenreDTO.class)).thenReturn(genreDTO);

        // when
        List<GenreDTO> result = themeService.findGenres();

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Comedy");
    }

    @DisplayName("테마 조회 - 회원 코드 포함 (반응 없음, 회원 코드 있음)")
    @Test
    void testFindThemeByGenresAndSearchOrderBySort_withMemberCode() {

        // given
        Pageable pageable = PageRequest.of(0, 10);
        String filter = "like";
        List<String> genres = List.of("Comedy", "Drama");
        String content = "sample";
        int memberCode = 999;

        // 두 개의 테마 준비
        Theme theme2 = new Theme();
        theme2.setThemeCode(2);
        theme2.setStore(store);

        ThemeDTO themeDTO2 = new ThemeDTO();
        themeDTO2.setStoreCode(store.getStoreCode());
        themeDTO2.setStoreName(store.getName());
        themeDTO2.setLikeCount(20);
        themeDTO2.setScrapCount(3);
        themeDTO2.setReviewCount(5);

        // mocking: 검색 메서드 분기 (genres != null && content != null)
        List<Theme> themeList = List.of(theme, theme2);
        when(themeRepository.findThemesByAllGenresAndSearch(genres, content)).thenReturn(themeList);

        // 각 Theme에 대한 매핑
        when(modelMapper.map(theme, ThemeDTO.class)).thenReturn(themeDTO);
        when(modelMapper.map(theme2, ThemeDTO.class)).thenReturn(themeDTO2);

        // count 값 mocking
        when(themeRepository.countLikesByThemeCode(theme.getThemeCode())).thenReturn(7);
        when(themeRepository.countScrapsByThemeCode(theme.getThemeCode())).thenReturn(2);
        when(themeRepository.countReviewsByThemeCode(theme.getThemeCode())).thenReturn(4);
        when(themeRepository.countLikesByThemeCode(theme2.getThemeCode())).thenReturn(20);
        when(themeRepository.countScrapsByThemeCode(theme2.getThemeCode())).thenReturn(3);
        when(themeRepository.countReviewsByThemeCode(theme2.getThemeCode())).thenReturn(5);

        // reaction 조회 (없다고 가정)
        when(themeReactionRepository.findReactionByThemeCodeAndMemberCode(any(), eq(memberCode)))
                .thenReturn(Optional.empty());

        // when
        List<ThemeDTO> result = themeService.findThemeByGenresAndSearchOrderBySort(
                pageable, filter, genres, content, memberCode);

        // then
        // 정렬: like 기준으로 내림차순 정렬되어야 함.
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getLikeCount()).isGreaterThanOrEqualTo(result.get(1).getLikeCount());
    }

    @DisplayName("테마 조회 - 회원코드 미포함")
    @Test
    void testFindThemeByGenresAndSearchOrderBySort_withoutMemberCode() {

        // given
        Pageable pageable = PageRequest.of(0, 10);
        String filter = "scrap";
        List<String> genres = List.of("Action");
        String content = null; // content null 분기

        // 단일 테마 준비
        List<Theme> themeList = List.of(theme);
        when(themeRepository.findThemesByAllGenres(genres)).thenReturn(themeList);
        when(modelMapper.map(theme, ThemeDTO.class)).thenReturn(themeDTO);
        when(themeRepository.countLikesByThemeCode(theme.getThemeCode())).thenReturn(5);
        when(themeRepository.countScrapsByThemeCode(theme.getThemeCode())).thenReturn(3);
        when(themeRepository.countReviewsByThemeCode(theme.getThemeCode())).thenReturn(2);

        // when
        List<ThemeDTO> result = themeService.findThemeByGenresAndSearchOrderBySort(
                pageable, filter, genres, content);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getScrapCount()).isEqualTo(3);
    }

    @DisplayName("업체별 테마 조회 - 회원 코드 포함")
    @Test
    void testFindThemeByStoreOrderBySort_withMemberCode() {

        // given
        Pageable pageable = PageRequest.of(0, 10);
        String filter = "review";
        int storeCode = 100;
        int memberCode = 999;

        // 두 개의 테마 준비
        Theme theme2 = new Theme();
        theme2.setThemeCode(2);
        theme2.setStore(store);

        ThemeDTO themeDTO2 = new ThemeDTO();
        themeDTO2.setStoreCode(store.getStoreCode());
        themeDTO2.setStoreName(store.getName());
        themeDTO2.setReviewCount(15);

        List<Theme> themeList = List.of(theme, theme2);
        when(themeRepository.findByStoreCode(storeCode)).thenReturn(themeList);
        when(modelMapper.map(theme, ThemeDTO.class)).thenReturn(themeDTO);
        when(modelMapper.map(theme2, ThemeDTO.class)).thenReturn(themeDTO2);

        // count stubbing
        when(themeRepository.countLikesByThemeCode(theme.getThemeCode())).thenReturn(5);
        when(themeRepository.countScrapsByThemeCode(theme.getThemeCode())).thenReturn(2);
        when(themeRepository.countReviewsByThemeCode(theme.getThemeCode())).thenReturn(3);
        when(themeRepository.countLikesByThemeCode(theme2.getThemeCode())).thenReturn(10);
        when(themeRepository.countScrapsByThemeCode(theme2.getThemeCode())).thenReturn(4);
        when(themeRepository.countReviewsByThemeCode(theme2.getThemeCode())).thenReturn(15);

        // reaction 조회 (없다고 가정)
        when(themeReactionRepository.findReactionByThemeCodeAndMemberCode(any(), eq(memberCode)))
                .thenReturn(Optional.empty());

        // when
        List<ThemeDTO> result = themeService.findThemeByStoreOrderBySort(
                pageable, filter, storeCode, memberCode);

        // then
        assertThat(result).hasSize(2);
        // reviewCount 기준 내림차순 정렬 확인
        assertThat(result.get(0).getReviewCount()).isGreaterThanOrEqualTo(result.get(1).getReviewCount());
    }

    @DisplayName("업체별 테마 조회 - 회원 코드 미포함")
    @Test
    void testFindThemeByStoreOrderBySort_withoutMemberCode() {

        // given
        Pageable pageable = PageRequest.of(0, 10);
        String filter = null; // 기본 정렬
        int storeCode = 100;

        List<Theme> themeList = List.of(theme);
        when(themeRepository.findByStoreCode(storeCode)).thenReturn(themeList);
        when(modelMapper.map(theme, ThemeDTO.class)).thenReturn(themeDTO);
        when(themeRepository.countLikesByThemeCode(theme.getThemeCode())).thenReturn(5);
        when(themeRepository.countScrapsByThemeCode(theme.getThemeCode())).thenReturn(2);
        when(themeRepository.countReviewsByThemeCode(theme.getThemeCode())).thenReturn(3);

        // when
        List<ThemeDTO> result = themeService.findThemeByStoreOrderBySort(
                pageable, filter, storeCode);

        // then
        assertThat(result).hasSize(1);
    }

    @DisplayName("테마 반응 생성 - 반응 추가")
    @Test
    void testCreateThemeReaction_newReaction() {

        // given : reaction 미존재 시 새로 저장
        Member dummyMember = new Member();
        dummyMember.setMemberCode(999);
        when(themeRepository.findById(themeReactionDTO.getThemeCode())).thenReturn(Optional.of(theme));
        when(themeReactionRepository.findReactionByThemeCodeAndMemberCode(themeReactionDTO.getThemeCode(), dummyMember.getMemberCode()))
                .thenReturn(Optional.empty());

        // when
        themeService.createThemeReaction(dummyMember, themeReactionDTO);

        // then : 새 ThemeReaction이 저장되어야 함
        verify(themeReactionRepository, times(1)).save(any(ThemeReaction.class));
    }

    @DisplayName("테마 반응 생성 - 반응 업데이트")
    @Test
    void testCreateThemeReaction_existingReactionUpdate() {

        // given : reaction가 존재하는 경우 (예: 기존 reaction이 SCRAP이고 요청이 like인 경우 -> SCRAPLIKE로 업데이트)
        Member dummyMember = new Member();
        dummyMember.setMemberCode(999);
        themeReaction = new ThemeReaction();
        themeReaction.setReaction(ReactionType.SCRAP);
        when(themeRepository.findById(themeReactionDTO.getThemeCode())).thenReturn(Optional.of(theme));
        when(themeReactionRepository.findReactionByThemeCodeAndMemberCode(themeReactionDTO.getThemeCode(), dummyMember.getMemberCode()))
                .thenReturn(Optional.of(themeReaction));

        // when
        themeService.createThemeReaction(dummyMember, themeReactionDTO);

        // then : save 호출되어 reaction이 SCRAPLIKE로 업데이트되었어야 함
        ArgumentCaptor<ThemeReaction> captor = ArgumentCaptor.forClass(ThemeReaction.class);
        verify(themeReactionRepository).save(captor.capture());
        ThemeReaction savedReaction = captor.getValue();
        assertThat(savedReaction.getReaction()).isEqualTo(ReactionType.SCRAPLIKE);
    }

    @DisplayName("테마 반응 생성 - 반응 업데이트 (좋아요 -> 좋아요 취소)")
    @Test
    void testDeleteThemeReaction_deleteLike() {

        // given : 요청 reaction이 "like"이고 현재 reaction이 LIKE이면 삭제
        int memberCode = 999;
        themeReaction = new ThemeReaction();
        themeReaction.setReaction(ReactionType.LIKE);
        when(themeReactionRepository.findReactionByThemeCodeAndMemberCode(1, memberCode))
                .thenReturn(Optional.of(themeReaction));

        // when
        themeService.deleteThemeReaction(memberCode, themeReactionDTO);

        // then : delete 호출 확인
        verify(themeReactionRepository, times(1)).delete(themeReaction);
    }

    @DisplayName("테마 반응 취소 - 반응 업데이트 (스크랩 -> 좋아요)")
    @Test
    void testDeleteThemeReaction_updateScrapLike_toScrap() {

        // given : 요청 reaction "like"이고 현재 reaction이 SCRAPLIKE이면 좋아요 취소 -> SCRAP로 변경
        int memberCode = 999;
        themeReaction = new ThemeReaction();
        themeReaction.setReaction(ReactionType.SCRAPLIKE);
        when(themeReactionRepository.findReactionByThemeCodeAndMemberCode(1, memberCode))
                .thenReturn(Optional.of(themeReaction));

        // when
        themeService.deleteThemeReaction(memberCode, themeReactionDTO);

        // then : save 호출되어 reaction이 SCRAP으로 변경되어야 함
        ArgumentCaptor<ThemeReaction> captor = ArgumentCaptor.forClass(ThemeReaction.class);
        verify(themeReactionRepository).save(captor.capture());
        ThemeReaction updated = captor.getValue();
        assertThat(updated.getReaction()).isEqualTo(ReactionType.SCRAP);
    }

    @DisplayName("잘못된 요청 - 반응 업데이트")
    @Test
    void testDeleteThemeReaction_invalidReactionThrowsException() {

        // given : 요청 reaction이 올바르지 않은 경우
        int memberCode = 999;
        ThemeReactionDTO invalidDTO = new ThemeReactionDTO();
        invalidDTO.setThemeCode(1);
        invalidDTO.setReaction("invalid");
        themeReaction = new ThemeReaction();
        themeReaction.setReaction(ReactionType.LIKE);
        when(themeReactionRepository.findReactionByThemeCodeAndMemberCode(1, memberCode))
                .thenReturn(Optional.of(themeReaction));

        // when & then
        assertThatThrownBy(() -> themeService.deleteThemeReaction(memberCode, invalidDTO))
                .isInstanceOf(UnexpectedReactionTypeException.class);
    }

    @DisplayName("사용자 반응으로 테마 조회 - 좋아요")
    @Test
    void testFindThemeByMemberReaction_like() {

        // given
        Pageable pageable = PageRequest.of(0, 10);
        int memberCode = 999;

        // reaction "like" branch
        when(themeReactionRepository.findThemeByMemberLike(pageable, memberCode))
                .thenReturn(List.of(themeReaction));

        // store lookup
        when(storeRepository.findByThemeCode(theme.getThemeCode())).thenReturn(store);

        // modelMapper mapping from Theme -> FindThemeByReactionDTO
        FindThemeByReactionDTO reactionDTO = new FindThemeByReactionDTO();
        reactionDTO.setStoreCode(store.getStoreCode());
        reactionDTO.setStoreName(store.getName());
        reactionDTO.setIsLike(true);
        reactionDTO.setIsScrap(true);
        when(modelMapper.map(theme, FindThemeByReactionDTO.class)).thenReturn(reactionDTO);

        // when
        List<FindThemeByReactionDTO> result = themeService.findThemeByMemberReaction(pageable, memberCode, "like");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStoreCode()).isEqualTo(100);
    }

    @DisplayName("사용자 반응으로 테마 조회 실패 - 잘못된 요청")
    @Test
    void testFindThemeByMemberReaction_invalidReaction() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        int memberCode = 999;
        // when & then
        assertThatThrownBy(() -> themeService.findThemeByMemberReaction(pageable, memberCode, "invalid"))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("사용자 코드로 테마 조회 - 최근 1주일")
    @Test
    void testFindThemeByWeek_withMemberCode() {
        // given
        int memberCode = 999;
        Pageable pageable = PageRequest.of(0, 5);
        List<Theme> themeList = List.of(theme);
        when(themeRepository.findByWeekOrderByLikes(any(LocalDateTime.class), eq(pageable)))
                .thenReturn(themeList);
        when(modelMapper.map(theme, ThemeDTO.class)).thenReturn(themeDTO);
        when(themeRepository.countLikesByThemeCode(theme.getThemeCode())).thenReturn(5);
        when(themeRepository.countScrapsByThemeCode(theme.getThemeCode())).thenReturn(2);
        when(themeRepository.countReviewsByThemeCode(theme.getThemeCode())).thenReturn(3);
        when(themeReactionRepository.findReactionByThemeCodeAndMemberCode(theme.getThemeCode(), memberCode))
                .thenReturn(Optional.empty());

        // when
        List<ThemeDTO> result = themeService.findThemeByWeek(memberCode);

        // then
        assertThat(result).hasSize(1);
    }

    @DisplayName("사용자 코드 없이 테마 조회 - 최근 1주일")
    @Test
    void testFindThemeByWeek_withoutMemberCode() {
        // given
        Pageable pageable = PageRequest.of(0, 5);
        List<Theme> themeList = List.of(theme);
        when(themeRepository.findByWeekOrderByLikes(any(LocalDateTime.class), eq(pageable)))
                .thenReturn(themeList);
        when(modelMapper.map(theme, ThemeDTO.class)).thenReturn(themeDTO);
        when(themeRepository.countLikesByThemeCode(theme.getThemeCode())).thenReturn(5);
        when(themeRepository.countScrapsByThemeCode(theme.getThemeCode())).thenReturn(2);
        when(themeRepository.countReviewsByThemeCode(theme.getThemeCode())).thenReturn(3);

        // when
        List<ThemeDTO> result = themeService.findThemeByWeek();

        // then
        assertThat(result).hasSize(1);
    }

    @DisplayName("테마 추천 - 요청 테마 코드 없음")
    @Test
    void testRecommendTheme_withNullThemeCodes() {

        // given
        Pageable pageable = PageRequest.of(0, 5);

        // recommendTheme(themeCodes == null) => 내부적으로 findThemeByGenresAndSearchOrderBySort 호출
        List<Theme> themeList = List.of(theme);
        when(themeRepository.findAll()).thenReturn(themeList);
        when(modelMapper.map(theme, ThemeDTO.class)).thenReturn(themeDTO);
        when(themeRepository.countLikesByThemeCode(theme.getThemeCode())).thenReturn(5);
        when(themeRepository.countScrapsByThemeCode(theme.getThemeCode())).thenReturn(2);
        when(themeRepository.countReviewsByThemeCode(theme.getThemeCode())).thenReturn(3);

        // when
        List<ThemeDTO> result = themeService.recommendTheme(null);

        // then
        assertThat(result).isNotNull();
    }

    @DisplayName("테마 추천 - 요청 테마 코드 있음")
    @Test
    void testRecommendTheme_withThemeCodes() {

        // given
        List<Integer> themeCodes = List.of(1, 2, 3);

        // findGenresByThemeCode: returns genres (as Integer list)
        when(themeRepository.findGenresByThemeCode(themeCodes)).thenReturn(List.of(10, 20, 10));

        // genreRepository.findGenreNames: for most frequent genres (here 10)
        when(genreRepository.findGenreNames(List.of(10))).thenReturn(List.of("Comedy"));

        // 내부 호출: findThemeByGenresAndSearchOrderBySort(pageable, "like", genreNames, null)
        List<Theme> themeList = List.of(theme);
        when(themeRepository.findThemesByAllGenres(anyList())).thenReturn(themeList);
        when(modelMapper.map(theme, ThemeDTO.class)).thenReturn(themeDTO);
        when(themeRepository.countLikesByThemeCode(theme.getThemeCode())).thenReturn(5);
        when(themeRepository.countScrapsByThemeCode(theme.getThemeCode())).thenReturn(2);
        when(themeRepository.countReviewsByThemeCode(theme.getThemeCode())).thenReturn(3);

        // when
        List<ThemeDTO> result = themeService.recommendTheme(themeCodes);

        // then
        assertThat(result).isNotEmpty();
    }

    @DisplayName("회원 코드로 스크랩한 테마 조회")
    @Test
    void testGetScrapedThemeByMemberCode() {
        // given
        int memberCode = 999;
        // themeReactionRepository: SCRAP 또는 SCRAPLIKE 반응을 가진 항목들 반환
        when(themeReactionRepository.findThemeReactionsByMemberCodeAndReactionType(eq(memberCode), any()))
                .thenReturn(List.of(themeReaction));
        // repository.findByThemeCodes: themeCodes를 통해 Theme 목록 반환
        when(themeRepository.findByThemeCodes(anyList())).thenReturn(List.of(theme));
        when(modelMapper.map(theme, ThemeDTO.class)).thenReturn(themeDTO);
        when(themeRepository.countLikesByThemeCode(theme.getThemeCode())).thenReturn(5);
        when(themeRepository.countScrapsByThemeCode(theme.getThemeCode())).thenReturn(2);
        when(themeRepository.countReviewsByThemeCode(theme.getThemeCode())).thenReturn(3);
        when(themeReactionRepository.findReactionByThemeCodeAndMemberCode(theme.getThemeCode(), memberCode))
                .thenReturn(Optional.empty());

        // when
        List<ThemeDTO> result = themeService.getScrapedThemeByMemberCode(memberCode);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStoreCode()).isEqualTo(100);
    }
}
