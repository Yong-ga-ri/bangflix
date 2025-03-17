package com.swcamp9th.bangflixbackend.domain.communitypost.entity;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class CommunityLikeId implements Serializable {      // 복합키 클래스 정의
    private Integer memberCode;
    private Integer communityPostCode;
}
