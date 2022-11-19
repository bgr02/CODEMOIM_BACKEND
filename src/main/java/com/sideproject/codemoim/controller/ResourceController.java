package com.sideproject.codemoim.controller;

import com.sideproject.codemoim.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/resource")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping("/upload")
    public String upload(@RequestPart(value="folder") String folder, @RequestPart(value="file") MultipartFile file) throws IOException {
        return resourceService.upload(folder, file);
    }

    @DeleteMapping("/delete")
    public void delete(@RequestBody Map<String ,Object> imgUrlInfo) {
        resourceService.delete(imgUrlInfo);
    }

}
