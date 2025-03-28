package com.swcamp9th.bangflixbackend.domain.communitypost.service;

import com.swcamp9th.bangflixbackend.domain.communitypost.dto.CommunityPostCreateDTO;
import com.swcamp9th.bangflixbackend.domain.communitypost.dto.CommunityPostDTO;
import com.swcamp9th.bangflixbackend.domain.communitypost.dto.CommunityPostUpdateDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CommunityPostService {

    void createPost(String loginId, CommunityPostCreateDTO newPost, List<MultipartFile> images);

    void updatePost(String loginId, int communityPostCode,
                    CommunityPostUpdateDTO modifiedPost, List<MultipartFile> images);

    void deletePost(String loginId, int communityPostCode);

    CommunityPostDTO findPostByCode(String loginId, int communityPostCode);

    List<CommunityPostDTO> getAllPosts(String loginId);

    List<CommunityPostDTO> getMyPosts(String loginId);
}
