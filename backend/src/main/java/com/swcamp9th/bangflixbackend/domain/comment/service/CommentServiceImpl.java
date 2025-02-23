package com.swcamp9th.bangflixbackend.domain.comment.service;

import com.swcamp9th.bangflixbackend.domain.comment.exception.CommentNotFoundException;
import com.swcamp9th.bangflixbackend.domain.comment.dto.CommentCountDTO;
import com.swcamp9th.bangflixbackend.domain.comment.dto.CommentDTO;
import com.swcamp9th.bangflixbackend.domain.comment.entity.Comment;
import com.swcamp9th.bangflixbackend.domain.comment.repository.CommentRepository;
import com.swcamp9th.bangflixbackend.domain.comment.dto.CommentCreateDTO;
import com.swcamp9th.bangflixbackend.domain.comment.dto.CommentUpdateDTO;
import com.swcamp9th.bangflixbackend.domain.communitypost.entity.CommunityPost;
import com.swcamp9th.bangflixbackend.domain.communitypost.exception.CommunityPostNotFoundException;
import com.swcamp9th.bangflixbackend.domain.communitypost.repository.CommunityPostRepository;
import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import com.swcamp9th.bangflixbackend.domain.user.exception.MemberNotFoundException;
import com.swcamp9th.bangflixbackend.domain.user.repository.UserRepository;
import com.swcamp9th.bangflixbackend.shared.error.exception.InvalidUserException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final ModelMapper modelMapper;

    private final CommentRepository commentRepository;
    private final CommunityPostRepository communityPostRepository;

    private final UserRepository userRepository;

    @Autowired
    public CommentServiceImpl(
            ModelMapper modelMapper,
            CommunityPostRepository communityPostRepository,
            CommentRepository commentRepository,
            UserRepository userRepository
    ) {
        this.modelMapper = modelMapper;
        this.communityPostRepository = communityPostRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public void createComment(
            String loginId,
            Integer communityPostCode,
            CommentCreateDTO newComment
    ) {
        Comment comment = new Comment();

        // 회원이 아니면 예외 발생
        Member author = userRepository.findById(loginId)
                .orElseThrow(InvalidUserException::new);

        CommunityPost post = communityPostRepository.findById(communityPostCode)
                .orElseThrow(CommunityPostNotFoundException::new);

        comment.setActive(true);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setContent(newComment.getContent());
        comment.setMember(author);
        comment.setCommunityPost(post);

        // 댓글 저장
        commentRepository.save(comment);
    }

    @Transactional
    @Override
    public void updateComment(
            String loginId,
            Integer communityPostCode,
            Integer commentCode,
            CommentUpdateDTO modifiedComment
    ) {
        Comment originalComment = commentRepository.findById(commentCode)
                .orElseThrow(CommentNotFoundException::new);

        CommunityPost post = communityPostRepository.findById(communityPostCode)
                .orElseThrow(CommunityPostNotFoundException::new);

        // 회원이 아니라면 예외 발생
        Member author = userRepository.findById(loginId)
                .orElseThrow(InvalidUserException::new);

        // 게시글 작성자가 아니라면 예외 발생
        if (!originalComment.getMember().getMemberCode().equals(author.getMemberCode())) {
            throw new InvalidUserException();
        }

        originalComment.setContent(modifiedComment.getContent());
        originalComment.setMember(author);
        originalComment.setCommunityPost(post);

        // 수정된 댓글 저장
        commentRepository.save(originalComment);
    }

    @Transactional
    @Override
    public void deleteComment(
            String loginId,
            Integer communityPostCode,
            Integer commentCode
    ) {
        Comment foundComment = commentRepository.findById(commentCode)
                .orElseThrow(CommentNotFoundException::new);

        // 회원이 아니라면 예외 발생
        Member author = userRepository.findById(loginId).orElseThrow(
                InvalidUserException::new);

        // 게시글 작성자가 아니라면 예외 발생
        if (!foundComment.getMember().getMemberCode().equals(author.getMemberCode())) {
            throw new InvalidUserException();
        }

        foundComment.setActive(false);
        commentRepository.save(foundComment);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDTO> getAllCommentsOfPost(Integer communityPostCode) {
        CommunityPost foundPost = communityPostRepository.findById(communityPostCode)
                .orElseThrow(CommunityPostNotFoundException::new);

        List<Comment> commentList = commentRepository.findByCommunityPostAndActiveTrue(foundPost);

        List<CommentDTO> allComments = commentList.stream()
                .map(comment -> {
                    CommentDTO commentDTO = modelMapper.map(comment, CommentDTO.class);
                    commentDTO.setNickname(comment.getMember().getNickname());
                    commentDTO.setCommunityPostCode(comment.getCommunityPost().getCommunityPostCode());
                    commentDTO.setProfile(comment.getMember().getImage());

                    return commentDTO;
                }).toList();

        return allComments;
    }

    @Transactional(readOnly = true)
    @Override
    public CommentCountDTO getCommentCount(Integer communityPostCode) {
        CommunityPost foundPost = communityPostRepository.findById(communityPostCode)
                .orElseThrow(CommunityPostNotFoundException::new);

        List<Comment> comments = commentRepository.findByCommunityPostAndActiveTrue(foundPost);
        Long commentCount = 0L;
        for (int i = 0; i < comments.size(); i++) {
            commentCount++;
        }

        CommentCountDTO count = new CommentCountDTO();
        count.setCommunityPostCode(communityPostCode);
        count.setCommentCount(commentCount);

        return count;
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDTO> getCommentsById(String loginId) {

        Member member = userRepository.findById(loginId)
                .orElseThrow(InvalidUserException::new);

        return commentRepository.findByMemberAndActiveTrue(member).stream()
                .map(comment ->
                        modelMapper.map(comment, CommentDTO.class)
                ).toList();
    }


}
