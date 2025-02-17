package com.swcamp9th.bangflixbackend.domain.review.service;

import com.swcamp9th.bangflixbackend.domain.review.dto.CreateReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewCodeDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewReportDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.StatisticsReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.entity.Review;
import java.util.List;

import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;


/**
 * 리뷰 관련 비즈니스 로직을 처리하는 서비스 인터페이스입니다.
 * 게스트와 회원용 리뷰 조회, 리뷰 생성 및 삭제, 리뷰 좋아요 등 다양한 기능을 제공합니다.
 * 게스트용 메서드는 로그인한 회원의 memberCode를 전달하지 않으며, 회원용 메서드는 memberCode를 전달합니다.
 */
public interface ReviewService {

    /**
     * 특정 테마에 대한 리뷰 목록을 조회합니다.
     * 기본적으로 최신순(생성일 내림차순) 정렬하며, 선택적 정렬 필터("highScore" 또는 "lowScore")에 따라 정렬 방식이 변경됩니다.
     *
     * @param themeCode 조회할 테마의 코드
     * @param filter 정렬 필터 (예: "highScore", "lowScore") - null인 경우 기본 최신순 정렬
     * @param pageable 페이징 정보(페이지 번호, 크기 등)
     * @return 조회된 리뷰 목록을 ReviewDTO 리스트로 반환
     */
    List<ReviewDTO> findReviewsWithFilters(Integer themeCode, String filter, Pageable pageable);

    /**
     * 특정 테마에 대한 리뷰 목록을 회원용으로 조회합니다.
     * 로그인한 회원의 memberCode에 기반하여 맞춤형 리뷰 데이터를 제공할 수 있습니다.
     *
     * @param themeCode 조회할 테마의 코드
     * @param filter 정렬 필터 (예: "highScore", "lowScore") - null인 경우 기본 최신순 정렬
     * @param pageable 페이징 정보(페이지 번호, 크기 등)
     * @param memberCode 로그인한 회원의 고유 코드
     * @return 조회된 리뷰 목록을 ReviewDTO 리스트로 반환
     */
    List<ReviewDTO> findReviewsWithFilters(Integer themeCode, String filter, Pageable pageable, int memberCode);

    /**
     * 특정 테마에 대한 리뷰 통계 정보를 조회합니다.
     * 통계 정보에는 리뷰 개수, 점수 분포 등 다양한 항목이 포함됩니다.
     *
     * @param themeCode 조회할 테마의 코드
     * @return 리뷰 통계 정보를 담은 StatisticsReviewDTO
     */
    StatisticsReviewDTO findReviewStatistics(Integer themeCode);

    /**
     * Review 엔티티 리스트를 ReviewDTO 리스트로 변환합니다.
     *
     * @param sublist 변환할 Review 엔티티 리스트
     * @return 변환된 ReviewDTO 리스트
     */
    List<ReviewDTO> getReviewDTOS(List<Review> sublist);

    /**
     * 회원용으로 Review 엔티티 리스트를 ReviewDTO 리스트로 변환합니다.
     * 회원의 고유 memberCode를 활용하여 추가 정보를 포함시킬 수 있습니다.
     *
     * @param sublist 변환할 Review 엔티티 리스트
     * @param memberCode 로그인한 회원의 고유 코드
     * @return 변환된 ReviewDTO 리스트
     */
    List<ReviewDTO> getReviewDTOS(List<Review> sublist, int memberCode);

    /**
     * 단일 Review 엔티티를 회원용 ReviewDTO로 변환합니다.
     * 회원 정보를 기반으로 추가 데이터를 포함시킬 수 있습니다.
     *
     * @param review 변환할 Review 엔티티
     * @param memberCode 로그인한 회원의 고유 코드
     * @return 변환된 ReviewDTO
     */
    ReviewDTO getReviewDTO(Review review, Integer memberCode);

    /**
     * 로그인한 회원의 리뷰 리포트 데이터를 조회합니다.
     * 리포트에는 리뷰 개수, 좋아요 수 등 회원의 리뷰 활동 통계가 포함됩니다.
     *
     * @param memberCode 로그인한 회원의 고유 코드
     * @return 회원 리뷰 리포트 정보를 담은 ReviewReportDTO
     */
    ReviewReportDTO findReviewReport(int memberCode);

    /**
     * 로그인한 회원이 작성한 리뷰 목록을 페이징 처리하여 조회합니다.
     *
     * @param memberCode 로그인한 회원의 고유 코드
     * @param pageable 페이징 정보(페이지 번호, 크기 등)
     * @return 회원이 작성한 리뷰 목록을 ReviewDTO 리스트로 반환
     */
    List<ReviewDTO> findReviewByMemberCode(int memberCode, Pageable pageable);

    /**
     * 특정 리뷰에 대해 로그인한 회원이 좋아요를 등록합니다.
     *
     * @param reviewCodeDTO 좋아요를 등록할 리뷰의 코드 정보를 담은 DTO
     * @param memberCode 로그인한 회원의 고유 코드
     */
    void likeReview(ReviewCodeDTO reviewCodeDTO, int memberCode);

    /**
     * 특정 리뷰에 대해 로그인한 회원이 등록한 좋아요를 취소합니다.
     *
     * @param reviewCodeDTO 좋아요를 취소할 리뷰의 코드 정보를 담은 DTO
     * @param memberCode 로그인한 회원의 고유 코드
     */
    void deleteLikeReview(ReviewCodeDTO reviewCodeDTO, int memberCode);

    /**
     * 리뷰를 생성합니다.
     * 첨부된 이미지 파일들을 함께 저장하며, 리뷰 생성 시 회원 정보를 기반으로 리뷰를 등록합니다.
     *
     * @param newReview 리뷰 생성에 필요한 데이터를 담은 CreateReviewDTO
     * @param images 첨부할 이미지 파일들의 리스트 (선택적)
     * @param member 리뷰를 작성하는 회원 엔티티
     */
    void createReview(CreateReviewDTO newReview, List<MultipartFile> images, Member member);

    /**
     * 리뷰를 삭제합니다.
     * 로그인한 회원의 권한을 확인한 후, 리뷰의 활성 상태를 변경하여 삭제합니다.
     *
     * @param reviewCodeDTO 삭제할 리뷰의 코드 정보를 담은 DTO
     * @param memberCode 로그인한 회원의 고유 코드
     */
    void deleteReview(ReviewCodeDTO reviewCodeDTO, int memberCode);
}
