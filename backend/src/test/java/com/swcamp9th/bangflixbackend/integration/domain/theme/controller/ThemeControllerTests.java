package com.swcamp9th.bangflixbackend.integration.domain.theme.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swcamp9th.bangflixbackend.domain.theme.controller.ThemeController;
import com.swcamp9th.bangflixbackend.domain.theme.dto.FindThemeByReactionDTO;
import com.swcamp9th.bangflixbackend.domain.theme.dto.GenreDTO;
import com.swcamp9th.bangflixbackend.domain.theme.dto.ThemeDTO;
import com.swcamp9th.bangflixbackend.domain.theme.dto.ThemeReactionDTO;
import com.swcamp9th.bangflixbackend.domain.theme.service.ThemeService;
import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import com.swcamp9th.bangflixbackend.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ThemeController.class)
@AutoConfigureMockMvc(addFilters = false)
class ThemeControllerTests {

    // 컨트롤러에서 사용되는 RequestAttribute key (실제 값은 상수에서 관리)
    private static final String SERVLET_REQUEST_ATTRIBUTE_KEY = "loginId";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ThemeService themeService;

    @MockBean
    private UserService userService;

    // 1. 특정 테마 조회 API - 게스트
    @Test
    @DisplayName("GET /api/v1/themes/{themeCode} - 게스트 요청")
    void testFindTheme_Guest() throws Exception {
        int themeCode = 1;
        ThemeDTO dummyTheme = new ThemeDTO();
        dummyTheme.setName("Test Theme");

        when(themeService.findThemeDTOByThemeCode(themeCode)).thenReturn(dummyTheme);

        mockMvc.perform(get("/api/v1/themes/{themeCode}", themeCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value(themeCode + "번 테마 조회 성공"))
                .andExpect(jsonPath("$.result.name").value("Test Theme"));
    }

    // 1. 특정 테마 조회 API - 회원
    @Test
    @DisplayName("GET /api/v1/themes/{themeCode} - 회원 요청")
    void testFindTheme_Member() throws Exception {
        int themeCode = 1;
        String loginId = "user123";
        int memberCode = 123;
        ThemeDTO dummyTheme = new ThemeDTO();
        dummyTheme.setName("Member Test Theme");

        when(userService.findMemberCodeByLoginId(loginId)).thenReturn(memberCode);
        when(themeService.findTheme(themeCode, memberCode)).thenReturn(dummyTheme);

        mockMvc.perform(get("/api/v1/themes/{themeCode}", themeCode)
                        .requestAttr(SERVLET_REQUEST_ATTRIBUTE_KEY, loginId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value(themeCode + "번 테마 조회 성공"))
                .andExpect(jsonPath("$.result.name").value("Member Test Theme"));
    }

    // 2. 전체 장르 조회 API
    @Test
    @DisplayName("GET /api/v1/themes/genres - 전체 장르 조회")
    void testFindGenres() throws Exception {
        GenreDTO genre1 = new GenreDTO();
        genre1.setName("Action");
        GenreDTO genre2 = new GenreDTO();
        genre2.setName("Adventure");
        List<GenreDTO> genres = Arrays.asList(genre1, genre2);

        when(themeService.findGenres()).thenReturn(genres);

        mockMvc.perform(get("/api/v1/themes/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("전체 장르 조회 성공"))
                .andExpect(jsonPath("$.result[0].name").value("Action"))
                .andExpect(jsonPath("$.result[1].name").value("Adventure"));
    }

    // 3. 테마 필터링 및 검색 조회 API - 게스트
    @Test
    @DisplayName("GET /api/v1/themes - 테마 필터링 및 검색 조회 API (게스트)")
    void testFindThemeByGenresAndSearchOrderBySort_Guest() throws Exception {
        ThemeDTO theme = new ThemeDTO();
        theme.setName("Filtered Theme");
        List<ThemeDTO> themeDTOList = Arrays.asList(theme);

        when(themeService.findThemeByGenresAndSearchOrderBySort(any(Pageable.class), eq("like"), any(), eq("test")))
                .thenReturn(themeDTOList);

        mockMvc.perform(get("/api/v1/themes")
                        .param("filter", "like")
                        .param("content", "test")
                        .param("genres", "Action", "Comedy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("테마 조회 성공"))
                .andExpect(jsonPath("$.result[0].name").value("Filtered Theme"));
    }

    // 3. 테마 필터링 및 검색 조회 API - 회원
    @Test
    @DisplayName("GET /api/v1/themes - 테마 필터링 및 검색 조회 API (회원)")
    void testFindThemeByGenresAndSearchOrderBySort_Member() throws Exception {
        String loginId = "user123";
        int memberCode = 123;
        ThemeDTO theme = new ThemeDTO();
        theme.setName("Member Filtered Theme");
        List<ThemeDTO> themeDTOList = Arrays.asList(theme);

        when(userService.findMemberCodeByLoginId(loginId)).thenReturn(memberCode);
        when(themeService.findThemeByGenresAndSearchOrderBySort(any(Pageable.class), eq("scrap"), any(), eq("memberTest"), eq(memberCode)))
                .thenReturn(themeDTOList);

        mockMvc.perform(get("/api/v1/themes")
                        .param("filter", "scrap")
                        .param("content", "memberTest")
                        .param("genres", "Drama")
                        .requestAttr(SERVLET_REQUEST_ATTRIBUTE_KEY, loginId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("테마 조회 성공"))
                .andExpect(jsonPath("$.result[0].name").value("Member Filtered Theme"));
    }

    // 4. 업체별 테마 조회 API - 게스트
    @Test
    @DisplayName("GET /api/v1/themes/store/{storeCode} - 업체별 테마 조회 API (게스트)")
    void testFindThemeByStore_Guest() throws Exception {
        int storeCode = 1;
        ThemeDTO theme = new ThemeDTO();
        theme.setName("Store Theme Guest");
        List<ThemeDTO> themeDTOList = Arrays.asList(theme);

        when(themeService.findThemeByStoreOrderBySort(any(Pageable.class), eq("review"), eq(storeCode)))
                .thenReturn(themeDTOList);

        mockMvc.perform(get("/api/v1/themes/store/{storeCode}", storeCode)
                        .param("filter", "review"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("테마 조회 성공"))
                .andExpect(jsonPath("$.result[0].name").value("Store Theme Guest"));
    }

    // 4. 업체별 테마 조회 API - 회원
    @Test
    @DisplayName("GET /api/v1/themes/store/{storeCode} - 업체별 테마 조회 API (회원)")
    void testFindThemeByStore_Member() throws Exception {
        String loginId = "user123";
        int memberCode = 123;
        int storeCode = 1;
        ThemeDTO theme = new ThemeDTO();
        theme.setName("Store Theme Member");
        List<ThemeDTO> themeDTOList = Arrays.asList(theme);

        when(userService.findMemberCodeByLoginId(loginId)).thenReturn(memberCode);
        when(themeService.findThemeByStoreOrderBySort(any(Pageable.class), eq("like"), eq(storeCode), eq(memberCode)))
                .thenReturn(themeDTOList);

        mockMvc.perform(get("/api/v1/themes/store/{storeCode}", storeCode)
                        .param("filter", "like")
                        .requestAttr(SERVLET_REQUEST_ATTRIBUTE_KEY, loginId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("테마 조회 성공"))
                .andExpect(jsonPath("$.result[0].name").value("Store Theme Member"));
    }

    // 5. 테마 반응 생성 API
    @Test
    @DisplayName("POST /api/v1/themes/reaction - 테마 반응 생성")
    void testCreateThemeReaction() throws Exception {
        String loginId = "user123";
        ThemeReactionDTO reactionDTO = new ThemeReactionDTO();
        reactionDTO.setThemeCode(1);
        reactionDTO.setReaction("like");

        // 회원의 member 객체를 반환하도록 stub (실제 객체 구조에 맞게 조정)
        Member dummyMember = new Member();
        when(userService.findMemberByLoginId(loginId)).thenReturn(dummyMember);

        mockMvc.perform(post("/api/v1/themes/reaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reactionDTO))
                        .requestAttr(SERVLET_REQUEST_ATTRIBUTE_KEY, loginId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("테마 like 추가 성공"));
    }

    // 6. 테마 반응 취소 API
    @Test
    @DisplayName("DELETE /api/v1/themes/reaction - 테마 반응 취소")
    void testDeleteThemeReaction() throws Exception {
        String loginId = "user123";
        ThemeReactionDTO reactionDTO = new ThemeReactionDTO();
        reactionDTO.setThemeCode(1);
        reactionDTO.setReaction("scrap");
        int memberCode = 123;

        when(userService.findMemberCodeByLoginId(loginId)).thenReturn(memberCode);

        mockMvc.perform(delete("/api/v1/themes/reaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reactionDTO))
                        .requestAttr(SERVLET_REQUEST_ATTRIBUTE_KEY, loginId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("테마 scrap 삭제 성공"));
    }

    // 7. 회원별 테마 반응 조회 API
    @Test
    @DisplayName("GET /api/v1/themes/reactions/member - 회원별 테마 반응 조회 API")
    void testFindThemeByMemberReaction() throws Exception {
        String loginId = "user123";
        int memberCode = 123;
        FindThemeByReactionDTO reactionDTO = new FindThemeByReactionDTO();
        reactionDTO.setThemeCode(1);
        List<FindThemeByReactionDTO> reactionList = Arrays.asList(reactionDTO);

        when(userService.findMemberCodeByLoginId(loginId)).thenReturn(memberCode);
        when(themeService.findThemeByMemberReaction(any(Pageable.class), eq(memberCode), eq("like")))
                .thenReturn(reactionList);

        mockMvc.perform(get("/api/v1/themes/reactions/member")
                        .param("reaction", "like")
                        .requestAttr(SERVLET_REQUEST_ATTRIBUTE_KEY, loginId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("유저 별 like 테마 조회 성공"));
    }

    // 8. 최근 1주일 간 베스트 테마 조회 API - 게스트
    @Test
    @DisplayName("GET /api/v1/themes/week - 최근 1주일 베스트 테마 (게스트)")
    void testFindThemeByWeek_Guest() throws Exception {
        ThemeDTO theme = new ThemeDTO();
        theme.setName("Weekly Best Theme");
        List<ThemeDTO> weeklyThemes = Arrays.asList(theme);

        when(themeService.findThemeByWeek()).thenReturn(weeklyThemes);

        mockMvc.perform(get("/api/v1/themes/week"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("이번 주 베스트 테마 조회 성공"))
                .andExpect(jsonPath("$.result[0].name").value("Weekly Best Theme"));
    }

    // 8. 최근 1주일 간 베스트 테마 조회 API - 회원
    @Test
    @DisplayName("GET /api/v1/themes/week - 최근 1주일 베스트 테마 (회원)")
    void testFindThemeByWeek_Member() throws Exception {
        String loginId = "user123";
        int memberCode = 123;
        ThemeDTO theme = new ThemeDTO();
        theme.setName("Member Weekly Best Theme");
        List<ThemeDTO> weeklyThemes = Arrays.asList(theme);

        when(userService.findMemberCodeByLoginId(loginId)).thenReturn(memberCode);
        when(themeService.findThemeByWeek(memberCode)).thenReturn(weeklyThemes);

        mockMvc.perform(get("/api/v1/themes/week")
                        .requestAttr(SERVLET_REQUEST_ATTRIBUTE_KEY, loginId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("이번 주 베스트 테마 조회 성공"))
                .andExpect(jsonPath("$.result[0].name").value("Member Weekly Best Theme"));
    }

    // 9. 테마 추천 API
    @Test
    @DisplayName("GET /api/v1/themes/recommend - 테마 추천 (테마 코드 제공)")
    void testRecommendTheme_WithThemeCodes() throws Exception {
        ThemeDTO theme = new ThemeDTO();
        theme.setName("Recommended Theme");
        List<ThemeDTO> recommendedThemes = Arrays.asList(theme);

        when(themeService.recommendTheme(Arrays.asList(1, 2, 3))).thenReturn(recommendedThemes);

        mockMvc.perform(get("/api/v1/themes/recommend")
                        .param("themeCodes", "1", "2", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("추천 테마 조회 성공"))
                .andExpect(jsonPath("$.result[0].name").value("Recommended Theme"));
    }

    // 10. 사용자 스크랩 테마 조회 API
    @Test
    @DisplayName("GET /api/v1/themes/scraped - 사용자 스크랩 테마 조회")
    void testScrapTheme() throws Exception {
        String loginId = "user123";
        int memberCode = 123;
        ThemeDTO theme1 = new ThemeDTO();
        theme1.setName("Scraped Theme 1");
        ThemeDTO theme2 = new ThemeDTO();
        theme2.setName("Scraped Theme 2");
        List<ThemeDTO> scrapedThemes = Arrays.asList(theme1, theme2);

        when(userService.findMemberCodeByLoginId(loginId)).thenReturn(memberCode);
        when(themeService.getScrapedThemeByMemberCode(memberCode)).thenReturn(scrapedThemes);

        mockMvc.perform(get("/api/v1/themes/scraped")
                        .requestAttr(SERVLET_REQUEST_ATTRIBUTE_KEY, loginId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("사용자 스크랩 테마 목록 조회 성공"))
                .andExpect(jsonPath("$.result[0].name").value("Scraped Theme 1"))
                .andExpect(jsonPath("$.result[1].name").value("Scraped Theme 2"));
    }
}
