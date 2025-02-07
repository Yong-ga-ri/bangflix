package com.swcamp9th.bangflixbackend.domain.noticepost.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Table(name = "notice_file")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class NoticeFile {

    @Id
    @Column(name = "notice_file_code")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer noticeFileCode;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "active", nullable = false)
    @ColumnDefault("TRUE")
    private Boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_post_code", nullable = false)
    private NoticePost noticePost;
}
