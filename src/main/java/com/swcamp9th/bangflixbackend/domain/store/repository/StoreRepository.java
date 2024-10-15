package com.swcamp9th.bangflixbackend.domain.store.repository;

import com.swcamp9th.bangflixbackend.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> {

}
