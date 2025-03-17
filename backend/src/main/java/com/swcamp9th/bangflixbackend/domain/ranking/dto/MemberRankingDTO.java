package com.swcamp9th.bangflixbackend.domain.ranking.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberRankingDTO {

    private Integer memberCode;
    private String id;
    private String nickname;
    private String email;
    private Boolean isAdmin;
    private String image;
    private Integer point;
    private Date createdAt;
    private Boolean active;
}
