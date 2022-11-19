package com.sideproject.codemoim.property;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
@ConstructorBinding
@ConfigurationProperties(prefix = "cloud.aws")
public class S3Properties {

    private final S3 s3;
    private final CloudFront cloudFront;

    @Getter
    @RequiredArgsConstructor
    public static final class S3 {
        private final String bucket;
    }

    @Getter
    @RequiredArgsConstructor
    public static final class CloudFront {
        private final String distributionDomain;
    }

}
