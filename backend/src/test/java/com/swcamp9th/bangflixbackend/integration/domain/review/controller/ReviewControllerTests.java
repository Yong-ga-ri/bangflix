package com.swcamp9th.bangflixbackend.integration.domain.review.controller;

import com.swcamp9th.bangflixbackend.domain.review.controller.ReviewController;
import com.swcamp9th.bangflixbackend.domain.review.dto.*;
import com.swcamp9th.bangflixbackend.domain.review.service.ReviewService;
import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import com.swcamp9th.bangflixbackend.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.ArrayList;
import java.util.List;

import static com.swcamp9th.bangflixbackend.shared.filter.RequestFilter.SERVLET_REQUEST_ATTRIBUTE_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private UserService userService;

    private static final String LOGIN_ID = "testUser";

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @Test
    void testCreateReview() throws Exception {
        // given
        String reviewJson = "{\"content\":\"Great movie!\",\"score\":5}";
        // review 파트 (JSON 형태)
        MockMultipartFile reviewPart = new MockMultipartFile("review", "review.json", "application/json", reviewJson.getBytes());
        // 이미지 파일 (선택적)
        MockMultipartFile imagePart = new MockMultipartFile("images", "image.jpg", "image/jpeg", "dummyImage".getBytes());

        // 로그인한 회원 정보 모킹
        Member dummyMember = new Member(); // 필요한 필드가 있다면 setter 등을 활용해 설정
        given(userService.findMemberByLoginId(LOGIN_ID)).willReturn(dummyMember);

        // when & then
        String response = mockMvc.perform(multipart("/api/v1/reviews")
                        .file(reviewPart)
                        .file(imagePart)
                        .requestAttr(SERVLET_REQUEST_ATTRIBUTE_KEY, LOGIN_ID)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("리뷰 작성 성공"))
                .andExpect(jsonPath("$.result").isEmpty())
                .andReturn().getResponse().getContentAsString();

        // AssertJ를 활용한 추가 검증
        assertThat(response).contains("리뷰 작성 성공");

        // reviewService.createReview 메서드가 올바른 인자로 호출되었는지 검증
        verify(reviewService).createReview(any(CreateReviewDTO.class), anyList(),  eq(dummyMember));
    }

    @Test
    void testDeleteReview() throws Exception {
        // given
        String reviewCodeJson = "{\"reviewCode\": 1}";
        given(userService.findMemberCodeByLoginId(LOGIN_ID)).willReturn(1);

        // when & then
        mockMvc.perform(delete("/api/v1/reviews")
                        .content(reviewCodeJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .requestAttr(SERVLET_REQUEST_ATTRIBUTE_KEY, LOGIN_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("리뷰 삭제 성공"))
                .andExpect(jsonPath("$.result").isEmpty());

        verify(reviewService).deleteReview(any(ReviewCodeDTO.class), eq(1));
    }

    @Test
    void testFindReviewListForGuest() throws Exception {
        // given
        int themeCode = 1;
        List<ReviewDTO> reviews = new ArrayList<>();
        given(reviewService.findReviewsBy(eq(themeCode), eq("highScore"), any(Pageable.class)))
                .willReturn(reviews);

        // when & then: 로그인 정보가 없는 guest 요청
        mockMvc.perform(get("/api/v1/reviews/{themeCode}", themeCode)
                        .param("filter", "highScore")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("리뷰 조회 성공"))
                .andExpect(jsonPath("$.result").isArray());

        verify(reviewService).findReviewsBy(eq(themeCode), eq("highScore"), any(Pageable.class));
    }

    @Test
    void testFindReviewListForMember() throws Exception {
        // given
        int themeCode = 1;
        int memberCode = 2;
        List<ReviewDTO> reviews = new ArrayList<>();
        given(userService.findMemberCodeByLoginId(LOGIN_ID)).willReturn(memberCode);
        given(reviewService.findReviewsBy(eq(themeCode), eq("lowScore"), any(Pageable.class), eq(memberCode)))
                .willReturn(reviews);

        // when & then: 로그인한 회원 요청
        mockMvc.perform(get("/api/v1/reviews/{themeCode}", themeCode)
                        .param("filter", "lowScore")
                        .contentType(MediaType.APPLICATION_JSON)
                        .requestAttr(SERVLET_REQUEST_ATTRIBUTE_KEY, LOGIN_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("리뷰 조회 성공"))
                .andExpect(jsonPath("$.result").isArray());

        verify(reviewService).findReviewsBy(eq(themeCode), eq("lowScore"), any(Pageable.class), eq(memberCode));
    }

    @Test
    void testFindReviewStatistics() throws Exception {
        // given
        int themeCode = 1;
        StatisticsReviewDTO statistics = new StatisticsReviewDTO();
        given(reviewService.findReviewStatistics(themeCode)).willReturn(statistics);

        // when & then
        mockMvc.perform(get("/api/v1/reviews/statistics/{themeCode}", themeCode)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("리뷰 통계 조회 성공"))
                .andExpect(jsonPath("$.result").exists());

        verify(reviewService).findReviewStatistics(themeCode);
    }

    @Test
    void testLikeReview() throws Exception {
        // given
        String reviewCodeJson = "{\"reviewCode\": 1}";
        int memberCode = 3;
        given(userService.findMemberCodeByLoginId(LOGIN_ID)).willReturn(memberCode);

        // when & then
        mockMvc.perform(post("/api/v1/reviews/likes")
                        .content(reviewCodeJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .requestAttr(SERVLET_REQUEST_ATTRIBUTE_KEY, LOGIN_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("리뷰 좋아요 성공"))
                .andExpect(jsonPath("$.result").isEmpty());

        verify(reviewService).likeReview(any(ReviewCodeDTO.class), eq(memberCode));
    }

    @Test
    void testDeleteLikeReview() throws Exception {
        // given
        String reviewCodeJson = "{\"reviewCode\": 1}";
        int memberCode = 4;
        given(userService.findMemberCodeByLoginId(LOGIN_ID)).willReturn(memberCode);

        // when & then
        mockMvc.perform(delete("/api/v1/reviews/likes")
                        .content(reviewCodeJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .requestAttr(SERVLET_REQUEST_ATTRIBUTE_KEY, LOGIN_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("리뷰 좋아요 취소 성공"))
                .andExpect(jsonPath("$.result").isEmpty());

        verify(reviewService).deleteLikeReview(any(ReviewCodeDTO.class), eq(memberCode));
    }

    @Test
    void testFindReviewReport() throws Exception {
        // given
        int memberCode = 5;
        ReviewReportDTO reportDTO = new ReviewReportDTO();
        given(userService.findMemberCodeByLoginId(LOGIN_ID)).willReturn(memberCode);
        given(reviewService.findReviewReport(memberCode)).willReturn(reportDTO);

        // when & then
        mockMvc.perform(get("/api/v1/reviews/user/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .requestAttr(SERVLET_REQUEST_ATTRIBUTE_KEY, LOGIN_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("유저 리뷰 report 조회 성공"))
                .andExpect(jsonPath("$.result").exists());

        verify(reviewService).findReviewReport(memberCode);
    }

    @Test
    void testFindReviewByMember() throws Exception {
        // given
        int memberCode = 6;
        List<ReviewDTO> reviews = new ArrayList<>();
        given(userService.findMemberCodeByLoginId(LOGIN_ID)).willReturn(memberCode);
        given(reviewService.findReviewByMemberCode(eq(memberCode), any(Pageable.class))).willReturn(reviews);

        // when & then
        mockMvc.perform(get("/api/v1/reviews/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .requestAttr(SERVLET_REQUEST_ATTRIBUTE_KEY, LOGIN_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("유저가 작성한 리뷰 조회 성공"))
                .andExpect(jsonPath("$.result").isArray());

        verify(reviewService).findReviewByMemberCode(eq(memberCode), any(Pageable.class));
    }
}
