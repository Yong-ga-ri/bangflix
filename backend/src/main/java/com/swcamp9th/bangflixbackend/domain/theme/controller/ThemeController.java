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

/**
 * ThemeController는 테마 관련 API 엔드포인트를 제공하며,
 * 테마 조회, 장르 조회, 필터링/검색, 업체별 조회, 테마 반응(좋아요/스크랩) 등록/취소,
 * 회원별 반응 조회, 주간 베스트 테마 조회, 테마 추천, 그리고 사용자가 스크랩한 테마 조회 등의 기능을 지원합니다.
 */
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

    /**
     * 특정 테마 조회 API.
     * <p>
     * 테마 고유 코드(themeCode)를 경로 변수로 받아 해당 테마 정보를 조회합니다.
     * 로그인 여부에 따라 게스트용 조회와 회원용 조회 로직이 분기됩니다.
     * 회원의 경우 추가 개인화 정보가 반영됩니다.
     *
     * @param themeCode 테마 고유 코드
     * @param loginId   (선택적) 인증 토큰에서 추출한 로그인 아이디. 게스트의 경우 null.
     * @return 해당 테마 정보를 포함한 ThemeDTO와 성공 메시지가 담긴 응답
     */
    @GetMapping("/{themeCode}")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "특정 테마 조회 API",
            description = "테마 고유 코드(themeCode)를 이용해 테마 정보를 조회합니다. "
                    + "로그인 여부에 따라 게스트용 또는 회원용 조회 로직이 적용됩니다.")
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

    /**
     * 전체 장르 조회 API.
     * <p>
     * 데이터베이스에 저장된 모든 장르 정보를 조회하여 반환합니다.
     *
     * @return 모든 장르 정보를 담은 GenreDTO 리스트와 성공 메시지가 포함된 응답
     */
    @GetMapping("/genres")
    @SecurityRequirement(name = "Authorization")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "전체 장르 조회 API",
            description = "현재 데이터베이스에 존재하는 모든 장르 정보를 조회합니다.")
    public ResponseEntity<ResponseMessage<List<GenreDTO>>> findGenres() {
        List<GenreDTO> genres = themeService.findGenres();
        return ResponseEntity.ok(new ResponseMessage<>(200, "전체 장르 조회 성공", genres));
    }

    /**
     * 테마 필터링 및 검색 조회 API.
     * <p>
     * 페이징 정보와 정렬 필터, 선택적 장르 리스트, 그리고 검색어(content)를 기반으로 테마를 필터링 및 검색합니다.
     * 정렬 필터는 'like'(좋아요 수), 'scrap'(스크랩 수), 'review'(리뷰 수) 중 하나를 사용할 수 있으며,
     * 값이 없으면 기본적으로 테마 생성 순으로 정렬됩니다.
     * 여러 장르가 선택되면 OR 조건으로 조합되어 해당 장르에 해당하는 테마를 반환합니다.
     *
     * @param pageable 페이징 정보 (기본 10개씩 반환)
     * @param filter   정렬 필터 (like, scrap, review 등)
     * @param genres   선택적 장르 리스트 (여러 개 가능)
     * @param content  검색어 (테마 이름에 포함된 문자열)
     * @param loginId  (선택적) 인증 토큰에서 추출한 로그인 아이디. 게스트의 경우 null.
     * @return 필터링 및 검색 조건에 맞는 테마 목록(ThemeDTO 리스트)과 성공 메시지가 포함된 응답
     */
    @GetMapping("")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "테마 필터링 및 검색 조회 API",
            description = "페이징, 정렬 필터, 선택적 장르, 및 검색어(content)를 기반으로 테마를 조회합니다. "
                    + "정렬 필터는 'like', 'scrap', 'review' 중 하나이며, 지정하지 않으면 기본 생성 순으로 정렬됩니다.")
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

    /**
     * 업체별 테마 조회 API.
     * <p>
     * 업체 고유 코드(storeCode)를 경로 변수로 받아 해당 업체의 테마를 정렬 필터에 따라 조회합니다.
     * 정렬 필터는 'like', 'scrap', 'review'를 지원하며, 값이 없으면 기본적으로 최신순으로 정렬됩니다.
     *
     * @param storeCode 업체 고유 코드
     * @param pageable  페이징 정보
     * @param filter    정렬 필터 (like, scrap, review 등)
     * @param loginId   인증 토큰에서 추출한 로그인 아이디 (회원용)
     * @return 해당 업체의 테마 목록(ThemeDTO 리스트)과 성공 메시지가 포함된 응답
     */
    @GetMapping("/store/{storeCode}")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "업체별 테마 조회 API",
            description = "업체 고유 코드(storeCode)를 경로 변수로 전달하며, 정렬 필터(예: 'like', 'scrap', 'review')에 따라 업체의 테마를 조회합니다.")
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

    /**
     * 테마 반응(좋아요/스크랩) 생성 API.
     * <p>
     * 요청 본문에 테마 반응 정보를 담은 ThemeReactionDTO를 받아,
     * 로그인한 회원이 해당 테마에 대해 좋아요 또는 스크랩을 등록합니다.
     * reaction 값은 "like" 또는 "scrap" 문자열로 전달됩니다.
     *
     * @param themeReactionDTO 테마 반응 정보를 담은 DTO
     * @param loginId          인증 토큰에서 추출한 로그인 아이디
     * @return 테마 반응 등록 성공 메시지를 포함한 응답
     */
    @PostMapping("/reaction")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "테마 반응 생성 API",
            description = "ThemeReactionDTO를 전달받아 로그인한 회원이 해당 테마에 좋아요 또는 스크랩 반응을 등록합니다. "
                    + "reaction 값은 'like' 또는 'scrap' 문자열로 입력합니다.")
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

    /**
     * 테마 반응(좋아요/스크랩) 취소 API.
     * <p>
     * 요청 본문에 테마 반응 정보를 담은 ThemeReactionDTO를 받아,
     * 로그인한 회원이 등록한 해당 테마의 좋아요 또는 스크랩 반응을 취소합니다.
     * reaction 값은 "like" 또는 "scrap" 문자열로 전달됩니다.
     *
     * @param themeReactionDTO 테마 반응 정보를 담은 DTO
     * @param loginId          인증 토큰에서 추출한 로그인 아이디
     * @return 테마 반응 취소 성공 메시지를 포함한 응답
     */
    @DeleteMapping("/reaction")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "테마 반응 취소 API",
            description = "ThemeReactionDTO를 전달받아 로그인한 회원이 해당 테마에 등록한 좋아요 또는 스크랩 반응을 취소합니다. "
                    + "reaction 값은 'like' 또는 'scrap' 문자열로 입력합니다.")
    public ResponseEntity<ResponseMessage<Object>> deleteThemeReaction(
        @RequestBody ThemeReactionDTO themeReactionDTO,
        @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId
    ) {
        int memberCode = userService.findMemberCodeByLoginId(loginId);
        themeService.deleteThemeReaction(memberCode, themeReactionDTO);

        return ResponseEntity.ok(new ResponseMessage<>(200,
            "테마 " + themeReactionDTO.getReaction() + " 삭제 성공", null));
    }

    /**
     * 회원별 테마 반응 조회 API.
     * <p>
     * 로그인한 회원이 좋아요 또는 스크랩한 테마를 조회합니다.
     * 요청 파라미터 reaction에 "like" 또는 "scrap"을 전달하여 해당 반응을 기준으로 조회합니다.
     *
     * @param reaction  조회할 반응 종류 ("like" 또는 "scrap")
     * @param pageable  페이징 정보 (기본적으로 10개씩 반환)
     * @param loginId   인증 토큰에서 추출한 로그인 아이디
     * @return 회원의 테마 반응 정보를 담은 FindThemeByReactionDTO 리스트와 성공 메시지를 포함한 응답
     */
    @GetMapping("/reactions/member")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "회원별 테마 반응 조회 API",
            description = "로그인한 회원이 좋아요 또는 스크랩한 테마를 조회합니다. "
                    + "reaction 파라미터에 'like' 또는 'scrap'을 전달합니다.")
    public ResponseEntity<ResponseMessage<Object>> findThemeByMemberReaction(
        @RequestParam String reaction,
        @PageableDefault(size = 10) Pageable pageable,
        @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId
    ) {
        int memberCode = userService.findMemberCodeByLoginId(loginId);

        List<FindThemeByReactionDTO> themes = themeService.findThemeByMemberReaction(pageable, memberCode, reaction);

        return ResponseEntity.ok(new ResponseMessage<>(200,
            "유저 별 " + reaction + " 테마 조회 성공", themes));
    }

    /**
     * 최근 1주일 간 베스트 테마 조회 API.
     * <p>
     * 사용자가 조회한 시점을 기준으로, 과거 1주일 동안 좋아요 수가 가장 많은 상위 5개의 테마를 반환합니다.
     * 로그인한 경우 개인화된 결과가 적용됩니다.
     *
     * @param loginId (선택적) 인증 토큰에서 추출한 로그인 아이디. 게스트의 경우 null.
     * @return 최근 1주일 간 좋아요 수 기준 상위 테마 목록(ThemeDTO 리스트)과 성공 메시지를 포함한 응답
     */
    @GetMapping("/week")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "최근 1주일 간 베스트 테마 조회 API",
            description = "사용자가 조회한 시점을 기준으로 과거 1주일 동안 좋아요 수가 가장 많은 상위 5개의 테마를 반환합니다. "
                    + "로그인 여부에 따라 게스트용 및 회원용 조회가 분기됩니다.")
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

    /**
     * 테마 추천 API.
     * <p>
     * 선택적 테마 코드 리스트를 기준으로 추천 테마를 조회합니다.
     * 테마 코드가 제공되지 않으면 기본 추천 로직에 따라 추천 결과를 반환합니다.
     *
     * @param themeCodes (선택적) 추천 기준 테마 코드 리스트. null 또는 빈 리스트일 경우 기본 추천 로직 적용.
     * @return 추천 테마 목록(ThemeDTO 리스트)과 성공 메시지를 포함한 응답
     */
    @GetMapping("/recommend")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "테마 추천 API",
            description = "선택적 테마 코드(themeCodes)를 기준으로 추천 테마를 조회합니다. "
                    + "테마 코드가 없으면 기본 추천 로직에 따라 결과를 반환합니다.")
    public ResponseEntity<ResponseMessage<Object>> recommendTheme(
            @RequestParam(required = false) List<Integer> themeCodes
    ) {
        List<ThemeDTO> themes = themeService.recommendTheme(themeCodes);

        return ResponseEntity.ok(new ResponseMessage<>(200, "추천 테마 조회 성공", themes));
    }

    /**
     * 사용자 스크랩 테마 조회 API.
     * <p>
     * 로그인한 회원이 스크랩한 테마 목록을 조회하여 반환합니다.
     *
     * @param loginId 인증 토큰에서 추출한 로그인 아이디
     * @return 사용자가 스크랩한 테마 목록(ThemeDTO 리스트)과 성공 메시지를 포함한 응답
     */
    @GetMapping("/scraped")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "사용자 스크랩 테마 조회 API",
            description = "로그인한 회원이 스크랩한 테마 목록을 조회합니다.")
    public ResponseEntity<ResponseMessage<List<ThemeDTO>>> scrapTheme(
            @RequestAttribute(SERVLET_REQUEST_ATTRIBUTE_KEY) String loginId
    ) {
        List<ThemeDTO> themeDTOList = themeService.getScrapedThemeByMemberCode(
                userService.findMemberCodeByLoginId(loginId)
        );

        return ResponseEntity.ok(
                new ResponseMessage<>(
                        200,
                        "사용자 스크랩 테마 목록 조회 성공",
                        themeDTOList
                        ));
    }
}
