package com.sideproject.codemoim.property;

import org.hibernate.boot.jaxb.SourceType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class CustomPropertiesTest {

    @Autowired
    CustomProperties customProperties;

    @Test
    void propertyTest() {
        Assertions.assertEquals(customProperties.getToken().getTokenExpiredTime(), 1800000);
    }

}