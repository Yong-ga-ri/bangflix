package com.swcamp9th.bangflixbackend.domain.store.service;

import com.swcamp9th.bangflixbackend.domain.store.dto.StoreDTO;
import com.swcamp9th.bangflixbackend.domain.store.exception.StoreNotFoundException;
import com.swcamp9th.bangflixbackend.domain.store.repository.StoreRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StoreServiceImpl implements StoreService {

    private final ModelMapper modelMapper;
    private final StoreRepository storeRepository;

    @Autowired
    public StoreServiceImpl(
            ModelMapper modelMapper,
            StoreRepository storeRepository
    ) {
        this.modelMapper = modelMapper;
        this.storeRepository = storeRepository;
    }

    @Override
    @Transactional
    public StoreDTO findStore(Integer storeCode) {
        return modelMapper.map(
                storeRepository.findById(storeCode)
                        .orElseThrow(StoreNotFoundException::new),
                StoreDTO.class);
    }
}
