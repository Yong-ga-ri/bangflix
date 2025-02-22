package com.swcamp9th.bangflixbackend.domain.store.repository;

import com.swcamp9th.bangflixbackend.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> {

    @Query("SELECT DISTINCT s " +
             "FROM Store s " +
             "JOIN Theme t" +
            "WHERE s.active = true " +
            "AND t.themeCode = :themeCode")
    Optional<Store> findStoreByThemeCode(@Param("themeCode") int themeCode);
}
