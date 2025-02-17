package com.swcamp9th.bangflixbackend.domain.theme.controller;

import com.swcamp9th.bangflixbackend.domain.user.service.UserService;
import com.swcamp9th.bangflixbackend.shared.response.ResponseMessage;
import com.swcamp9th.bangflixbackend.domain.theme.dto.FindThemeByReactionDTO;
import com.swcamp9th.bangflixbackend.domain.theme.dto.ThemeReactionDTO;
import com.swcamp9th.bangflixbackend.domain.theme.dto.GenreDTO;
import com.swcamp9th.bangflixbackend.domain.theme.dto.ThemeDTO;
import com.swcamp9th.bangflixbackend.domain.theme.service.ThemeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.swcamp9th.bangflixbackend.shared.filter.RequestFilter.SERVLET_REQUEST_ATTRIBUTE_KEY;

@RestController
@RequestMapping("/api/v1/themes")
@Slf4j
public class ThemeController {

    private final ThemeService themeService;
    private final UserService userService;

    @Autowired
    public ThemeController(ThemeService themeService, UserService userService) {
        this.userService = userService;
        this.themeService = themeService;
    }

    @GetMapping("/{themeCode}")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "특정 테마를 조회하는 API.")
    public ResponseEntity<ResponseMessage<ThemeDTO>> findTheme(
        @PathVariable("themeCode") Integer themeCode,
        @RequestAttribute(value = SERVLET_REQUEST_ATTRIBUTE_KEY, required = false) String loginId
    ) {
        ThemeDTO themeDTO;

        if (loginId == null) {  // for guests
            themeDTO = themeService.findTheme(themeCode);
        } else {    // for members
            int memberCode = userService.findMemberCodeByLoginId(loginId);
            themeDTO = themeService.findTheme(themeCode, memberCode);
        }
        return ResponseEntity.ok(new ResponseMessage<>(200, themeCode + "번 테마 조회 성공", themeDTO));
    }

    @GetMapping("/genres")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "현재 DB에 존재하는 모든 장르를 조회하는 API.")
    public ResponseEntity<ResponseMessage<List<GenreDTO>>> findGenres() {
        List<GenreDTO> genres = themeService.findGenres();
        return ResponseEntity.ok(new ResponseMessage<>(200, "전체 장르 조회 성공", genres));
    }

    @GetMapping("")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "테마 필터링 & 검색해서 조회하는 API. filter 값 (like : 좋아요 수가 많은 테마, scrap : 스크랩 수가 많은 순, review: 리뷰 수가 많은 순, 값 안넣으면 테마 생성 순)"
        + ", genre 여러 개 선택 시, or 문으로 조합해서 선택한 장르에 해당하는 테마를 선택해 보여줌 "
        + "content를 작성하면 테마 이름에 content가 포함된 테마 반환")
    public ResponseEntity<ResponseMessage<List<ThemeDTO>>> findThemeByGenresAndSearchOrderBySort(
        @PageableDefault(size = 10, page = 0) Pageable pageable,
        @RequestParam(required = false) String filter,
        @RequestParam(required = false) List<String> genres,
        @RequestParam(required = false) String content,
        @RequestAttribute(value = SERVLET_REQUEST_ATTRIBUTE_KEY, required = false) String loginId
    ) {
        List<ThemeDTO> themeDTOList;

        if (loginId == null) {  // for guests
            themeDTOList = themeService.findThemeByGenresAndSearchOrderBySort(pageable, filter, genres, content);
        } else {    // for members
            int memberCode = userService.findMemberCodeByLoginId(loginId);
            themeDTOList = themeService.findThemeByGenresAndSearchOrderBySort(pageable, filter, genres, content, memberCode);
        }
        return ResponseEntity.ok(new ResponseMessage<>(200, "테마 조회 성공", themeDTOList));
    }

    @GetMapping("/store/{storeCode}")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "업체 별 테마 조회 API. filter 값 : (like, scrap, review, 값이 없다면 최신 순) ")
    public ResponseEntity<ResponseMessage<List<ThemeDTO>>> findThemeByStore(
        @PathVariable("storeCode") Integer storeCode,
        @PageableDefault(size = 10) Pageable pageable,
        @RequestParam(required = false) String filter,
        @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId
    ) {
        List<ThemeDTO> themeDTOList;

        if (loginId == null) {  // for guests
            themeDTOList = themeService.findThemeByStoreOrderBySort(pageable, filter, storeCode);
        } else {    // for members
            int memberCode = userService.findMemberCodeByLoginId(loginId);
            themeDTOList = themeService.findThemeByStoreOrderBySort(pageable, filter, storeCode, memberCode);
        }

        return ResponseEntity.ok(new ResponseMessage<>(200, "테마 조회 성공", themeDTOList));
    }

    @PostMapping("/reaction")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "테마 별로 스크랩이나 좋아요를 할 수 있는 API. 해당 API로 좋아요, 스크랩을 동시에 지원합니다. reaction 값으로는 String으로 like or scrap 입력해주시면 됩니다. ")
    public ResponseEntity<ResponseMessage<Object>> createThemeReaction(
        @RequestBody ThemeReactionDTO themeReactionDTO,
        @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId
    ) {
        themeService.createThemeReaction(
                userService.findMemberByLoginId(loginId),
                themeReactionDTO
        );

        return ResponseEntity.ok(new ResponseMessage<>(200,
            "테마 " + themeReactionDTO.getReaction() + " 추가 성공", null));
    }

    @DeleteMapping("/reaction")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "테마 별로 유저가 한 스크랩이나 좋아요를 취소 할 수 있는 API. 해당 API로 좋아요 취소, 스크랩 취소 동시에 지원합니다. reaction 값으로는 String으로 like or scrap 입력해주시면 됩니다. ")
    public ResponseEntity<ResponseMessage<Object>> deleteThemeReaction(
        @RequestBody ThemeReactionDTO themeReactionDTO,
        @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId
    ) {

        themeService.deleteThemeReaction(loginId, themeReactionDTO);

        return ResponseEntity.ok(new ResponseMessage<>(200,
            "테마 " + themeReactionDTO.getReaction() + " 삭제 성공", null));
    }

    @GetMapping("/reactions/member")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "유저 별로 스크랩이나 좋아요한 테마 조회 API. 해당 API로 좋아요, 스크랩을 동시에 지원합니다. reaction 값으로는 String으로 like or scrap 입력해주시면 됩니다. ")
    public ResponseEntity<ResponseMessage<Object>> findThemeByMemberReaction(
        @RequestParam String reaction,
        @PageableDefault(size = 10) Pageable pageable,
        @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId
    ) {

        List<FindThemeByReactionDTO> themes = themeService.findThemeByMemberReaction(pageable, loginId, reaction);

        return ResponseEntity.ok(new ResponseMessage<>(200,
            "유저 별 " + reaction + " 테마 조회 성공", themes));
    }

    @GetMapping("/week")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "사용자가 조회한 날 부터 과거 1주일 전 데이터를 확인해 좋아요 수가 가장 많은 theme 5개 반환하는 API.")
    public ResponseEntity<ResponseMessage<List<ThemeDTO>>> findThemeByWeek(
        @RequestAttribute(value = SERVLET_REQUEST_ATTRIBUTE_KEY, required = false) String loginId
    ) {
        List<ThemeDTO> themes;
        if (loginId == null) {
            themes = themeService.findThemeByWeek();
        } else {
            int memberCode = userService.findMemberCodeByLoginId(loginId);
            themes = themeService.findThemeByWeek(memberCode);
        }

        return ResponseEntity.ok(new ResponseMessage<>(200, "이번 주 베스트 테마 조회 성공", themes));
    }

    @GetMapping("/recommend")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "테마 추천 API.")
    public ResponseEntity<ResponseMessage<Object>> recommendTheme(
            @RequestParam(required = false) List<Integer> themeCodes
    ) {
        List<ThemeDTO> themes = themeService.recommendTheme(themeCodes);

        return ResponseEntity.ok(new ResponseMessage<>(200, "추천 테마 조회 성공", themes));
    }

    @GetMapping("scraped")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "사용자가 스크랩한 테마 목록")
    public ResponseEntity<ResponseMessage<List<ThemeDTO>>> scrapTheme(
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId
    ) {
        List<ThemeDTO> themeDTOs = themeService.getScrapedTheme(loginId);

        return ResponseEntity.ok(new ResponseMessage<>(200, "사용자 스크랩 테마 목록 조회 성공", themeDTOs));
    }
}
