package com.swcamp9th.bangflixbackend.domain.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class ReissueTestController {

    @PostMapping("/test")
    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "토큰 재발급 테스트용 api")
    public String test() {
        return "It's work!";
    }

}