package com.swcamp9th.bangflixbackend.domain.review.service;

import com.swcamp9th.bangflixbackend.domain.review.dto.CreateReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewCodeDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewReportDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.StatisticsReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.UpdateReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.entity.Review;
import com.swcamp9th.bangflixbackend.domain.review.entity.ReviewFile;
import com.swcamp9th.bangflixbackend.domain.review.entity.ReviewLike;
import com.swcamp9th.bangflixbackend.domain.theme.entity.Theme;
import com.swcamp9th.bangflixbackend.domain.review.repository.ReviewFileRepository;
import com.swcamp9th.bangflixbackend.domain.review.repository.ReviewLikeRepository;
import com.swcamp9th.bangflixbackend.domain.review.repository.ReviewRepository;
import com.swcamp9th.bangflixbackend.domain.review.repository.ReviewTendencyGenreRepository;
import com.swcamp9th.bangflixbackend.domain.theme.repository.ThemeRepository;
import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import com.swcamp9th.bangflixbackend.domain.user.repository.UserRepository;
import com.swcamp9th.bangflixbackend.shared.exception.AlreadyLikedException;
import com.swcamp9th.bangflixbackend.shared.exception.FileUploadException;
import com.swcamp9th.bangflixbackend.shared.exception.InvalidUserException;
import com.swcamp9th.bangflixbackend.shared.exception.LikeNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ModelMapper modelMapper;
    private final ThemeRepository themeRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewFileRepository reviewFileRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewTendencyGenreRepository reviewTendencyGenreRepository;

    @Autowired
    public ReviewServiceImpl(
            ModelMapper modelMapper,
            ThemeRepository themeRepository,
            UserRepository userRepository,
            ReviewRepository reviewRepository,
            ReviewFileRepository reviewFileRepository,
            ReviewLikeRepository reviewLikeRepository,
            ReviewTendencyGenreRepository reviewTendencyGenreRepository
    ) {
        this.modelMapper = modelMapper;
        this.themeRepository = themeRepository;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.reviewFileRepository = reviewFileRepository;
        this.reviewLikeRepository = reviewLikeRepository;
        this.reviewTendencyGenreRepository = reviewTendencyGenreRepository;
    }

    @Transactional
    @Override
    public void createReview(
            CreateReviewDTO newReview,
            List<MultipartFile> images,
            Member member
    ) {

        // 리뷰 저장
        Review review = modelMapper.map(newReview, Review.class);
        Theme theme = themeRepository.findById(newReview.getThemeCode()).orElse(null);
        review.setTheme(theme);
        review.setMember(member);
        review.setActive(true);
        review.setCreatedAt(LocalDateTime.now());
        Review insertReview = reviewRepository.save(review);

        // 리뷰 파일 저장
        if(images != null) {
            saveReviewFile(images, insertReview);
        }

        // 멤버 포인트 올리기
        member.setPoint(member.getPoint()+5);
        userRepository.save(member);
    }

    @Transactional
    @Override
    public void deleteReview(ReviewCodeDTO reviewCodeDTO, String loginId) {

        // 기존 리뷰 조회
        Review existingReview = reviewRepository.findById(reviewCodeDTO.getReviewCode()).orElse(null);

        if(existingReview == null)
            throw new RuntimeException("Review not found");

        if(loginId.equals(existingReview.getMember().getId())) {
            existingReview.setActive(false);
            reviewRepository.save(existingReview);
        }
        else {
            throw new InvalidUserException("리뷰 삭제 권한이 없습니다");
        }
    }

    @Transactional
    @Override
    public List<ReviewDTO> findReviewsWithFilters(
            Integer themeCode,
            String filter,
            Pageable pageable,
            String loginId
    ) {

        // 테마 코드로 리뷰를 모두 조회
        List<Review> reviews = reviewRepository.findByThemeCodeAndActiveTrueWithFetchJoin(themeCode, pageable);
        Member member = userRepository.findById(loginId).orElse(null);

        // 필터가 있을 경우 해당 조건에 맞게 정렬
        if (filter != null) {
            switch (filter) {
                case "highScore":

                    // 점수 높은 순 정렬
                    reviews.sort(Comparator.comparing(Review::getTotalScore).reversed()
                        .thenComparing(Comparator.comparing(Review::getCreatedAt).reversed()));
                    break;
                case "lowScore":

                    // 점수 낮은 순 정렬
                    reviews.sort(Comparator.comparing(Review::getTotalScore)
                        .thenComparing(Comparator.comparing(Review::getCreatedAt).reversed()));
                    break;
                default:

                    // 필터가 일치하지 않으면 최신순으로 정렬 (기본값)
                    reviews.sort(Comparator.comparing(Review::getCreatedAt).reversed());
                    break;
            }
        } else {

            // 필터가 없으면 최신순으로 정렬 (기본값)
            reviews.sort(Comparator.comparing(Review::getCreatedAt).reversed());
        }

        if(member != null)
            return getReviewDTOS(reviews, member.getMemberCode());
        else
            return getReviewDTOS(reviews, null);
    }

    @Transactional
    @Override
    public List<ReviewDTO> getReviewDTOS(List<Review> sublist, Integer memberCode) {
        List<ReviewDTO> result = sublist.stream()
            .map(review -> {
                ReviewDTO reviewDTO = modelMapper.map(review, ReviewDTO.class);

                // 이미지 경로 추가
                reviewDTO.setImagePaths(findImagePathsByReviewCode(review.getReviewCode()));
                reviewDTO.setLikes(findReviewLikesByReviewCode(review.getReviewCode()));
                reviewDTO.setMemberNickname(review.getMember().getNickname());
                reviewDTO.setReviewCode(review.getReviewCode());
                reviewDTO.setMemberCode(review.getMember().getMemberCode());
                reviewDTO.setMemberImage(review.getMember().getImage());
                List<String> genres = findMemberTendencyGenre(review.getMember().getMemberCode());
                reviewDTO.setThemeCode(review.getTheme().getThemeCode());
                reviewDTO.setThemeImage(review.getTheme().getPosterImage());
                reviewDTO.setThemeName(review.getTheme().getName());
                ReviewLike reviewLike = reviewLikeRepository.findByReviewCodeAndMemberCode(review.getReviewCode(), memberCode).orElse(null);

                if (reviewLike != null)
                    reviewDTO.setIsLike(true);
                else
                    reviewDTO.setIsLike(false);

                if(!genres.isEmpty())
                    reviewDTO.setGenres(genres);

                return reviewDTO;
            }).toList();

        return result;
    }

    @Transactional
    @Override
    public ReviewDTO getReviewDTO(Review review, Integer memberCode) {

        ReviewDTO reviewDTO = modelMapper.map(review, ReviewDTO.class);

        // 이미지 경로 추가
        reviewDTO.setImagePaths(findImagePathsByReviewCode(review.getReviewCode()));
        reviewDTO.setLikes(findReviewLikesByReviewCode(review.getReviewCode()));
        reviewDTO.setMemberNickname(review.getMember().getNickname());
        reviewDTO.setReviewCode(review.getReviewCode());
        reviewDTO.setMemberCode(review.getMember().getMemberCode());
        reviewDTO.setMemberImage(review.getMember().getImage());
        List<String> genres = findMemberTendencyGenre(review.getMember().getMemberCode());
        reviewDTO.setThemeCode(review.getTheme().getThemeCode());
        reviewDTO.setThemeImage(review.getTheme().getPosterImage());
        reviewDTO.setThemeName(review.getTheme().getName());

        ReviewLike reviewLike = reviewLikeRepository.findByReviewCodeAndMemberCode(review.getReviewCode(), memberCode).orElse(null);

        if (reviewLike != null)
            reviewDTO.setIsLike(true);
        else
            reviewDTO.setIsLike(false);

        if(!genres.isEmpty())
            reviewDTO.setGenres(genres);

        return reviewDTO;
    }

    @Transactional
    @Override
    public ReviewReportDTO findReviewReport(String loginId) {
        Member member = userRepository.findById(loginId).orElseThrow();
        Integer avgScore = reviewRepository.findAvgScoreByMemberCode(member.getMemberCode());

        if(avgScore == null)
            return null;

        Pageable pageable = PageRequest.of(0, 3);
        List<String> genres = reviewRepository.findTopGenresByMemberCode(member.getMemberCode(), pageable);
        ReviewReportDTO reviewReportDTO = new ReviewReportDTO(avgScore, genres);
        return reviewReportDTO;
    }

    @Transactional
    @Override
    public List<ReviewDTO> findReviewByMember(String loginId, Pageable pageable) {
        Member member = userRepository.findById(loginId).orElseThrow();
        List<Review> review = reviewRepository.findByMemberCode(member.getMemberCode(), pageable);

        if(review == null || review.isEmpty())
            return null;
        return getReviewDTOS(review, member.getMemberCode());
    }

    @Transactional
    @Override
    public ReviewDTO findReviewDetail(String loginId, Integer reviewCode) {
        Member member = userRepository.findById(loginId).orElseThrow();
        Review review = reviewRepository.findById(reviewCode).orElseThrow();

        if(!review.getMember().getMemberCode().equals(member.getMemberCode()))
            throw new InvalidUserException("리뷰 수정 권한이 없습니다");

        return getReviewDTO(review, member.getMemberCode());
    }

    @Transactional
    @Override
    public StatisticsReviewDTO findReviewStatistics(Integer themeCode) {

        StatisticsReviewDTO statisticsReviewDTO = reviewRepository.findStatisticsByThemeCode(themeCode)
                .orElse(null);

        if (statisticsReviewDTO == null || statisticsReviewDTO.getAvgTotalScore() == null)
            return null;

        return statisticsReviewDTO;
    }

    @Transactional
    @Override
    public void likeReview(ReviewCodeDTO reviewCodeDTO, String loginId) {
        Member member = userRepository.findById(loginId).orElseThrow();
        ReviewLike reviewLike = reviewLikeRepository.findByMemberCodeAndReviewCode(
            member.getMemberCode(), reviewCodeDTO.getReviewCode()
        );

        if (reviewLike == null) {
            ReviewLike newReviewLike = new ReviewLike();
            newReviewLike.setMemberCode(member.getMemberCode());
            newReviewLike.setReviewCode(reviewCodeDTO.getReviewCode());
            newReviewLike.setCreatedAt(LocalDateTime.now());
            newReviewLike.setActive(true);
            reviewLikeRepository.save(newReviewLike);
        } else {
            if(reviewLike.getActive()) {
                throw new AlreadyLikedException("이미 좋아요가 존재합니다.");
            } else{
                reviewLike.setActive(true);
                reviewLikeRepository.save(reviewLike);
            }
        }
    }

    @Transactional
    @Override
    public void deleteLikeReview(ReviewCodeDTO reviewCodeDTO, String loginId) {
        Member member = userRepository.findById(loginId).orElseThrow();
        ReviewLike reviewLike = reviewLikeRepository.findByMemberCodeAndReviewCode(
            member.getMemberCode(), reviewCodeDTO.getReviewCode()
        );

        if (reviewLike == null) {
            throw new LikeNotFoundException("좋아요가 존재하지 않습니다.");
        } else {
            if(reviewLike.getActive()) {
                reviewLike.setActive(false);
                reviewLikeRepository.save(reviewLike);
            } else{
                throw new LikeNotFoundException("좋아요가 존재하지 않습니다.");
            }
        }
    }

    private void saveReviewFile(List<MultipartFile> images, Review review) {
        String uploadsDir = "src/main/resources/static/uploadFiles/reviewFile";

        try {
            for(MultipartFile file : images) {
                String fileName = UUID.randomUUID().toString().replace("-", "") + "_" + file.getOriginalFilename();
                String filePath = uploadsDir + "/" + fileName;
                String dbFilePath = "/uploadFiles/reviewFile/" + fileName;

                Path path = Paths.get(filePath);
                Files.createDirectories(path.getParent());
                Files.write(path, file.getBytes());

                reviewFileRepository.save(ReviewFile.builder()
                        .review(review)
                        .active(true)
                        .createdAt(LocalDateTime.now())
                        .url(dbFilePath)
                        .build());
            }
        } catch (IOException e) {
            throw new FileUploadException("파일 업로드에 실패했습니다.");
        }
    }

    public List<String> findImagePathsByReviewCode(Integer reviewCode) {
        return reviewFileRepository.findByReview_ReviewCode(reviewCode)
            .stream().map(ReviewFile::getUrl).toList();
    }

    private Integer findReviewLikesByReviewCode(Integer reviewCode) {
        return reviewLikeRepository.findByReviewCode(reviewCode).size();
    }

    private List<String> findMemberTendencyGenre(Integer memberCode) {
        return reviewTendencyGenreRepository
            .findMemberGenreByMemberCode(memberCode).stream()
            .map(reviewTendencyGenre -> reviewTendencyGenre.getGenre().getName()).toList();
    }
}
