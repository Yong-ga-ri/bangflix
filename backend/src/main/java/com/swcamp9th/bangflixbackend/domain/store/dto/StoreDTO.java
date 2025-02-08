package com.swcamp9th.bangflixbackend.domain.store.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StoreDTO {
    private Integer storeCode;
    private Boolean active;
    private String createdAt;
    private String name;
    private String address;
    private String pageUrl;
    private String image;
}
