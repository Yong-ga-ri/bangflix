package com.swcamp9th.bangflixbackend.domain.comment.repository;

import com.swcamp9th.bangflixbackend.domain.comment.entity.Comment;
import com.swcamp9th.bangflixbackend.domain.communitypost.entity.CommunityPost;
import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findByCommunityPostAndActiveTrue(CommunityPost foundPost);

    List<Object> findByMemberAndActiveTrue(Member member);
}
