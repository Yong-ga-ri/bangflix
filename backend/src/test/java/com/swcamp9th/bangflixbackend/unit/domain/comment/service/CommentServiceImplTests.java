package com.swcamp9th.bangflixbackend.unit.domain.comment.service;

import com.swcamp9th.bangflixbackend.domain.comment.dto.CommentCountDTO;
import com.swcamp9th.bangflixbackend.domain.comment.dto.CommentCreateDTO;
import com.swcamp9th.bangflixbackend.domain.comment.dto.CommentDTO;
import com.swcamp9th.bangflixbackend.domain.comment.dto.CommentUpdateDTO;
import com.swcamp9th.bangflixbackend.domain.comment.entity.Comment;
import com.swcamp9th.bangflixbackend.domain.comment.repository.CommentRepository;
import com.swcamp9th.bangflixbackend.domain.comment.service.CommentServiceImpl;
import com.swcamp9th.bangflixbackend.domain.communitypost.entity.CommunityPost;
import com.swcamp9th.bangflixbackend.domain.communitypost.repository.CommunityPostRepository;
import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import com.swcamp9th.bangflixbackend.domain.user.repository.UserRepository;
import com.swcamp9th.bangflixbackend.shared.error.exception.InvalidUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceImplTests {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommunityPostRepository communityPostRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Member mockMember;
    private CommunityPost mockPost;
    private Comment mockComment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockMember = new Member();
        mockMember.setId("user123");
        mockMember.setMemberCode(1);
        mockMember.setNickname("Test User");

        mockPost = new CommunityPost();
        mockPost.setCommunityPostCode(100);

        mockComment = new Comment();
        mockComment.setCommentCode(1);
        mockComment.setContent("Test comment");
        mockComment.setCreatedAt(LocalDateTime.now());
        mockComment.setActive(true);
        mockComment.setMember(mockMember);
        mockComment.setCommunityPost(mockPost);
    }

    @DisplayName("댓글 생성 테스트")
    @Test
    void createComment_ShouldSaveComment_WhenValidRequest() {
        // Given
        CommentCreateDTO request = new CommentCreateDTO();
        request.setContent("New Comment");

        when(userRepository.findById("user123")).thenReturn(Optional.of(mockMember));
        when(communityPostRepository.findById(100)).thenReturn(Optional.of(mockPost));
        when(commentRepository.save(any(Comment.class))).thenReturn(mockComment);

        // When
        commentService.createComment("user123", 100, request);

        // Then
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @DisplayName("댓글 생성 실패 - 존재하지 않는 유저")
    @Test
    void createComment_ShouldThrowException_WhenUserNotFound() {
        // Given
        CommentCreateDTO request = new CommentCreateDTO();
        request.setContent("New Comment");

        when(userRepository.findById("user123")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> commentService.createComment("user123", 100, request))
                .isInstanceOf(InvalidUserException.class)
                .hasMessage("유효하지 않은 사용자입니다.");
    }

    @DisplayName("댓글 업데이트 테스트")
    @Test
    void updateComment_ShouldUpdateComment_WhenValidRequest() {
        // Given
        CommentUpdateDTO updateDTO = new CommentUpdateDTO();
        updateDTO.setContent("Updated Content");

        when(commentRepository.findById(1)).thenReturn(Optional.of(mockComment));
        when(communityPostRepository.findById(100)).thenReturn(Optional.of(mockPost));
        when(userRepository.findById("user123")).thenReturn(Optional.of(mockMember));

        // When
        commentService.updateComment("user123", 100, 1, updateDTO);

        // Then
        assertThat(mockComment.getContent()).isEqualTo("Updated Content");
        verify(commentRepository, times(1)).save(mockComment);
    }

    @DisplayName("댓글 삭제 테스트")
    @Test
    void deleteComment_ShouldDeactivateComment_WhenValidRequest() {
        // Given
        when(commentRepository.findById(1)).thenReturn(Optional.of(mockComment));
        when(userRepository.findById("user123")).thenReturn(Optional.of(mockMember));

        // When
        commentService.deleteComment("user123", 100, 1);

        // Then
        assertThat(mockComment.getActive()).isFalse();
        verify(commentRepository, times(1)).save(mockComment);
    }

    @DisplayName("특정 게시글의 모든 댓글 조회")
    @Test
    void getAllCommentsOfPost_ShouldReturnComments_WhenPostExists() {
        // Given
        when(communityPostRepository.findById(100)).thenReturn(Optional.of(mockPost));
        when(commentRepository.findByCommunityPostAndActiveTrue(mockPost)).thenReturn(List.of(mockComment));

        CommentDTO mockCommentDTO = new CommentDTO();
        mockCommentDTO.setContent("Test comment");
        mockCommentDTO.setNickname("Test User");

        when(modelMapper.map(mockComment, CommentDTO.class)).thenReturn(mockCommentDTO);

        // When
        List<CommentDTO> comments = commentService.getAllCommentsOfPost(100);

        // Then
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getContent()).isEqualTo("Test comment");
    }

    @DisplayName("특정 게시글의 댓글 개수 조회")
    @Test
    void getCommentCount_ShouldReturnCommentCount_WhenPostExists() {
        // Given
        when(communityPostRepository.findById(100)).thenReturn(Optional.of(mockPost));
        when(commentRepository.findByCommunityPostAndActiveTrue(mockPost)).thenReturn(List.of(mockComment));

        // When
        CommentCountDTO countDTO = commentService.getCommentCount(100);

        // Then
        assertThat(countDTO.getCommentCount()).isEqualTo(1);
    }

    @DisplayName("특정 사용자의 댓글 조회")
    @Test
    void getCommentsById_ShouldReturnUserComments_WhenUserExists() {
        // Given
        when(userRepository.findById("user123")).thenReturn(Optional.of(mockMember));
        when(commentRepository.findByMemberAndActiveTrue(mockMember)).thenReturn(List.of(mockComment));

        CommentDTO mockCommentDTO = new CommentDTO();
        mockCommentDTO.setContent("Test comment");

        when(modelMapper.map(mockComment, CommentDTO.class)).thenReturn(mockCommentDTO);

        // When
        List<CommentDTO> comments = commentService.getCommentsById("user123");

        // Then
        assertThat(comments).hasSize(1);
        assertThat(comments.get(0).getContent()).isEqualTo("Test comment");
    }
}
