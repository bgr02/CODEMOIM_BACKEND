package com.sideproject.codemoim.property;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.List;

@Getter
@RequiredArgsConstructor
@ConstructorBinding
@ConfigurationProperties(prefix = "spring.datasource")
public class DatabaseProperties {

    private final Hikari hikari;
    private final String url;
    private final Replica replica;
    private final String driverClassName;
    private final String username;
    private final String password;

    @Getter
    @RequiredArgsConstructor
    public static final class Hikari {
        private final Boolean autoCommit;
    }

    @Getter
    @RequiredArgsConstructor
    public static final class Replica {
        private final String[] names;
        private final String url;
    }

}
