package com.swcamp9th.bangflixbackend.domain.theme.service;

import com.swcamp9th.bangflixbackend.domain.store.dto.StoreDTO;
import com.swcamp9th.bangflixbackend.domain.store.service.StoreService;
import com.swcamp9th.bangflixbackend.domain.theme.dto.*;
import com.swcamp9th.bangflixbackend.domain.theme.dto.mapper.ReactionMapper;
import com.swcamp9th.bangflixbackend.domain.theme.entity.ReactionType;
import com.swcamp9th.bangflixbackend.domain.theme.entity.Theme;
import com.swcamp9th.bangflixbackend.domain.theme.entity.ThemeReaction;
import com.swcamp9th.bangflixbackend.domain.theme.exception.*;
import com.swcamp9th.bangflixbackend.domain.theme.repository.GenreRepository;
import com.swcamp9th.bangflixbackend.domain.theme.repository.ThemeReactionRepository;
import com.swcamp9th.bangflixbackend.domain.theme.repository.ThemeRepository;
import com.swcamp9th.bangflixbackend.domain.user.entity.Member;
import java.time.LocalDateTime;
import java.util.*;

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
    private final StoreService storeService;
    private final GenreRepository genreRepository;
    private final ThemeRepository themeRepository;
    private final ThemeReactionRepository themeReactionRepository;

    @Autowired
    public ThemeServiceImpl(
            ModelMapper modelMapper,
            StoreService storeService,
            GenreRepository genreRepository,
            ThemeRepository themeRepository,
            ThemeReactionRepository themeReactionRepository
    ) {
        this.modelMapper = modelMapper;
        this.storeService = storeService;
        this.genreRepository = genreRepository;
        this.themeRepository = themeRepository;
        this.themeReactionRepository = themeReactionRepository;
    }

    @Override
    @Transactional
    public ThemeDTO findThemeDTOByThemeCode(int themeCode) {
        return createBaseThemeDTO(findThemeByThemeCode(themeCode));
    }

    public Theme findThemeByThemeCode(int themeCode) {
        return themeRepository.findById(themeCode)
                .orElseThrow(ThemeNotFoundException::new);
    }

    @Override
    @Transactional
    public ThemeDTO findThemeDTOByThemeCode(
            int themeCode,
            int memberCode
    ) {
        Theme theme = themeRepository.findById(themeCode)
                .orElseThrow(ThemeNotFoundException::new);

        return createThemeDTO(theme, memberCode);
    }


    @Override
    @Transactional
    public List<GenreDTO> findGenres() {
        return genreRepository.findAll(Sort.by(Sort.Direction.ASC, "name")).stream()
                .map(genre -> modelMapper.map(genre, GenreDTO.class))
                .toList();
    }

    @Override
    @Transactional
    public List<ThemeDTO> findThemeByGenresAndSearchOrderBySort(
            Pageable pageable,
            String sort,
            List<String> genres,
            String search,
            int memberCode
    ) {
        List<Theme> themes = fetchThemesBy(pageable, genres, search);
        List<ThemeDTO> themeDTOList = createThemeDTOList(themes, memberCode);
        sortThemeList(themeDTOList, sort);
        return themeDTOList;
    }

    @Override
    @Transactional
    public List<ThemeDTO> findThemeByGenresAndSearchOrderBySort(
            Pageable pageable,
            String sort,
            List<String> genres,
            String search
    ) {
        List<Theme> themes = fetchThemesBy(pageable, genres, search);
        List<ThemeDTO> themeDTOList = createThemeDTOList(themes);
        sortThemeList(themeDTOList, sort);
        return themeDTOList;
    }

    private List<Theme> fetchThemesBy(
            Pageable pageable,
            List<String> genres,
            String search
    ) {
        return themeRepository.findThemesBy(genres, search, pageable);
    }

    @Override
    @Transactional
    public List<ThemeDTO> findThemeDTOListByStoreCode(
            Pageable pageable,
            String sort,
            int storeCode,
            int memberCode
    ) {
        List<Theme> themes = themeRepository.findThemeListByStoreCode(storeCode, pageable);
        List<ThemeDTO> themeDTOList = createThemeDTOList(themes, memberCode);
        sortThemeList(themeDTOList, sort);
        return themeDTOList;
    }

    @Override
    @Transactional
    public List<ThemeDTO> findThemeDTOListByStoreCode(
            Pageable pageable,
            String sort,
            int storeCode
    ) {
        List<Theme> themes = themeRepository.findThemeListByStoreCode(storeCode, pageable);
        List<ThemeDTO> themeDTOList = createThemeDTOList(themes);
        sortThemeList(themeDTOList, sort);
        return themeDTOList;
    }

    @Override
    @Transactional
    public void createThemeReaction(
            Member member,
            ThemeReactionDTO themeReactionDTO
    ) {
        Theme theme = themeRepository.findById(themeReactionDTO.getThemeCode())
                .orElseThrow(ThemeNotFoundException::new);
        ThemeReaction themeReaction = themeReactionRepository
                .findReactionByThemeCodeAndMemberCode(
                        themeReactionDTO.getThemeCode(),
                        member.getMemberCode()
                )
                .orElse(null);
        if (themeReaction == null) {
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
            if (themeReactionDTO.getReaction().equals("like")) {
                if (themeReaction.getReaction().equals(ReactionType.LIKE))
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
    public void deleteThemeReaction(
            int memberCode,
            ThemeReactionDTO themeReactionDTO
    ) {
        ThemeReaction themeReaction =
                themeReactionRepository.findReactionByThemeCodeAndMemberCode(themeReactionDTO.getThemeCode(), memberCode)
                .orElseThrow(ReactionNotFoundException::new);

        ReactionType currentReaction = themeReaction.getReaction();
        String requestedReaction = themeReactionDTO.getReaction();

        // 요청이 'like'인 경우
        if ("like".equals(requestedReaction)) {
            if (currentReaction == ReactionType.LIKE) {

                // 이미 좋아요 상태이면 삭제
                themeReactionRepository.delete(themeReaction);
            } else if (currentReaction == ReactionType.SCRAPLIKE) {

                // 스크랩+좋아요인 경우 좋아요만 취소 -> 스크랩 상태로 변경
                themeReaction.setReaction(ReactionType.SCRAP);
                themeReactionRepository.save(themeReaction);
            }
        }
        // 요청이 'scrap'인 경우
        else if ("scrap".equals(requestedReaction)) {
            if (currentReaction == ReactionType.SCRAP) {

                // 이미 스크랩 상태이면 삭제
                themeReactionRepository.delete(themeReaction);
            } else if (currentReaction == ReactionType.SCRAPLIKE) {

                // 스크랩+좋아요인 경우 스크랩만 취소 -> 좋아요 상태로 변경
                themeReaction.setReaction(ReactionType.LIKE);
                themeReactionRepository.save(themeReaction);
            }
        } else {
            throw new UnexpectedReactionTypeException("잘못된 타입입니다. + 요청된 리액션: " + requestedReaction);
        }
    }

    @Override
    @Transactional
    public List<FindThemeByReactionDTO> findThemeByMemberReaction(
            Pageable pageable,
            int memberCode,
            String reaction
    ) {
        List<ThemeReaction> themeReactions;

        if(reaction.equals("like"))
            themeReactions = themeReactionRepository.findLikeReactionsByMemberCode(pageable, memberCode);

        else if(reaction.equals("scrap"))
            themeReactions = themeReactionRepository.findScrapReactionsByMemberCode(pageable, memberCode);

        else
            throw new UnexpectedReactionTypeException("잘못된 타입입니다. + 요청된 리액션: " + reaction);


        List<FindThemeByReactionDTO> result = new ArrayList<>();

        for(ThemeReaction themeReaction : themeReactions){
            FindThemeByReactionDTO findThemeByReaction = modelMapper.map(themeReaction.getTheme(), FindThemeByReactionDTO.class);

            StoreDTO storeDTO = storeService.findStore(themeReaction.getTheme().getThemeCode());
            findThemeByReaction.setStoreCode(storeDTO.getStoreCode());
            findThemeByReaction.setStoreName(storeDTO.getName());
            findThemeByReaction.setIsLike(true);
            findThemeByReaction.setIsScrap(true);
            result.add(findThemeByReaction);
        }

        return result;
    }

    @Override
    public List<ThemeDTO> findThemeByWeek(int memberCode) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);  // 현재로부터 1주일 이전
        Pageable pageable = PageRequest.of(0,5);

        List<Theme> themes = themeRepository.findByWeekOrderByLikes(oneWeekAgo, pageable);

        return createThemeDTOList(themes, memberCode);
    }

    @Override
    public List<ThemeDTO> findThemeByWeek() {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);  // 현재로부터 1주일 이전
        Pageable pageable = PageRequest.of(0,5);

        List<Theme> themes = themeRepository.findByWeekOrderByLikes(oneWeekAgo, pageable);

        return createThemeDTOList(themes);
    }

    @Override
    @Transactional
    public List<ThemeDTO> recommendTheme(List<Integer> themeCodes) {
        return findThemeByGenresAndSearchOrderBySort(
                PageRequest.of(0,5),
                "like",
                (themeCodes == null) ? null : getGenreNameListByThemeCodeList(themeCodes),
                null
        );
    }

    @Transactional(readOnly = true)
    @Override
    public List<ThemeDTO> getScrapedThemeByMemberCode(int memberCode) {
        List<ThemeReaction> themeReactions =
                themeReactionRepository.findThemeReactionsByMemberCodeAndReactionType(memberCode, List.of(ReactionType.SCRAP, ReactionType.SCRAPLIKE));

        List<Theme> themes = themeRepository.findByThemeCodes(themeReactions.stream()
                .map(ThemeReaction::getThemeCode)
                .toList()
        );

        return themes.stream()
                .map(theme -> createThemeDTO(theme, memberCode))
                .toList();

    }

    private List<String> getGenreNameListByThemeCodeList(List<Integer> themeCodes) {
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

        return genreRepository.findGenreNames(mostFrequentNumbers);
    }

    private List<ThemeDTO> createThemeDTOList(
            List<Theme> themes,
            int memberCode) {

        List<ThemeDTO> themeDTOList = new ArrayList<>();

        themes.stream()
                .map(theme -> {
                    ThemeDTO themeDto = createBaseThemeDTO(theme);
                    applyReaction(theme.getThemeCode(), memberCode, themeDto);
                    return themeDto;
                })
                .forEach(themeDTOList::add);

        return themeDTOList;
    }

    private List<ThemeDTO> createThemeDTOList(
            List<Theme> themes
    ) {
        List<ThemeDTO> themeDTOList = new ArrayList<>();

        themes.stream()
                .map(this::createBaseThemeDTO)
                .forEach(themeDTOList::add);

        return themeDTOList;
    }

    private ThemeDTO createThemeDTO(
            Theme theme,
            int memberCode
    ) {
        ThemeDTO themeDto = createBaseThemeDTO(theme);
        applyReaction(theme.getThemeCode(), memberCode, themeDto);
        return themeDto;
    }

    private ThemeDTO createBaseThemeDTO(Theme theme) {
        ThemeDTO themeDto = modelMapper.map(theme, ThemeDTO.class);
        ThemeCountDTO reactions = getThemeReactions(theme.getThemeCode());
        themeDto.setLikeCount(Math.toIntExact(reactions.getLikeCount()));
        themeDto.setScrapCount(Math.toIntExact(reactions.getScrapCount()));
        themeDto.setReviewCount(Math.toIntExact(reactions.getReviewCount()));
        themeDto.setStoreCode(theme.getStore().getStoreCode());
        themeDto.setStoreName(theme.getStore().getName());
        themeDto.setIsLike(false);
        themeDto.setIsScrap(false);
        return themeDto;
    }

    private ThemeCountDTO getThemeReactions(int themeCode) {
        return themeRepository.findThemeCountsByThemeCode(themeCode).orElseThrow(ThemeNotFoundException::new);
    }

    private void applyReaction(
            int themeCode,
            int memberCode,
            ThemeDTO themeDto
    ) {
        themeReactionRepository.findReactionByThemeCodeAndMemberCode(themeCode, memberCode)
                .ifPresent(reaction -> ReactionMapper.applyReaction(themeDto, reaction.getReaction()));
    }

    private void sortThemeList(
            List<ThemeDTO> themesDTO,
            String sort
    ) {
        if (sort == null) {
            sort = "NONE";
        }
        switch (sort) {
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
    }
}
