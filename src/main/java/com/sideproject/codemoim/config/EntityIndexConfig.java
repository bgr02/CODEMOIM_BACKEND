package com.sideproject.codemoim.config;

import com.sideproject.codemoim.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!test")
@Component
public class EntityIndexConfig implements CommandLineRunner {

    @Autowired(required = false)
    private SearchService searchService;

    @Override
    public void run(String... args) throws Exception {
        searchService.buildSearchIndex();
    }

}
