package com.swcamp9th.bangflixbackend.domain.communitypost.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Table(name = "community_like")
@IdClass(CommunityLikeId.class)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class CommunityLike {

    @Id
    @Column(name = "member_code")
    private Integer memberCode;

    @Id
    @Column(name = "community_post_code")
    private Integer communityPostCode;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "active", nullable = false)
    @ColumnDefault("TRUE")
    private Boolean active;
}
