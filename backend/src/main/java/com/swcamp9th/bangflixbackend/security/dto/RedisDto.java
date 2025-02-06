package com.swcamp9th.bangflixbackend.security.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RedisDto {
    private String key;
    private String value;
}