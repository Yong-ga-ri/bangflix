package com.swcamp9th.bangflixbackend.domain.communitypost.service;

import com.swcamp9th.bangflixbackend.domain.communitypost.dto.CommunityLikeCountDTO;
import com.swcamp9th.bangflixbackend.domain.communitypost.dto.CommunityLikeCreateDTO;

public interface CommunityLikeService {
    void addLike(String loginId, CommunityLikeCreateDTO newLike);

    CommunityLikeCountDTO countLike(int communityPostCode);
}
