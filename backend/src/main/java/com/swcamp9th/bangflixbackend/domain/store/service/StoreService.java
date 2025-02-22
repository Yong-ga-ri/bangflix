package com.swcamp9th.bangflixbackend.domain.store.service;

import com.swcamp9th.bangflixbackend.domain.store.dto.StoreDTO;

public interface StoreService {

    StoreDTO findStore(Integer storeCode);
}
