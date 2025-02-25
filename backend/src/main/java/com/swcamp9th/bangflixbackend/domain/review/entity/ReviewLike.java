package com.swcamp9th.bangflixbackend.domain.review.entity;

import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_like")
@IdClass(ReviewLikeId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewLike {

    @Id
    @Column(name = "member_code", nullable = false)
    private int memberCode;

    @Id
    @Column(name = "review_code", nullable = false)
    private int reviewCode;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "active", nullable = false)
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_code", insertable = false, updatable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_code", insertable = false, updatable = false)
    private Review review;
}
