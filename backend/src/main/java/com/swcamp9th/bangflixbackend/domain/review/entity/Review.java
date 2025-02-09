package com.swcamp9th.bangflixbackend.domain.review.entity;

import com.swcamp9th.bangflixbackend.domain.review.enums.Activity;
import com.swcamp9th.bangflixbackend.domain.review.enums.Composition;
import com.swcamp9th.bangflixbackend.domain.review.enums.HorrorLevel;
import com.swcamp9th.bangflixbackend.domain.review.enums.Interior;
import com.swcamp9th.bangflixbackend.domain.review.enums.Level;
import com.swcamp9th.bangflixbackend.domain.review.enums.Probability;
import com.swcamp9th.bangflixbackend.domain.theme.entity.Theme;
import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_code", nullable = false)
    private Integer reviewCode;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "headcount", nullable = false)
    private Integer headcount;

    @Column(name = "taken_time")
    private Integer takenTime; // 분 단위로 기록

    @Enumerated(EnumType.STRING)
    @Column(name = "composition", nullable = false)
    private Composition composition;

    @Enumerated(EnumType.STRING)
    @Column(name = "level", nullable = false)
    private Level level;

    @Enumerated(EnumType.STRING)
    @Column(name = "horror_level", nullable = false)
    private HorrorLevel horrorLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity", nullable = false)
    private Activity activity;

    @Column(name = "total_score", nullable = false)
    private Integer totalScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "interior", nullable = false)
    private Interior interior;

    @Enumerated(EnumType.STRING)
    @Column(name = "probability")
    private Probability probability;

    @Column(name = "content", length = 1024, nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_code", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_code", nullable = false)
    private Theme theme;

}

