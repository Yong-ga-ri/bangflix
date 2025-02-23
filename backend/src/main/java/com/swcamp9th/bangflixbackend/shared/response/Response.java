package com.swcamp9th.bangflixbackend.shared.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class Response<T> {
    private int status;
    private String msg;
    private T result;
}
