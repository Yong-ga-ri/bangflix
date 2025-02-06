package com.swcamp9th.bangflixbackend.domain.store.repository;

import com.swcamp9th.bangflixbackend.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Integer> {

    @Query("SELECT s FROM Store s INNER JOIN Theme t ON t.store.storeCode = s.storeCode "
        + "WHERE t.themeCode = :themeCode GROUP BY s.storeCode")
    Store findByThemeCode(@Param("themeCode") Integer themeCode);
}
