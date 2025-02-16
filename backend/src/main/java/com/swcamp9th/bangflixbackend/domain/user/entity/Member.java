package com.swcamp9th.bangflixbackend.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_code")
    private Integer memberCode;

    @Column(name = "id", nullable = false, length = 255)
    private String id;

    @Column(name = "password", nullable = false, length = 1024)
    private String password;

    @Column(name = "nickname", nullable = false, length = 255)
    private String nickname;

    @Column(name = "email", nullable = false, length = 512)
    private String email;

    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin;

    @Column(name = "image", length = 1024)
    private String image;

    @Column(name = "point", nullable = false)
    private Integer point = 0;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Builder
    public Member(String id, String password, String nickname, String email, Boolean isAdmin, String image) {
        this.id = id;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.isAdmin = isAdmin;
        this.image = image;
        point = 0;
        createdAt = new Date();
        active = true;
    }

    public MemberRoleEnum getRole() {
        return isAdmin ? MemberRoleEnum.ADMIN : MemberRoleEnum.USER;
    }
}
