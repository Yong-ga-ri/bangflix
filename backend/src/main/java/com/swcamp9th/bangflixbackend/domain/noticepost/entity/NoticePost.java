package com.swcamp9th.bangflixbackend.domain.noticepost.entity;

import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "notice_post")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class NoticePost {

    @Id
    @Column(name = "notice_post_code")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer noticePostCode;

    @Column(name = "active", nullable = false)
    @ColumnDefault("TRUE")
    private Boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_code", nullable = false)
    private Member member;

    // 첨부파일과의 관계 설정
    @OneToMany(mappedBy = "noticePost", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<NoticeFile> noticeFiles = new ArrayList<>();
}
