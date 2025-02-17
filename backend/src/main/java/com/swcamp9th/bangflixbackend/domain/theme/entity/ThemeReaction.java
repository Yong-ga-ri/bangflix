package com.swcamp9th.bangflixbackend.domain.theme.entity;

import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "theme_reaction")
@IdClass(ThemeReactionId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ThemeReaction {

    @Id
    @Column(name = "member_code", nullable = false)
    private Integer memberCode;

    @Id
    @Column(name = "theme_code", nullable = false)
    private Integer themeCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReactionType reaction;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_code", insertable = false, updatable = false)
    private Theme theme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_code", insertable = false, updatable = false)
    private Member member;


}
