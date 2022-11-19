package com.sideproject.codemoim.config;

import com.sideproject.codemoim.property.CustomProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${custom.cookieConfig.protocol}")
    private String protocol;

    @Value("${custom.cookieConfig.frontSubDomain}")
    private String domain;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //WebMvcConfigurer.super.addCorsMappings(registry);
        registry.addMapping("/**")
                .allowedOrigins(protocol + "://" + domain)
                .allowedHeaders("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowCredentials(true)
                //.exposedHeaders("jwt-token")
                .maxAge(3600);
    }

}
