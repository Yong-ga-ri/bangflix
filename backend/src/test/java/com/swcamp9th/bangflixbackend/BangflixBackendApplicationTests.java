package com.swcamp9th.bangflixbackend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("prod") // application-{}.yml 에 맞게 설정
class BangflixBackendApplicationTests {

    @Test
    void contextLoads() {
    }

}
