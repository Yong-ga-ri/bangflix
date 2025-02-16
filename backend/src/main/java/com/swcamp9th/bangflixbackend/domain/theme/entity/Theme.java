package com.swcamp9th.bangflixbackend.domain.theme.entity;

import com.swcamp9th.bangflixbackend.domain.store.entity.Store;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.*;

@Entity
@Table(name = "theme")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theme_code")
    private Integer themeCode;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "level", nullable = false)
    private Integer level;

    @Column(name = "timelimit", nullable = false)
    private Integer timeLimit;

    @Column(name = "story", nullable = false, length = 1024)
    private String story;

    @Column(name = "price")
    private Integer price;

    @Column(name = "poster_image", nullable = false, length = 255)
    private String posterImage;

    @Column(name = "headcount", length = 255)
    private String headcount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_code", nullable = false)
    private Store store;

}

