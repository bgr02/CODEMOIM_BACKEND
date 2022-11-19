package com.sideproject.codemoim.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/external")
public class HealthCheckController {

    @RequestMapping("/health-check")
    public void healthCheck() {
        log.info("======================== Health Check ========================");
    }

}
