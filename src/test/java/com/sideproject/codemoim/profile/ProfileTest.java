package com.sideproject.codemoim.profile;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ProfileTest {

    @Value("${test.value}")
    private String value;

    @Test
    void profileTest() {
        System.out.println(value);
    }

}