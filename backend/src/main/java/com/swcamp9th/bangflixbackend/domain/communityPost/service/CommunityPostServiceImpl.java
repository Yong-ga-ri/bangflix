package com.swcamp9th.bangflixbackend.domain.communitypost.service;

import com.swcamp9th.bangflixbackend.domain.communitypost.dto.CommunityPostCreateDTO;
import com.swcamp9th.bangflixbackend.domain.communitypost.dto.CommunityPostDTO;
import com.swcamp9th.bangflixbackend.domain.communitypost.dto.CommunityPostUpdateDTO;
import com.swcamp9th.bangflixbackend.domain.communitypost.entity.CommunityFile;
import com.swcamp9th.bangflixbackend.domain.communitypost.exception.CommunityPostNotFoundException;
import com.swcamp9th.bangflixbackend.domain.communitypost.repository.CommunityFileRepository;
import com.swcamp9th.bangflixbackend.domain.communitypost.repository.CommunityLikeRepository;
import com.swcamp9th.bangflixbackend.domain.communitypost.repository.CommunityPostRepository;
import com.swcamp9th.bangflixbackend.domain.communitypost.entity.CommunityPost;
import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import com.swcamp9th.bangflixbackend.domain.user.exception.MemberNotFoundException;
import com.swcamp9th.bangflixbackend.domain.user.repository.UserRepository;
import com.swcamp9th.bangflixbackend.shared.error.exception.InvalidUserException;
import com.swcamp9th.bangflixbackend.shared.error.exception.FileUploadException;
import com.swcamp9th.bangflixbackend.shared.error.exception.LoginRequiredException;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CommunityPostServiceImpl implements CommunityPostService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;

    private final CommunityFileRepository communityFileRepository;
    private final CommunityLikeRepository communityLikeRepository;

    private final CommunityPostRepository communityPostRepository;

    @Autowired
    public CommunityPostServiceImpl(
            ModelMapper modelMapper,
            UserRepository userRepository,
            CommunityFileRepository communityFileRepository,
            CommunityLikeRepository communityLikeRepository,
            CommunityPostRepository communityPostRepository
    ) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.communityFileRepository = communityFileRepository;
        this.communityLikeRepository = communityLikeRepository;
        this.communityPostRepository = communityPostRepository;
    }

    @Transactional
    @Override
    public void createPost(
            String loginId,
            CommunityPostCreateDTO newPost,
            List<MultipartFile> images
    ) {
        CommunityPost createdPost = modelMapper.map(newPost, CommunityPost.class);

        // 회원이 아니라면 예외 발생
        Member member = userRepository.findById(loginId)
                .orElseThrow(MemberNotFoundException::new);

        createdPost.setTitle(newPost.getTitle());
        createdPost.setContent(newPost.getContent());
        createdPost.setCreatedAt(LocalDateTime.now());
        createdPost.setActive(true);
        createdPost.setMember(member);

        // 게시글 저장
        communityPostRepository.save(createdPost);

        // 게시글 첨부파일 있으면 저장
        if (images != null) {
            List<CommunityFile> addedImages = null;
            addedImages = saveFiles(images, createdPost);
            createdPost.setCommunityFiles(addedImages);
        }
    }

    private List<CommunityFile> saveFiles(
            List<MultipartFile> images,
            CommunityPost savedPost
    ) {
        List<CommunityFile> communityFiles = new ArrayList<>();

        for (MultipartFile file : images) {
            String fileName = file.getOriginalFilename();

            // 파일이름만 남김
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
            // UUID 생성
            String uuid = UUID.randomUUID().toString();
            // 저장 경로
            String filePath = "src/main/resources/static/uploadFiles/communityFiles/" + uuid + fileName;
            Path path = Paths.get(filePath);
            // DB 저장명
            String dbUrl = "/uploadFiles/communityFiles/" + uuid + fileName;

            //저장
            try {
                Files.createDirectories(path.getParent());
                Files.write(path, file.getBytes());
            } catch (IOException e) {
                throw new FileUploadException();
            }

            CommunityFile addedImages = communityFileRepository.save(
                    CommunityFile.builder()
                    .url(dbUrl)
                    .createdAt(LocalDateTime.now())
                    .active(true)
                    .communityPost(savedPost)
                    .build()
            );

            communityFiles.add(addedImages);
        }

        return communityFiles;
    }

    @Transactional
    @Override
    public void updatePost(
            String loginId,
            int communityPostCode,
            CommunityPostUpdateDTO modifiedPost,
            List<MultipartFile> images
    ) {
        CommunityPost foundPost = communityPostRepository.findById(communityPostCode)
                                    .orElseThrow(CommunityPostNotFoundException::new);

        // 회원이 아니라면 예외 발생
        Member author = userRepository.findById(loginId)
                .orElseThrow(LoginRequiredException::new);

        // 게시글 작성자가 아니라면 예외 발생
        if (!foundPost.getMember().getMemberCode().equals(author.getMemberCode())) {
            throw new InvalidUserException();
        }

        foundPost.setTitle(modifiedPost.getTitle());
        foundPost.setContent(modifiedPost.getContent());

        // 수정된 게시글 저장
        communityPostRepository.save(foundPost);
    }

    @Transactional
    @Override
    public void deletePost(String loginId, int communityPostCode) {
        CommunityPost foundPost = communityPostRepository.findById(communityPostCode)
                                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        // 회원이 아니라면 예외 발생
        Member author = userRepository.findById(loginId)
                .orElseThrow(LoginRequiredException::new);

        // 게시글 작성자가 아니라면 예외 발생
        if (!foundPost.getMember().getMemberCode().equals(author.getMemberCode())) {
            throw new InvalidUserException();
        }

        foundPost.setActive(false);

        communityPostRepository.save(foundPost);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommunityPostDTO> getAllPosts(String loginId) {
        List<CommunityPost> allPosts = communityPostRepository
                .findByActiveTrue(Sort.by("createdAt").descending());

        Member loginMember = userRepository.findById(loginId)
                .orElseThrow(MemberNotFoundException::new);

        List<CommunityPostDTO> postList = allPosts.stream()
                .map(communityPost -> {
                    CommunityPostDTO postDTO = modelMapper.map(communityPost, CommunityPostDTO.class);

                    List<CommunityFile> images = communityFileRepository.findByCommunityPost(communityPost);
                    List<String> urls = images.stream().map(CommunityFile::getUrl).toList();
                    boolean isLike = communityLikeRepository.existsByMemberCodeAndCommunityPostCodeAndActiveTrue(
                                    loginMember.getMemberCode(),
                                    communityPost.getCommunityPostCode()
                            );

                    postDTO.setNickname(communityPost.getMember().getNickname());
                    postDTO.setProfile(communityPost.getMember().getImage());
                    postDTO.setImageUrls(urls);
                    postDTO.setIsLike(isLike);
                    return postDTO;
                }).toList();

        return postList;
    }

    @Transactional(readOnly = true)
    @Override
    public CommunityPostDTO findPostByCode(String loginId, int communityPostCode) {
        CommunityPost post = communityPostRepository.findById(communityPostCode)
                .orElseThrow(CommunityPostNotFoundException::new);

        Member loginMember = userRepository.findById(loginId)
                .orElseThrow(MemberNotFoundException::new);

        CommunityPostDTO selectedPost = modelMapper.map(post, CommunityPostDTO.class);
        selectedPost.setNickname(post.getMember().getNickname());
        selectedPost.setProfile(post.getMember().getImage());

        List<CommunityFile> images = communityFileRepository.findByCommunityPost(post);
        List<String> urls = images.stream()
                .map(CommunityFile::getUrl)
                .toList();
        boolean isLike = communityLikeRepository
                .existsByMemberCodeAndCommunityPostCodeAndActiveTrue(
                        loginMember.getMemberCode(),
                        post.getCommunityPostCode()
                );

        selectedPost.setImageUrls(urls);
        selectedPost.setIsLike(isLike);

        return selectedPost;
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommunityPostDTO> getMyPosts(String loginId) {
        Member loginMember = userRepository.findById(loginId)
                .orElseThrow(MemberNotFoundException::new);

        List<CommunityPost> myPosts = communityPostRepository.findByMemberAndActiveTrueOrderByCreatedAtDesc(loginMember);

        List<CommunityPostDTO> myPostList = myPosts.stream()
                .map(communityPost -> {
                    CommunityPostDTO postDTO = modelMapper.map(communityPost, CommunityPostDTO.class);

                    List<CommunityFile> images = communityFileRepository.findByCommunityPost(communityPost);
                    List<String> urls = images.stream().map(CommunityFile::getUrl).toList();
                    boolean isLike = communityLikeRepository.existsByMemberCodeAndCommunityPostCodeAndActiveTrue(
                                    loginMember.getMemberCode(),
                            communityPost.getCommunityPostCode()
                    );

                    postDTO.setNickname(communityPost.getMember().getNickname());
                    postDTO.setProfile(communityPost.getMember().getImage());
                    postDTO.setImageUrls(urls);
                    postDTO.setIsLike(isLike);
                    return postDTO;
                }).toList();

        return myPostList;
    }
}
