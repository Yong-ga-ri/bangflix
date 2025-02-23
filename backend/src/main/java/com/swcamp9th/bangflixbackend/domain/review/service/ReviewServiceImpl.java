package com.swcamp9th.bangflixbackend.domain.review.service;

import com.swcamp9th.bangflixbackend.domain.review.exception.ReviewNotFoundException;
import com.swcamp9th.bangflixbackend.domain.review.dto.CreateReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewCodeDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.ReviewReportDTO;
import com.swcamp9th.bangflixbackend.domain.review.dto.StatisticsReviewDTO;
import com.swcamp9th.bangflixbackend.domain.review.entity.Review;
import com.swcamp9th.bangflixbackend.domain.review.entity.ReviewFile;
import com.swcamp9th.bangflixbackend.domain.review.entity.ReviewLike;
import com.swcamp9th.bangflixbackend.domain.review.exception.ReviewAlreadyLiked;
import com.swcamp9th.bangflixbackend.domain.review.exception.ReviewNotLikedException;
import com.swcamp9th.bangflixbackend.domain.review.repository.ReviewFileRepository;
import com.swcamp9th.bangflixbackend.domain.review.repository.ReviewLikeRepository;
import com.swcamp9th.bangflixbackend.domain.review.repository.ReviewRepository;
import com.swcamp9th.bangflixbackend.domain.review.repository.ReviewTendencyGenreRepository;
import com.swcamp9th.bangflixbackend.domain.theme.service.ThemeService;
import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import com.swcamp9th.bangflixbackend.domain.user.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.swcamp9th.bangflixbackend.shared.error.exception.FileUploadException;
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
    private final ThemeService themeService;
    private final UserService userService;
    private final ReviewRepository reviewRepository;
    private final ReviewFileRepository reviewFileRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewTendencyGenreRepository reviewTendencyGenreRepository;

    @Autowired
    public ReviewServiceImpl(
            ModelMapper modelMapper,
            ThemeService themeService,
            UserService userService,
            ReviewRepository reviewRepository,
            ReviewFileRepository reviewFileRepository,
            ReviewLikeRepository reviewLikeRepository,
            ReviewTendencyGenreRepository reviewTendencyGenreRepository
    ) {
        this.modelMapper = modelMapper;
        this.themeService = themeService;
        this.userService = userService;
        this.reviewRepository = reviewRepository;
        this.reviewFileRepository = reviewFileRepository;
        this.reviewLikeRepository = reviewLikeRepository;
        this.reviewTendencyGenreRepository = reviewTendencyGenreRepository;
    }

    @Transactional
    @Override
    public void createReview(
            CreateReviewDTO newReviewDTO,
            List<MultipartFile> images,
            Member member
    ) {

        // 리뷰 저장
        Review review = modelMapper.map(newReviewDTO, Review.class);
        review.setTheme(
                themeService.findThemeByThemeCode(newReviewDTO.getThemeCode())
        );
        review.setMember(member);
        review.setActive(true);
        review.setCreatedAt(LocalDateTime.now());
        Review createdReview = reviewRepository.save(review);

        // 리뷰 파일 저장
        if(images != null) {
            saveReviewFile(images, createdReview);
        }

        // 멤버 포인트 올리기
        userService.memberGainPoint(member, 5);
    }

    @Transactional
    @Override
    public void deleteReview(ReviewCodeDTO reviewCodeDTO, int memberCode) {
        Review existingReview = reviewRepository.findById(reviewCodeDTO.getReviewCode())
                .orElseThrow(ReviewNotFoundException::new);
        existingReview.setActive(false);
        reviewRepository.save(existingReview);
    }

    @Transactional
    @Override
    public List<ReviewDTO> findReviewsBy(
            int themeCode,
            String sort,
            Pageable pageable,
            int memberCode
    ) {
        List<Review> reviews = reviewRepository.findReviewListByThemeCode(pageable, themeCode);
        sortReviewList(sort, reviews);
        return toReviewDTOList(reviews, memberCode);
    }

    @Transactional
    @Override
    public List<ReviewDTO> findReviewsBy(
            int themeCode,
            String sort,
            Pageable pageable
    ) {
        List<Review> reviews = reviewRepository.findReviewListByThemeCode(pageable, themeCode);
        sortReviewList(sort, reviews);
        return toReviewDTOList(reviews);
    }

    @Transactional
    @Override
    public ReviewReportDTO findReviewReport(int memberCode) {
        Integer avgScore = reviewRepository.findAvgScoreByMemberCode(memberCode);

        if(avgScore == null)
            return null;

        Pageable pageable = PageRequest.of(0, 3);
        List<String> genres = reviewRepository.findTopGenresByMemberCode(pageable, memberCode);
        ReviewReportDTO reviewReportDTO = new ReviewReportDTO(avgScore, genres);
        return reviewReportDTO;
    }

    @Transactional
    @Override
    public List<ReviewDTO> findReviewByMemberCode(int memberCode, Pageable pageable) {
        return toReviewDTOList(
                reviewRepository.findByMemberCode(pageable, memberCode)
                , memberCode
        );
    }

    @Transactional
    @Override
    public StatisticsReviewDTO findReviewStatistics(int themeCode) {

        StatisticsReviewDTO statisticsReviewDTO = reviewRepository.findStatisticsByThemeCode(themeCode)
                .orElse(null);

        if (statisticsReviewDTO == null || statisticsReviewDTO.getAvgTotalScore() == null)
            return null;

        return statisticsReviewDTO;
    }

    @Transactional
    @Override
    public void likeReview(ReviewCodeDTO reviewCodeDTO, int memberCode) {
        Optional<ReviewLike> reviewLikeOptional = reviewLikeRepository.findByMemberCodeAndReviewCode(
                memberCode,
                reviewCodeDTO.getReviewCode());

        if (reviewLikeOptional.isEmpty()) {
            ReviewLike newReviewLike = new ReviewLike();
            newReviewLike.setMemberCode(memberCode);
            newReviewLike.setReviewCode(reviewCodeDTO.getReviewCode());
            newReviewLike.setCreatedAt(LocalDateTime.now());
            newReviewLike.setActive(true);
            reviewLikeRepository.save(newReviewLike);
        } else {
            ReviewLike reviewLike = reviewLikeOptional.get();
            if(reviewLike.getActive()) {
                throw new ReviewAlreadyLiked();
            } else{
                reviewLike.setActive(true);
                reviewLikeRepository.save(reviewLike);
            }
        }
    }

    @Transactional
    @Override
    public void deleteLikeReview(ReviewCodeDTO reviewCodeDTO, int memberCode) {
        ReviewLike reviewLike = reviewLikeRepository.findByMemberCodeAndReviewCode(memberCode, reviewCodeDTO.getReviewCode())
                .orElseThrow(ReviewNotLikedException::new);

        if(reviewLike.getActive()) {
            reviewLike.setActive(false);
            reviewLikeRepository.save(reviewLike);
        } else{
            throw new ReviewNotLikedException();
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
            throw new FileUploadException();
        }
    }

    public List<String> findImagePathsByReviewCode(int reviewCode) {
        return reviewFileRepository.findByReview_ReviewCode(reviewCode)
                .stream()
                .map(ReviewFile::getUrl)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private int findReviewLikesByReviewCode(int reviewCode) {
        return reviewLikeRepository.countReviewLikesByReviewCode(reviewCode);
    }

    private List<String> findMemberTendencyGenre(int memberCode) {
        return reviewTendencyGenreRepository.findMemberGenreByMemberCode(memberCode).stream()
                .map(reviewTendencyGenre ->
                        reviewTendencyGenre.getGenre().getName()
                )
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    @Transactional
    public ReviewDTO getBestReviewByStoreCode(int storeCode) {
        List<ReviewLike> reviewLike = reviewLikeRepository.findBestReviewByStoreCode(storeCode);

        if(reviewLike.isEmpty())
            return null;

        Review review = reviewRepository.findById(reviewLike.get(0).getReviewCode()).orElse(null);

        return toBaseReviewDTO(review);

    }

    private boolean checkMemberLikeReview(int memberCode, int reviewCode) {
        reviewLikeRepository.
        return true;
    }

    private void sortReviewList(
            String sort,
            List<Review> reviews
    ) {
        if (sort == null) sort = "";
        switch (sort) {
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
    }

    @Transactional
    @Override
    public List<ReviewDTO> toReviewDTOList(
            List<Review> reviewList,
            int memberCode
    ) {
        return reviewList.stream()
                .map(review -> {
                    ReviewDTO reviewDTO = toBaseReviewDTO(review);
                    ReviewLike reviewLike = reviewLikeRepository.findByReviewCodeAndMemberCode(review.getReviewCode(), memberCode).orElse(null);
                    if (reviewLike != null)
                        reviewDTO.setIsLike(true);

                    return reviewDTO;
                }).collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public List<ReviewDTO> toReviewDTOList(
            List<Review> reviewList
    ) {
        return reviewList.stream()
                .map(this::toBaseReviewDTO)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private ReviewDTO toBaseReviewDTO(Review review) {
        ReviewDTO reviewDTO = modelMapper.map(review, ReviewDTO.class);

        // 이미지 경로 추가
        reviewDTO.setImagePaths(findImagePathsByReviewCode(review.getReviewCode()));
        reviewDTO.setLikes(findReviewLikesByReviewCode(review.getReviewCode()));
        reviewDTO.setMemberNickname(review.getMember().getNickname());
        reviewDTO.setReviewCode(review.getReviewCode());
        reviewDTO.setMemberCode(review.getMember().getMemberCode());
        reviewDTO.setMemberImage(review.getMember().getImage());
        List<String> genres = findMemberTendencyGenre(review.getMember().getMemberCode());
        if(!genres.isEmpty())
            reviewDTO.setGenres(genres);
        reviewDTO.setThemeCode(review.getTheme().getThemeCode());
        reviewDTO.setThemeImage(review.getTheme().getPosterImage());
        reviewDTO.setThemeName(review.getTheme().getName());
        reviewDTO.setIsLike(false);
        return reviewDTO;
    }

}
