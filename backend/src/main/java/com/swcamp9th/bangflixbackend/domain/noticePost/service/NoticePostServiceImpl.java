package com.swcamp9th.bangflixbackend.domain.noticePost.service;

import com.swcamp9th.bangflixbackend.shared.response.NoticePageResponse;
import com.swcamp9th.bangflixbackend.domain.noticePost.dto.NoticePostCreateDTO;
import com.swcamp9th.bangflixbackend.domain.noticePost.dto.NoticePostDTO;
import com.swcamp9th.bangflixbackend.domain.noticePost.dto.NoticePostUpdateDTO;
import com.swcamp9th.bangflixbackend.domain.noticePost.entity.NoticeFile;
import com.swcamp9th.bangflixbackend.domain.noticePost.entity.NoticePost;
import com.swcamp9th.bangflixbackend.domain.noticePost.repository.NoticeFileRepository;
import com.swcamp9th.bangflixbackend.domain.noticePost.repository.NoticePostRepository;
import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import com.swcamp9th.bangflixbackend.domain.user.repository.UserRepository;
import com.swcamp9th.bangflixbackend.shared.exception.InvalidUserException;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

@Service("noticePostService")
public class NoticePostServiceImpl implements NoticePostService {

    private final ModelMapper modelMapper;
    private final NoticePostRepository noticePostRepository;
    private final NoticeFileRepository noticeFileRepository;
    private final UserRepository userRepository;

    @Autowired
    public NoticePostServiceImpl(ModelMapper modelMapper,
                                 NoticePostRepository noticePostRepository,
                                 NoticeFileRepository noticeFileRepository,
                                 UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.noticePostRepository = noticePostRepository;
        this.noticeFileRepository = noticeFileRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public void createNoticePost(NoticePostCreateDTO newNotice, List<MultipartFile> images, String userId) throws IOException {

        // 관리자 회원이 아니라면 예외 발생
        Member admin = userRepository.findByIdAndIsAdminTrue(userId)
                .orElseThrow(() -> new InvalidUserException("관리자 회원만 접근 가능합니다."));

        NoticePost createdNotice = new NoticePost();
        createdNotice.setActive(true);
        createdNotice.setCreatedAt(LocalDateTime.now());
        createdNotice.setTitle(newNotice.getTitle());
        createdNotice.setContent(newNotice.getContent());
        createdNotice.setMember(admin);

        // 게시글 저장
        noticePostRepository.save(createdNotice);

        // 첨부파일 있으면 저장
        if (images != null) {
            List<NoticeFile> addedImages = saveFiles(images, createdNotice);
            createdNotice.setNoticeFiles(addedImages);
        }
    }

    private List<NoticeFile> saveFiles(List<MultipartFile> images, NoticePost createdNotice) throws IOException {
        List<NoticeFile> noticeFiles = new ArrayList<>();

        for (MultipartFile file : images) {
            String fileName = file.getOriginalFilename();

            // 파일이름만 남김
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
            // UUID 생성
            String uuid = UUID.randomUUID().toString();
            // 저장 경로
            String filePath = "src/main/resources/static/uploadFiles/noticeFiles/" + uuid + fileName;
            Path path = Paths.get(filePath);
            // DB 저장명
            String dbUrl = "/uploadFiles/noticeFiles/" + uuid + fileName;

            //저장
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            NoticeFile addedImage = noticeFileRepository.save(NoticeFile.builder()
                    .url(dbUrl)
                    .active(true)
                    .createdAt(LocalDateTime.now())
                    .noticePost(createdNotice)
                    .build()
            );

            noticeFiles.add(addedImage);
        }

        return noticeFiles;
    }

    @Transactional
    @Override
    public void updateNoticePost(int noticePostCode, NoticePostUpdateDTO updatedNotice,
                                 List<MultipartFile> images, String userId) {

        NoticePost foundNotice = noticePostRepository.findById(noticePostCode)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        // 관리자 회원이 아니라면 예외 발생
        Member admin = userRepository.findByIdAndIsAdminTrue(userId)
                .orElseThrow(() -> new InvalidUserException("관리자 회원만 접근 가능합니다."));

        // 게시글 작성자가 아니라면 예외 발생
        if (!foundNotice.getMember().getMemberCode().equals(admin.getMemberCode())) {
            throw new InvalidUserException("게시글 수정 권한이 없습니다.");
        }

        foundNotice.setTitle(updatedNotice.getTitle());
        foundNotice.setContent(updatedNotice.getContent());

        // 수정된 게시글 저장
        noticePostRepository.save(foundNotice);
    }

    @Transactional
    @Override
    public void deleteNoticePost(int noticePostCode, String userId) {
        NoticePost foundNotice = noticePostRepository.findById(noticePostCode)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        // 관리자 회원이 아니라면 예외 발생
        Member admin = userRepository.findByIdAndIsAdminTrue(userId)
                .orElseThrow(() -> new InvalidUserException("관리자 회원만 접근 가능합니다."));

        // 게시글 작성자가 아니라면 예외 발생
        if (!foundNotice.getMember().getMemberCode().equals(admin.getMemberCode())) {
            throw new InvalidUserException("게시글 삭제 권한이 없습니다.");
        }

        foundNotice.setActive(false);

        noticePostRepository.save(foundNotice);
    }

    @Transactional(readOnly = true)
    @Override
    public NoticePageResponse getAllNotices(Pageable pageable) {
        pageable = PageRequest.of(pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() - 1,
                pageable.getPageSize(),
                Sort.by("createdAt").descending());

        Page<NoticePost> noticeList = noticePostRepository.findByActiveTrue(pageable);

        List<NoticePostDTO> noticePosts = noticeList.getContent().stream()
                .map(noticePost -> {
                    NoticePostDTO noticeDTO = modelMapper.map(noticePost, NoticePostDTO.class);

                    List<NoticeFile> images = noticeFileRepository.findByNoticePost(noticePost).stream().toList();
                    List<String> urls = images.stream().map(NoticeFile::getUrl).toList();

                    noticeDTO.setNickname(noticePost.getMember().getNickname());
                    noticeDTO.setImageUrls(urls);
                    return noticeDTO;
                }).toList();

        NoticePageResponse response = new NoticePageResponse();
        response.setNoticePosts(noticePosts);
        response.setCurrentPage(noticeList.getNumber() + 1);    // 페이지 번호가 1부터 시작
        response.setTotalPages(noticeList.getTotalPages());
        response.setTotalElements(noticeList.getTotalElements());

        return response;
    }

    @Transactional(readOnly = true)
    @Override
    public NoticePostDTO findNoticeByCode(int noticePostCode) {
        NoticePost foundNotice = noticePostRepository.findById(noticePostCode)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        NoticePostDTO selectedNotice = modelMapper.map(foundNotice, NoticePostDTO.class);
        selectedNotice.setNickname(foundNotice.getMember().getNickname());

        List<NoticeFile> images = noticeFileRepository.findByNoticePost(foundNotice).stream().toList();
        List<String> urls = images.stream().map(NoticeFile::getUrl).toList();
        selectedNotice.setImageUrls(urls);

        return selectedNotice;
    }
}
