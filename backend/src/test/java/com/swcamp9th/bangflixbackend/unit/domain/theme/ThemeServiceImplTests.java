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
import com.swcamp9th.bangflixbackend.domain.theme.repository.GenreRepository;
import com.swcamp9th.bangflixbackend.domain.theme.repository.ThemeReactionRepository;
import com.swcamp9th.bangflixbackend.domain.theme.repository.ThemeRepository;
import com.swcamp9th.bangflixbackend.domain.theme.service.ThemeServiceImpl;
import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import com.swcamp9th.bangflixbackend.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThemeServiceImplTests {

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private ThemeRepository themeRepository;

    @Mock
    private ThemeReactionRepository themeReactionRepository;

    // 스파이(spy)를 사용하면 내부 메소드 호출(예: findThemeByGenresAndSearchOrderBySort)도 조작할 수 있다.
    private ThemeServiceImpl themeService;

    @BeforeEach
    void setUp() {
        themeService = spy(new ThemeServiceImpl(
                modelMapper,
                userRepository,
                storeRepository,
                genreRepository,
                themeRepository,
                themeReactionRepository));
    }

    @Test
    void findTheme_ShouldReturnThemeDTO_WithMemberFound() {
        // Arrange
        int themeCode = 1;
        String loginId = "user1";
        Theme theme = new Theme();
        theme.setThemeCode(themeCode);
        Store store = new Store();
        store.setStoreCode(100);
        store.setName("Test Store");
        theme.setStore(store);

        Member member = new Member();
        member.setMemberCode(200);

        ThemeDTO themeDTO = new ThemeDTO();
        themeDTO.setCreatedAt(String.valueOf(LocalDateTime.now().minusDays(1)));

        when(themeRepository.findById(themeCode)).thenReturn(Optional.of(theme));
        when(userRepository.findById(loginId)).thenReturn(Optional.of(member));
        when(modelMapper.map(theme, ThemeDTO.class)).thenReturn(themeDTO);
        when(themeRepository.countLikesByThemeCode(themeCode)).thenReturn(10);
        when(themeRepository.countScrapsByThemeCode(themeCode)).thenReturn(5);
        when(themeRepository.countReviewsByThemeCode(themeCode)).thenReturn(3);
        when(themeReactionRepository.findByIds(themeCode, member.getMemberCode())).thenReturn(Optional.empty());

        // Act
        ThemeDTO result = themeService.findTheme(themeCode, loginId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStoreCode()).isEqualTo(store.getStoreCode());
        assertThat(result.getStoreName()).isEqualTo(store.getName());
        assertThat(result.getLikeCount()).isEqualTo(10);
        assertThat(result.getScrapCount()).isEqualTo(5);
        assertThat(result.getReviewCount()).isEqualTo(3);
        assertThat(result.getIsLike()).isFalse();
        assertThat(result.getIsScrap()).isFalse();
    }

    @Test
    void findTheme_ShouldReturnThemeDTO_WithMemberNotFound() {
        // Arrange
        int themeCode = 1;
        String loginId = "nonexistent";
        Theme theme = new Theme();
        theme.setThemeCode(themeCode);
        Store store = new Store();
        store.setStoreCode(100);
        store.setName("Test Store");
        theme.setStore(store);

        ThemeDTO themeDTO = new ThemeDTO();
        themeDTO.setCreatedAt(String.valueOf(LocalDateTime.now()));

        when(themeRepository.findById(themeCode)).thenReturn(Optional.of(theme));
        when(userRepository.findById(loginId)).thenReturn(Optional.empty());
        when(modelMapper.map(theme, ThemeDTO.class)).thenReturn(themeDTO);
        when(themeRepository.countLikesByThemeCode(themeCode)).thenReturn(7);
        when(themeRepository.countScrapsByThemeCode(themeCode)).thenReturn(2);
        when(themeRepository.countReviewsByThemeCode(themeCode)).thenReturn(4);

        // Act
        ThemeDTO result = themeService.findTheme(themeCode, loginId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStoreCode()).isEqualTo(store.getStoreCode());
        assertThat(result.getStoreName()).isEqualTo(store.getName());
        assertThat(result.getLikeCount()).isEqualTo(7);
        assertThat(result.getScrapCount()).isEqualTo(2);
        assertThat(result.getReviewCount()).isEqualTo(4);
        assertThat(result.getIsLike()).isFalse();
        assertThat(result.getIsScrap()).isFalse();
    }

    @Test
    void findGenres_ShouldReturnListOfGenreDTOs() {
        // Arrange
        Genre genre1 = new Genre();
        genre1.setName("Action");
        Genre genre2 = new Genre();
        genre2.setName("Drama");
        List<Genre> genres = List.of(genre1, genre2);

        GenreDTO genreDTO1 = new GenreDTO();
        genreDTO1.setName("Action");
        GenreDTO genreDTO2 = new GenreDTO();
        genreDTO2.setName("Drama");

        when(genreRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))).thenReturn(genres);
        when(modelMapper.map(genre1, GenreDTO.class)).thenReturn(genreDTO1);
        when(modelMapper.map(genre2, GenreDTO.class)).thenReturn(genreDTO2);

        // Act
        List<GenreDTO> result = themeService.findGenres();

        // Assert
        assertThat(result)
                .hasSize(2)
                .containsExactly(genreDTO1, genreDTO2);
    }

    @Test
    void findThemeByGenresAndSearchOrderBySort_ShouldReturnPaginatedThemes() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 2);
        String filter = "like";
        List<String> genresFilter = Arrays.asList("Horror", "Thriller");
        String content = "searchText";
        String loginId = "user1";

        Member member = new Member();
        member.setMemberCode(300);
        when(userRepository.findById(loginId)).thenReturn(Optional.of(member));

        Theme theme1 = new Theme();
        theme1.setThemeCode(1);
        Theme theme2 = new Theme();
        theme2.setThemeCode(2);
        // 각 테마에 Store 설정
        Store store = new Store();
        store.setStoreCode(101);
        store.setName("Store1");
        theme1.setStore(store);
        theme2.setStore(store);

        List<Theme> themes = List.of(theme1, theme2);
        when(themeRepository.findThemesByAllGenresAndSearch(genresFilter, content)).thenReturn(themes);

        ThemeDTO dto1 = new ThemeDTO();
        dto1.setCreatedAt(String.valueOf(LocalDateTime.now().minusDays(1)));
        dto1.setLikeCount(15);
        ThemeDTO dto2 = new ThemeDTO();
        dto2.setCreatedAt(String.valueOf(LocalDateTime.now().minusDays(2)));
        dto2.setLikeCount(10);
        // 매핑 결과 지정
        when(modelMapper.map(theme1, ThemeDTO.class)).thenReturn(dto1);
        when(modelMapper.map(theme2, ThemeDTO.class)).thenReturn(dto2);
        when(themeRepository.countLikesByThemeCode(1)).thenReturn(15);
        when(themeRepository.countScrapsByThemeCode(1)).thenReturn(5);
        when(themeRepository.countReviewsByThemeCode(1)).thenReturn(3);
        when(themeRepository.countLikesByThemeCode(2)).thenReturn(10);
        when(themeRepository.countScrapsByThemeCode(2)).thenReturn(2);
        when(themeRepository.countReviewsByThemeCode(2)).thenReturn(1);
        when(themeReactionRepository.findByIds(anyInt(), eq(member.getMemberCode()))).thenReturn(Optional.empty());

        // Act
        List<ThemeDTO> result = themeService.findThemeByGenresAndSearchOrderBySort(
                pageable, filter, genresFilter, content, loginId);

        // Assert
        assertThat(result).hasSize(2);
        // filter "like"에 의해 likeCount 내림차순 정렬되어 dto1가 dto2보다 먼저 위치해야 함.
        assertThat(result.get(0).getLikeCount()).isGreaterThanOrEqualTo(result.get(1).getLikeCount());
    }

    @Test
    void findThemeByStoreOrderBySort_ShouldReturnPaginatedThemes() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 2);
        String filter = "review";
        Integer storeCode = 500;
        String loginId = "user2";

        Member member = new Member();
        member.setMemberCode(400);
        when(userRepository.findById(loginId)).thenReturn(Optional.of(member));

        Theme theme1 = new Theme();
        theme1.setThemeCode(10);
        Theme theme2 = new Theme();
        theme2.setThemeCode(20);
        Store store = new Store();
        store.setStoreCode(storeCode);
        store.setName("StoreTest");
        theme1.setStore(store);
        theme2.setStore(store);

        List<Theme> themes = List.of(theme1, theme2);
        when(themeRepository.findByStoreCode(storeCode)).thenReturn(themes);

        ThemeDTO dto1 = new ThemeDTO();
        dto1.setCreatedAt(String.valueOf(LocalDateTime.now().minusHours(1)));
        dto1.setReviewCount(8);
        ThemeDTO dto2 = new ThemeDTO();
        dto2.setCreatedAt(String.valueOf(LocalDateTime.now().minusHours(2)));
        dto2.setReviewCount(12);

        when(modelMapper.map(theme1, ThemeDTO.class)).thenReturn(dto1);
        when(modelMapper.map(theme2, ThemeDTO.class)).thenReturn(dto2);
        when(themeRepository.countLikesByThemeCode(10)).thenReturn(20);
        when(themeRepository.countScrapsByThemeCode(10)).thenReturn(5);
        when(themeRepository.countReviewsByThemeCode(10)).thenReturn(8);
        when(themeRepository.countLikesByThemeCode(20)).thenReturn(15);
        when(themeRepository.countScrapsByThemeCode(20)).thenReturn(3);
        when(themeRepository.countReviewsByThemeCode(20)).thenReturn(12);
        when(themeReactionRepository.findByIds(anyInt(), eq(member.getMemberCode()))).thenReturn(Optional.empty());

        // Act
        List<ThemeDTO> result = themeService.findThemeByStoreOrderBySort(
                pageable, filter, storeCode, loginId);

        // Assert
        assertThat(result).hasSize(2);
        // filter "review"에 의해 reviewCount 내림차순 정렬되어 dto2가 먼저 나와야 함.
        assertThat(result.get(0).getReviewCount()).isGreaterThanOrEqualTo(result.get(1).getReviewCount());
    }

    @Test
    void createThemeReaction_ShouldCreateNewReaction_WhenNoneExists() {
        // Arrange
        String userId = "user1";
        ThemeReactionDTO reactionDTO = new ThemeReactionDTO();
        reactionDTO.setThemeCode(100);
        reactionDTO.setReaction("like");

        Member member = new Member();
        member.setMemberCode(1);
        when(userRepository.findById(userId)).thenReturn(Optional.of(member));

        Theme theme = new Theme();
        theme.setThemeCode(100);
        when(themeRepository.findById(100)).thenReturn(Optional.of(theme));

        when(themeReactionRepository.findByIds(100, member.getMemberCode())).thenReturn(Optional.empty());

        // Act
        themeService.createThemeReaction(userId, reactionDTO);

        // Assert: 새 ThemeReaction이 ReactionType.LIKE로 생성되어 저장되었는지 검증
        ArgumentCaptor<ThemeReaction> captor = ArgumentCaptor.forClass(ThemeReaction.class);
        verify(themeReactionRepository).save(captor.capture());
        ThemeReaction savedReaction = captor.getValue();
        assertThat(savedReaction.getMember()).isEqualTo(member);
        assertThat(savedReaction.getTheme()).isEqualTo(theme);
        assertThat(savedReaction.getReaction()).isEqualTo(ReactionType.LIKE);
    }

    @Test
    void createThemeReaction_ShouldUpdateReaction_WhenExistingReactionFound() {
        // Arrange – "like" 업데이트의 경우 (기존 반응이 SCRAP이면 SCRAPLIKE로 전환)
        String userId = "user1";
        ThemeReactionDTO reactionDTO = new ThemeReactionDTO();
        reactionDTO.setThemeCode(200);
        reactionDTO.setReaction("like");

        Member member = new Member();
        member.setMemberCode(2);
        when(userRepository.findById(userId)).thenReturn(Optional.of(member));

        Theme theme = new Theme();
        theme.setThemeCode(200);
        when(themeRepository.findById(200)).thenReturn(Optional.of(theme));

        ThemeReaction existingReaction = new ThemeReaction();
        existingReaction.setReaction(ReactionType.SCRAP);
        when(themeReactionRepository.findByIds(200, member.getMemberCode())).thenReturn(Optional.of(existingReaction));

        // Act
        themeService.createThemeReaction(userId, reactionDTO);

        // Assert: 기존 반응이 SCRAPLIKE로 업데이트되어 저장되어야 함.
        assertThat(existingReaction.getReaction()).isEqualTo(ReactionType.SCRAPLIKE);
        verify(themeReactionRepository).save(existingReaction);
    }

    @Test
    void createThemeReaction_ShouldNotUpdate_WhenExistingReactionAlreadySame() {
        // Arrange – 기존 반응이 이미 LIKE인 경우 (새로운 "like" 요청이면 아무 동작 없음)
        String userId = "user1";
        ThemeReactionDTO reactionDTO = new ThemeReactionDTO();
        reactionDTO.setThemeCode(300);
        reactionDTO.setReaction("like");

        Member member = new Member();
        member.setMemberCode(3);
        when(userRepository.findById(userId)).thenReturn(Optional.of(member));

        Theme theme = new Theme();
        theme.setThemeCode(300);
        when(themeRepository.findById(300)).thenReturn(Optional.of(theme));

        ThemeReaction existingReaction = new ThemeReaction();
        existingReaction.setReaction(ReactionType.LIKE);
        when(themeReactionRepository.findByIds(300, member.getMemberCode())).thenReturn(Optional.of(existingReaction));

        // Act
        themeService.createThemeReaction(userId, reactionDTO);

        // Assert: 반응 변경이 없으므로 save()가 호출되지 않아야 함.
        verify(themeReactionRepository, never()).save(any(ThemeReaction.class));
    }

    @Test
    void deleteThemeReaction_ShouldDelete_WhenReactionIsLikeAndExistingReactionIsLike() {
        // Arrange – "like" 삭제 요청이고 기존 반응이 LIKE이면 삭제
        String loginId = "user1";
        ThemeReactionDTO reactionDTO = new ThemeReactionDTO();
        reactionDTO.setThemeCode(400);
        reactionDTO.setReaction("like");

        Member member = new Member();
        member.setMemberCode(10);
        when(userRepository.findById(loginId)).thenReturn(Optional.of(member));

        ThemeReaction existingReaction = new ThemeReaction();
        existingReaction.setReaction(ReactionType.LIKE);
        when(themeReactionRepository.findByIds(400, member.getMemberCode())).thenReturn(Optional.of(existingReaction));

        // Act
        themeService.deleteThemeReaction(loginId, reactionDTO);

        // Assert: delete()가 호출되어야 함.
        verify(themeReactionRepository).delete(existingReaction);
    }

    @Test
    void deleteThemeReaction_ShouldUpdate_WhenReactionIsLikeAndExistingReactionIsScrapLike() {
        // Arrange – "like" 삭제 요청이고 기존 반응이 SCRAPLIKE이면 reaction을 SCRAP로 업데이트 후 저장
        String loginId = "user1";
        ThemeReactionDTO reactionDTO = new ThemeReactionDTO();
        reactionDTO.setThemeCode(500);
        reactionDTO.setReaction("like");

        Member member = new Member();
        member.setMemberCode(11);
        when(userRepository.findById(loginId)).thenReturn(Optional.of(member));

        ThemeReaction existingReaction = new ThemeReaction();
        existingReaction.setReaction(ReactionType.SCRAPLIKE);
        when(themeReactionRepository.findByIds(500, member.getMemberCode())).thenReturn(Optional.of(existingReaction));

        // Act
        themeService.deleteThemeReaction(loginId, reactionDTO);

        // Assert: 반응이 SCRAP로 업데이트되어 저장되어야 함.
        assertThat(existingReaction.getReaction()).isEqualTo(ReactionType.SCRAP);
        verify(themeReactionRepository).save(existingReaction);
    }

    @Test
    void deleteThemeReaction_ShouldDelete_WhenReactionIsScrapAndExistingReactionIsScrap() {
        // Arrange – "scrap" 삭제 요청이고 기존 반응이 SCRAP이면 삭제
        String loginId = "user1";
        ThemeReactionDTO reactionDTO = new ThemeReactionDTO();
        reactionDTO.setThemeCode(600);
        reactionDTO.setReaction("scrap");

        Member member = new Member();
        member.setMemberCode(12);
        when(userRepository.findById(loginId)).thenReturn(Optional.of(member));

        ThemeReaction existingReaction = new ThemeReaction();
        existingReaction.setReaction(ReactionType.SCRAP);
        when(themeReactionRepository.findByIds(600, member.getMemberCode())).thenReturn(Optional.of(existingReaction));

        // Act
        themeService.deleteThemeReaction(loginId, reactionDTO);

        // Assert: delete() 호출되어야 함.
        verify(themeReactionRepository).delete(existingReaction);
    }

    @Test
    void deleteThemeReaction_ShouldNotUpdate_WhenNoExistingReaction() {
        // Arrange – 반응이 없는 경우, 아무 동작도 하지 않아야 함.
        String loginId = "user1";
        ThemeReactionDTO reactionDTO = new ThemeReactionDTO();
        reactionDTO.setThemeCode(700);
        reactionDTO.setReaction("like");

        Member member = new Member();
        member.setMemberCode(13);
        when(userRepository.findById(loginId)).thenReturn(Optional.of(member));

        when(themeReactionRepository.findByIds(700, member.getMemberCode())).thenReturn(Optional.empty());

        // Act
        themeService.deleteThemeReaction(loginId, reactionDTO);

        // Assert: delete나 save 호출 없이 아무 동작 없음.
        verify(themeReactionRepository, never()).delete(any());
        verify(themeReactionRepository, never()).save(any());
    }

    @Test
    void findThemeByMemberReaction_ShouldReturnResult_ForLikeReaction() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 2);
        String loginId = "user1";
        String reaction = "like";

        Member member = new Member();
        member.setMemberCode(20);
        when(userRepository.findById(loginId)).thenReturn(Optional.of(member));

        Theme theme = new Theme();
        theme.setThemeCode(800);
        Store store = new Store();
        store.setStoreCode(900);
        store.setName("Store900");
        theme.setStore(store);

        ThemeReaction themeReaction = new ThemeReaction();
        themeReaction.setTheme(theme);

        List<ThemeReaction> reactionList = List.of(themeReaction);
        when(themeReactionRepository.findThemeByMemberLike(pageable, member.getMemberCode()))
                .thenReturn(reactionList);

        FindThemeByReactionDTO reactionDTO = new FindThemeByReactionDTO();
        when(modelMapper.map(theme, FindThemeByReactionDTO.class)).thenReturn(reactionDTO);
        when(storeRepository.findByThemeCode(theme.getThemeCode())).thenReturn(store);

        // Act
        List<FindThemeByReactionDTO> result = themeService.findThemeByMemberReaction(pageable, loginId, reaction);

        // Assert
        assertThat(result).hasSize(1);
        FindThemeByReactionDTO dto = result.get(0);
        assertThat(dto.getStoreCode()).isEqualTo(store.getStoreCode());
        assertThat(dto.getStoreName()).isEqualTo(store.getName());
        assertThat(dto.getIsLike()).isTrue();
        assertThat(dto.getIsScrap()).isTrue();
    }

    @Test
    void findThemeByMemberReaction_ShouldThrowException_ForInvalidReaction() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 2);
        String loginId = "user1";
        String reaction = "invalid";

        Member member = new Member();
        member.setMemberCode(30);
        when(userRepository.findById(loginId)).thenReturn(Optional.of(member));

        // Act & Assert
        assertThatThrownBy(() -> themeService.findThemeByMemberReaction(pageable, loginId, reaction))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void findThemeByWeek_ShouldReturnThemes_WithMemberFound() {
        // Arrange
        String loginId = "user1";
        Pageable pageable = PageRequest.of(0, 5);
        LocalDateTime now = LocalDateTime.now();
        Theme theme = new Theme();
        theme.setThemeCode(1000);
        Store store = new Store();
        store.setStoreCode(1100);
        store.setName("Store1100");
        theme.setStore(store);

        List<Theme> themes = List.of(theme);
        when(themeRepository.findByWeekOrderByLikes(any(), eq(pageable))).thenReturn(themes);

        Member member = new Member();
        member.setMemberCode(40);
        when(userRepository.findById(loginId)).thenReturn(Optional.of(member));

        ThemeDTO themeDTO = new ThemeDTO();
        themeDTO.setCreatedAt(String.valueOf(now.minusDays(3)));
        when(modelMapper.map(theme, ThemeDTO.class)).thenReturn(themeDTO);
        when(themeRepository.countLikesByThemeCode(theme.getThemeCode())).thenReturn(5);
        when(themeRepository.countScrapsByThemeCode(theme.getThemeCode())).thenReturn(2);
        when(themeRepository.countReviewsByThemeCode(theme.getThemeCode())).thenReturn(1);
        when(themeReactionRepository.findByIds(theme.getThemeCode(), member.getMemberCode())).thenReturn(Optional.empty());

        // Act
        List<ThemeDTO> result = themeService.findThemeByWeek(loginId);

        // Assert
        assertThat(result).hasSize(1);
        ThemeDTO dto = result.get(0);
        assertThat(dto.getStoreCode()).isEqualTo(store.getStoreCode());
    }

    @Test
    void findThemeByWeek_ShouldReturnThemes_WithMemberNotFound() {
        // Arrange
        String loginId = "nonexistent";
        Pageable pageable = PageRequest.of(0, 5);
        LocalDateTime now = LocalDateTime.now();
        Theme theme = new Theme();
        theme.setThemeCode(1001);
        Store store = new Store();
        store.setStoreCode(1101);
        store.setName("Store1101");
        theme.setStore(store);

        List<Theme> themes = List.of(theme);
        when(themeRepository.findByWeekOrderByLikes(any(), eq(pageable))).thenReturn(themes);
        when(userRepository.findById(loginId)).thenReturn(Optional.empty());

        ThemeDTO themeDTO = new ThemeDTO();
        themeDTO.setCreatedAt(String.valueOf(now.minusDays(2)));
        when(modelMapper.map(theme, ThemeDTO.class)).thenReturn(themeDTO);
        when(themeRepository.countLikesByThemeCode(theme.getThemeCode())).thenReturn(3);
        when(themeRepository.countScrapsByThemeCode(theme.getThemeCode())).thenReturn(1);
        when(themeRepository.countReviewsByThemeCode(theme.getThemeCode())).thenReturn(0);

        // Act
        List<ThemeDTO> result = themeService.findThemeByWeek(loginId);

        // Assert
        assertThat(result).hasSize(1);
        ThemeDTO dto = result.get(0);
        // 회원이 없으므로 isLike, isScrap은 false
        assertThat(dto.getIsLike()).isFalse();
        assertThat(dto.getIsScrap()).isFalse();
    }

    @Test
    void recommendTheme_ShouldDelegateToFindThemeByGenresAndSearchOrderBySort_WhenThemeCodesIsNull() {
        // Arrange
        List<Integer> themeCodes = null;
        // themeCodes가 null이면 내부에서 findThemeByGenresAndSearchOrderBySort를 호출
        List<ThemeDTO> expected = List.of(new ThemeDTO());
        doReturn(expected).when(themeService)
                .findThemeByGenresAndSearchOrderBySort(any(), eq("like"), isNull(), isNull(), isNull());

        // Act
        List<ThemeDTO> result = themeService.recommendTheme(themeCodes);

        // Assert
        assertThat(result).isEqualTo(expected);
        verify(themeService)
                .findThemeByGenresAndSearchOrderBySort(any(), eq("like"), isNull(), isNull(), isNull());
    }

    @Test
    void recommendTheme_ShouldReturnThemes_BasedOnGenresFrequency() {
        // Arrange
        List<Integer> themeCodes = Arrays.asList(1, 2, 3);
        // themeRepository.findGenresByThemeCode: 예를 들어 [10, 10, 20] 반환
        when(themeRepository.findGenresByThemeCode(themeCodes)).thenReturn(Arrays.asList(10, 10, 20));
        // 가장 많이 등장한 번호는 10
        List<Integer> mostFrequent = List.of(10);
        List<String> genreNames = List.of("Horror");
        when(genreRepository.findGenreNames(mostFrequent)).thenReturn(genreNames);

        List<ThemeDTO> expected = List.of(new ThemeDTO());
        doReturn(expected).when(themeService)
                .findThemeByGenresAndSearchOrderBySort(any(), eq("like"), eq(genreNames), isNull(), isNull());

        // Act
        List<ThemeDTO> result = themeService.recommendTheme(themeCodes);

        // Assert
        assertThat(result).isEqualTo(expected);
        verify(themeRepository).findGenresByThemeCode(themeCodes);
        verify(genreRepository).findGenreNames(mostFrequent);
        verify(themeService)
                .findThemeByGenresAndSearchOrderBySort(any(), eq("like"), eq(genreNames), isNull(), isNull());
    }

    @Test
    void getScrapedTheme_ShouldReturnThemes_WhenUserExists() {
        // Arrange
        String loginId = "user1";
        Member member = new Member();
        member.setMemberCode(50);
        when(userRepository.findById(loginId)).thenReturn(Optional.of(member));

        ThemeReaction reaction1 = new ThemeReaction();
        reaction1.setThemeCode(1000);
        ThemeReaction reaction2 = new ThemeReaction();
        reaction2.setThemeCode(2000);
        List<ThemeReaction> reactions = List.of(reaction1, reaction2);
        when(themeReactionRepository.findThemeByMemberCode(member.getMemberCode()))
                .thenReturn(reactions);

        Theme theme1 = new Theme();
        theme1.setThemeCode(1000);
        Theme theme2 = new Theme();
        theme2.setThemeCode(2000);
        Store store = new Store();
        store.setStoreCode(3000);
        store.setName("Store3000");
        theme1.setStore(store);
        theme2.setStore(store);
        when(themeRepository.findByThemeCodes(Arrays.asList(1000, 2000)))
                .thenReturn(List.of(theme1, theme2));

        ThemeDTO dto1 = new ThemeDTO();
        dto1.setCreatedAt(String.valueOf(LocalDateTime.now()));
        ThemeDTO dto2 = new ThemeDTO();
        dto2.setCreatedAt(String.valueOf(LocalDateTime.now().minusDays(1)));
        when(modelMapper.map(theme1, ThemeDTO.class)).thenReturn(dto1);
        when(modelMapper.map(theme2, ThemeDTO.class)).thenReturn(dto2);
        when(themeRepository.countLikesByThemeCode(1000)).thenReturn(8);
        when(themeRepository.countScrapsByThemeCode(1000)).thenReturn(4);
        when(themeRepository.countReviewsByThemeCode(1000)).thenReturn(2);
        when(themeRepository.countLikesByThemeCode(2000)).thenReturn(6);
        when(themeRepository.countScrapsByThemeCode(2000)).thenReturn(3);
        when(themeRepository.countReviewsByThemeCode(2000)).thenReturn(1);
        when(themeReactionRepository.findByIds(anyInt(), eq(member.getMemberCode()))).thenReturn(Optional.empty());

        // Act
        List<ThemeDTO> result = themeService.getScrapedTheme(loginId);

        // Assert
        assertThat(result).hasSize(2);
        result.forEach(dto -> {
            assertThat(dto.getStoreCode()).isEqualTo(store.getStoreCode());
            assertThat(dto.getStoreName()).isEqualTo(store.getName());
        });
    }

    @Test
    void getScrapedTheme_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        String loginId = "nonexistent";
        when(userRepository.findById(loginId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> themeService.getScrapedTheme(loginId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("존재하지 않는 유저입니다.");
    }
}
