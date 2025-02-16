package com.swcamp9th.bangflixbackend.domain.theme.service;

import com.swcamp9th.bangflixbackend.domain.store.entity.Store;
import com.swcamp9th.bangflixbackend.domain.store.repository.StoreRepository;
import com.swcamp9th.bangflixbackend.domain.theme.dto.FindThemeByReactionDTO;
import com.swcamp9th.bangflixbackend.domain.theme.dto.ThemeReactionDTO;
import com.swcamp9th.bangflixbackend.domain.theme.dto.GenreDTO;
import com.swcamp9th.bangflixbackend.domain.theme.dto.ThemeDTO;
import com.swcamp9th.bangflixbackend.domain.theme.entity.Genre;
import com.swcamp9th.bangflixbackend.domain.theme.entity.ReactionType;
import com.swcamp9th.bangflixbackend.domain.theme.entity.Theme;
import com.swcamp9th.bangflixbackend.domain.theme.entity.ThemeReaction;
import com.swcamp9th.bangflixbackend.domain.theme.repository.GenreRepository;
import com.swcamp9th.bangflixbackend.domain.theme.repository.ThemeReactionRepository;
import com.swcamp9th.bangflixbackend.domain.theme.repository.ThemeRepository;
import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import com.swcamp9th.bangflixbackend.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ThemeServiceImpl implements ThemeService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    private final GenreRepository genreRepository;

    private final ThemeRepository themeRepository;
    private final ThemeReactionRepository themeReactionRepository;

    @Autowired
    public ThemeServiceImpl(
            ModelMapper modelMapper,
            UserRepository userRepository,
            StoreRepository storeRepository,
            GenreRepository genreRepository,
            ThemeRepository themeRepository,
            ThemeReactionRepository themeReactionRepository
    ) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
        this.genreRepository = genreRepository;
        this.themeRepository = themeRepository;
        this.themeReactionRepository = themeReactionRepository;
    }

    @Override
    @Transactional
    public ThemeDTO findTheme(Integer themeCode, String loginId) {
        Theme theme = themeRepository.findById(themeCode).orElseThrow();
        Member member = userRepository.findById(loginId).orElse(null);

        if(member == null)
            return createThemeDTO(theme, null);
        else
            return createThemeDTO(theme, member.getMemberCode());

    }

    @Override
    @Transactional
    public List<GenreDTO> findGenres() {
        List<Genre> genres = genreRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));

        return genres.stream()
                .map(genre -> modelMapper
                        .map(genre, GenreDTO.class))
                .toList();
    }

    @Override
    @Transactional
    public List<ThemeDTO> findThemeByGenresAndSearchOrderBySort(
            Pageable pageable,
            String filter,
            List<String> genres,
            String content,
            String loginId
    ) {
        Member member = userRepository.findById(loginId).orElse(null);
        List<Theme> themes = new ArrayList<>();

        if(genres != null){
            if(content != null)
                themes = themeRepository.findThemesByAllGenresAndSearch(genres, content);
            else
                themes = themeRepository.findThemesByAllGenres(genres);
        } else {
            if(content != null)
                themes = themeRepository.findThemesBySearch(content);
            else
                themes = themeRepository.findAll();
        }

        List<ThemeDTO> themesDTO = new ArrayList<>();

        for(Theme theme : themes) {
            if(member == null)
                themesDTO.add(createThemeDTO(theme, null));
            else
                themesDTO.add(createThemeDTO(theme, member.getMemberCode()));
        }

        if (filter != null) {
            switch (filter) {
                case "like":
                    themesDTO.sort(Comparator.comparing(ThemeDTO::getLikeCount).reversed()
                        .thenComparing(Comparator.comparing(ThemeDTO::getCreatedAt).reversed()));
                    break;

                case "scrap":
                    themesDTO.sort(Comparator.comparing(ThemeDTO::getScrapCount).reversed()
                        .thenComparing(Comparator.comparing(ThemeDTO::getCreatedAt).reversed()));
                    break;

                case "review":
                    themesDTO.sort(Comparator.comparing(ThemeDTO::getReviewCount).reversed()
                        .thenComparing(Comparator.comparing(ThemeDTO::getCreatedAt).reversed()));
                    break;

                default:
                    themesDTO.sort(Comparator.comparing(ThemeDTO::getCreatedAt).reversed()
                        .thenComparing(Comparator.comparing(ThemeDTO::getCreatedAt).reversed()));
                    break;
            }
        } else
            themesDTO.sort(Comparator.comparing(ThemeDTO::getCreatedAt).reversed());

        int startIndex = pageable.getPageNumber() * pageable.getPageSize();
        int lastIndex = Math.min((startIndex + pageable.getPageSize()), themes.size());
        return themesDTO.subList(startIndex, lastIndex);
    }

    @Override
    @Transactional
    public List<ThemeDTO> findThemeByStoreOrderBySort(
            Pageable pageable,
            String filter,
            Integer storeCode,
            String loginId
    ) {
        List<Theme> themes = themeRepository.findByStoreCode(storeCode);
        Member member = userRepository.findById(loginId).orElseThrow();
        List<ThemeDTO> themesDTO = new ArrayList<>();

        for(Theme theme : themes)
            themesDTO.add(createThemeDTO(theme, member.getMemberCode()));

        if (filter != null) {
            switch (filter) {
                case "like":
                    themesDTO.sort(Comparator.comparing(ThemeDTO::getLikeCount).reversed()
                        .thenComparing(Comparator.comparing(ThemeDTO::getCreatedAt).reversed()));
                    break;

                case "scrap":
                    themesDTO.sort(Comparator.comparing(ThemeDTO::getScrapCount).reversed()
                        .thenComparing(Comparator.comparing(ThemeDTO::getCreatedAt).reversed()));
                    break;

                case "review":
                    themesDTO.sort(Comparator.comparing(ThemeDTO::getReviewCount).reversed()
                        .thenComparing(Comparator.comparing(ThemeDTO::getCreatedAt).reversed()));
                    break;

                default:
                    themesDTO.sort(Comparator.comparing(ThemeDTO::getCreatedAt).reversed()
                        .thenComparing(Comparator.comparing(ThemeDTO::getCreatedAt).reversed()));
                    break;
            }
        } else
            themesDTO.sort(Comparator.comparing(ThemeDTO::getCreatedAt).reversed());


        int startIndex = pageable.getPageNumber() * pageable.getPageSize();
        int lastIndex = Math.min((startIndex + pageable.getPageSize()), themes.size());
        return themesDTO.subList(startIndex, lastIndex);
    }

    @Override
    @Transactional
    public void createThemeReaction(
            String userId,
            ThemeReactionDTO themeReactionDTO
    ) {
        Member member = userRepository.findById(userId).orElseThrow();
        Theme theme = themeRepository.findById(themeReactionDTO.getThemeCode()).orElseThrow();
        ThemeReaction themeReaction = themeReactionRepository.findReactionsByThemeCodeAndMemberCode(
            themeReactionDTO.getThemeCode(), member.getMemberCode()).orElse(null);

        if(themeReaction == null){
            themeReaction = new ThemeReaction();
            themeReaction.setMember(member);
            if(themeReactionDTO.getReaction().equals("like"))
                themeReaction.setReaction(ReactionType.LIKE);
            else if (themeReactionDTO.getReaction().equals("scrap"))
                themeReaction.setReaction(ReactionType.SCRAP);
            themeReaction.setCreatedAt(LocalDateTime.now());
            themeReaction.setActive(true);
            themeReaction.setTheme(theme);
            themeReaction.setThemeCode(theme.getThemeCode());
            themeReaction.setMemberCode(member.getMemberCode());
            themeReactionRepository.save(themeReaction);
        }
        else {
            if(themeReactionDTO.getReaction().equals("like")){
                if(themeReaction.getReaction().equals(ReactionType.LIKE))
                    return;
                else if (themeReaction.getReaction().equals(ReactionType.SCRAP))
                    themeReaction.setReaction(ReactionType.SCRAPLIKE);
                else if (themeReaction.getReaction().equals(ReactionType.SCRAPLIKE))
                    return;
            }
            else if (themeReactionDTO.getReaction().equals("scrap")){
                if(themeReaction.getReaction().equals(ReactionType.LIKE))
                    themeReaction.setReaction(ReactionType.SCRAPLIKE);
                else if (themeReaction.getReaction().equals(ReactionType.SCRAP))
                    return;
                else if (themeReaction.getReaction().equals(ReactionType.SCRAPLIKE))
                    return;
            }
            themeReactionRepository.save(themeReaction);
        }
    }

    @Override
    @Transactional
    public void deleteThemeReaction(String loginId, ThemeReactionDTO themeReactionDTO) {
        Member member = userRepository.findById(loginId).orElseThrow();
        ThemeReaction themeReaction = themeReactionRepository.findReactionsByThemeCodeAndMemberCode(
            themeReactionDTO.getThemeCode(), member.getMemberCode()).orElse(null);

        if(themeReaction != null) {
            if (themeReactionDTO.getReaction().equals("like")) {
                if (themeReaction.getReaction().equals(ReactionType.LIKE))
                    themeReactionRepository.delete(themeReaction);
                else if (themeReaction.getReaction().equals(ReactionType.SCRAP))
                    return;
                else if (themeReaction.getReaction().equals(ReactionType.SCRAPLIKE)){
                    themeReaction.setReaction(ReactionType.SCRAP);
                    themeReactionRepository.save(themeReaction);
                }
            } else if (themeReactionDTO.getReaction().equals("scrap")) {
                if (themeReaction.getReaction().equals(ReactionType.LIKE))
                    return;
                else if (themeReaction.getReaction().equals(ReactionType.SCRAP))
                    themeReactionRepository.delete(themeReaction);
                else if (themeReaction.getReaction().equals(ReactionType.SCRAPLIKE)){
                    themeReaction.setReaction(ReactionType.LIKE);
                    themeReactionRepository.save(themeReaction);
                }
            }
        }
    }

    @Override
    @Transactional
    public List<FindThemeByReactionDTO> findThemeByMemberReaction(
            Pageable pageable,
            String loginId,
            String reaction
    ) {
        Member member = userRepository.findById(loginId).orElseThrow();
        List<ThemeReaction> themeReactions;

        if(reaction.equals("like"))
            themeReactions = themeReactionRepository.findThemeByMemberLike(pageable, member.getMemberCode());

        else if(reaction.equals("scrap"))
            themeReactions = themeReactionRepository.findThemeByMemberScrap(pageable, member.getMemberCode());

        else
            throw new RuntimeException();


        List<FindThemeByReactionDTO> result = new ArrayList<>();

        for(ThemeReaction themeReaction : themeReactions){
            FindThemeByReactionDTO findThemeByReaction = modelMapper.map(themeReaction.getTheme(), FindThemeByReactionDTO.class);
            Store store = storeRepository.findByThemeCode(themeReaction.getTheme().getThemeCode());
            findThemeByReaction.setStoreCode(store.getStoreCode());
            findThemeByReaction.setStoreName(store.getName());
            findThemeByReaction.setIsLike(true);
            findThemeByReaction.setIsScrap(true);
            result.add(findThemeByReaction);
        }

        return result;
    }

    @Override
    public List<ThemeDTO> findThemeByWeek(String loginId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekAgo = now.minusWeeks(1);  // 현재로부터 1주일 이전
        Pageable pageable = PageRequest.of(0,5);

        List<Theme> themes = themeRepository.findByWeekOrderByLikes(oneWeekAgo, pageable);
        Member member = userRepository.findById(loginId).orElse(null);

        if(member == null)
            return createThemeDTOs(themes, null);

        return createThemeDTOs(themes, member.getMemberCode());
    }

    @Override
    @Transactional
    public List<ThemeDTO> recommendTheme(List<Integer> themeCodes) {
        Pageable pageable = PageRequest.of(0,5);

        if (themeCodes == null)
            return findThemeByGenresAndSearchOrderBySort(
                    pageable,
                    "like",
                    null,
                    null,
                    null
            );

        List<Integer> genres = new ArrayList<>(themeRepository.findGenresByThemeCode(themeCodes));

        HashMap<Integer, Integer> countMap = new HashMap<>();
        for (int number : genres) {
            countMap.put(number, countMap.getOrDefault(number, 0) + 1);
        }

        // 가장 많이 등장한 횟수 찾기
        int maxCount = 0;
        for (int count : countMap.values()) {
            if (count > maxCount) {
                maxCount = count;
            }
        }

        // 가장 많이 등장한 숫자들을 리스트에 저장
        List<Integer> mostFrequentNumbers = new ArrayList<>();
        for (int number : countMap.keySet()) {
            if (countMap.get(number) == maxCount)
                mostFrequentNumbers.add(number);
        }

        List<String> genreNames = genreRepository.findGenreNames(mostFrequentNumbers);

        return findThemeByGenresAndSearchOrderBySort(
                pageable,
                "like",
                genreNames,
                null,
                null
        );
    }

    @Transactional(readOnly = true)
    @Override
    public List<ThemeDTO> getScrapedTheme(String loginId) {
        Integer memberCode = userRepository.findById(loginId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 유저입니다."))
                .getMemberCode();
        List<ThemeReaction> themeReactions =
                themeReactionRepository.findThemeReactionsByMemberCodeAndReactionType(memberCode, List.of(ReactionType.SCRAP, ReactionType.SCRAPLIKE));

        List<Theme> themes = themeRepository.findByThemeCodes(themeReactions.stream()
                .map(ThemeReaction::getThemeCode)
                .toList()
        );

        return themes.stream()
                .map(theme ->
                        createThemeDTO(theme, memberCode))
                .toList();

    }



    private ThemeDTO createThemeDTO(Theme theme, Integer memberCode) {
        ThemeDTO themeDto = modelMapper.map(theme, ThemeDTO.class);
        themeDto.setStoreCode(theme.getStore().getStoreCode());
        themeDto.setLikeCount(themeRepository.countLikesByThemeCode(theme.getThemeCode()));
        themeDto.setScrapCount(themeRepository.countScrapsByThemeCode(theme.getThemeCode()));
        themeDto.setReviewCount(themeRepository.countReviewsByThemeCode(theme.getThemeCode()));
        themeDto.setStoreName(theme.getStore().getName());

        if(memberCode != null){
            ThemeReaction themeReaction = themeReactionRepository.findReactionsByThemeCodeAndMemberCode(theme.getThemeCode(), memberCode)
                    .orElse(null);

            if(themeReaction != null){
                themeDto.setIsLike(true);
                themeDto.setIsScrap(true);
            }
            else{
                themeDto.setIsLike(false);
                themeDto.setIsScrap(false);
            }
        }
        else {
            themeDto.setIsLike(false);
            themeDto.setIsScrap(false);
        }
        return themeDto;
    }

    private List<ThemeDTO> createThemeDTOs(List<Theme> themes, Integer memberCode) {

        List<ThemeDTO> themeDTOs = new ArrayList<>();

        for(Theme theme : themes) {
            ThemeDTO themeDto = modelMapper.map(theme, ThemeDTO.class);
            themeDto.setStoreCode(theme.getStore().getStoreCode());
            themeDto.setLikeCount(themeRepository.countLikesByThemeCode(theme.getThemeCode()));
            themeDto.setScrapCount(themeRepository.countScrapsByThemeCode(theme.getThemeCode()));
            themeDto.setReviewCount(themeRepository.countReviewsByThemeCode(theme.getThemeCode()));
            themeDto.setStoreName(theme.getStore().getName());

            if(memberCode != null){
                ThemeReaction themeReaction = themeReactionRepository.findReactionsByThemeCodeAndMemberCode(theme.getThemeCode(), memberCode)
                        .orElse(null);

                if(themeReaction != null){
                    themeDto.setIsLike(true);
                    themeDto.setIsScrap(true);
                }
                else{
                    themeDto.setIsLike(false);
                    themeDto.setIsScrap(false);
                }
            }
            else {
                themeDto.setIsLike(false);
                themeDto.setIsScrap(false);
            }

            themeDTOs.add(themeDto);
        }

        return themeDTOs;
    }
}
