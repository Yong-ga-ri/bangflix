package com.swcamp9th.bangflixbackend.domain.communityPost.service;

import com.swcamp9th.bangflixbackend.domain.communityPost.dto.CommunityLikeCountDTO;
import com.swcamp9th.bangflixbackend.domain.communityPost.dto.CommunityLikeCreateDTO;
import com.swcamp9th.bangflixbackend.domain.communityPost.entity.CommunityLike;
import com.swcamp9th.bangflixbackend.domain.communityPost.entity.CommunityPost;
import com.swcamp9th.bangflixbackend.domain.communityPost.repository.CommunityLikeRepository;
import com.swcamp9th.bangflixbackend.domain.communityPost.repository.CommunityPostRepository;
import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import com.swcamp9th.bangflixbackend.domain.user.repository.UserRepository;
import com.swcamp9th.bangflixbackend.shared.exception.InvalidUserException;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service("communityLikeService")
public class CommunityLikeServiceImpl implements CommunityLikeService {

    private final ModelMapper modelMapper;
    private final CommunityLikeRepository communityLikeRepository;
    private final UserRepository userRepository;
    private final CommunityPostRepository communityPostRepository;

    @Autowired
    public CommunityLikeServiceImpl(ModelMapper modelMapper,
                                    CommunityLikeRepository communityLikeRepository,
                                    UserRepository userRepository,
                                    CommunityPostRepository communityPostRepository) {
        this.modelMapper = modelMapper;
        this.communityLikeRepository = communityLikeRepository;
        this.userRepository = userRepository;
        this.communityPostRepository = communityPostRepository;
    }

    @Transactional
    @Override
    public void addLike(String loginId, CommunityLikeCreateDTO newLike) {
        CommunityLike addedLike = modelMapper.map(newLike, CommunityLike.class);

        // 회원이 아니라면 예외 발생
        Member likeMember = userRepository.findById(loginId).orElseThrow(
                () -> new InvalidUserException("회원가입이 필요합니다."));

        CommunityPost likePost = communityPostRepository.findById(newLike.getCommunityPostCode())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        addedLike.setMemberCode(likeMember.getMemberCode());
        addedLike.setCommunityPostCode(likePost.getCommunityPostCode());
        addedLike.setCreatedAt(LocalDateTime.now());

        // 이미 좋아요가 존재하는지 체크 후 존재하면 좋아요 취소(비활성화)
        if (communityLikeRepository.existsByMemberCodeAndCommunityPostCodeAndActiveTrue(
                                                                            likeMember.getMemberCode(),
                                                                            likePost.getCommunityPostCode())) {
            addedLike.setActive(false);
        } else {
            addedLike.setActive(true);
        }

        communityLikeRepository.save(addedLike);
    }

    @Transactional(readOnly = true)
    @Override
    public CommunityLikeCountDTO countLike(int communityPostCode) {
        CommunityPost likePost = communityPostRepository.findById(communityPostCode)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        CommunityLikeCountDTO count = new CommunityLikeCountDTO();
        Long likeCount = 0L;
        List<CommunityLike> likes = communityLikeRepository
                                    .findByCommunityPostCodeAndActiveTrue(communityPostCode);

        for (int i = 0; i < likes.size(); i++) {
            likeCount++;
        }

        count.setCommunityPostCode(likePost.getCommunityPostCode());
        count.setLikeCount(likeCount);

        return count;
    }
}
