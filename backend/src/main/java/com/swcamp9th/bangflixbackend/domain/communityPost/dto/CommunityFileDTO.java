package com.swcamp9th.bangflixbackend.domain.communitypost.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CommunityFileDTO {

    private Integer communityFileCode;
    private String url;
    private String createdAt;
    private Boolean active;
    private Integer communityPostCode;
}
