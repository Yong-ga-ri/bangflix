package com.swcamp9th.bangflixbackend.domain.comment.entity;

import com.swcamp9th.bangflixbackend.domain.communitypost.entity.CommunityPost;
import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Table(name = "comment")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Comment {

    @Id
    @Column(name = "comment_code")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer commentCode;

    @Column(name = "active", nullable = false)
    @ColumnDefault("TRUE")
    private Boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_code", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_post_code", nullable = false)
    private CommunityPost communityPost;
}
