package com.swcamp9th.bangflixbackend.domain.communitypost.repository;

import com.swcamp9th.bangflixbackend.domain.communitypost.entity.CommunityPost;
import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("communityPostRepository")
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Integer> {

    Page<CommunityPost> findByActiveTrue(Pageable pageable);

    List<CommunityPost> findByActiveTrue(Sort createdAt);

    List<CommunityPost> findByMemberAndActiveTrueOrderByCreatedAtDesc(Member author);
}
