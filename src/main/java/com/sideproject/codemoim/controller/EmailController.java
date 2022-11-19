package com.sideproject.codemoim.controller;

import com.sideproject.codemoim.annotation.AccessTokenUse;
import com.sideproject.codemoim.property.CustomProperties;
import com.sideproject.codemoim.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final CustomProperties customProperties;

    @GetMapping("/duplicate-check")
    public boolean duplicateCheck(@RequestParam(name = "email") String email) throws UnsupportedEncodingException {
        return emailService.duplicateCheck(email);
    }

    @AccessTokenUse
    @PostMapping("/send-verify-email")
    public String sendVerifyEmail(@RequestBody Map<String, Object> emailInfo, Long userId) throws MessagingException {
        return emailService.sendVerifyEmail(emailInfo, userId);
    }

    @GetMapping("/verify-key")
    public void verifyKey(@RequestParam("key") String key, HttpServletResponse response) throws IOException {
        String secretKey = emailService.verifyKey(key);
        String protocol = customProperties.getCookieConfig().getProtocol();
        String domain = customProperties.getCookieConfig().getFrontSubDomain();

        if(secretKey != null) {
            response.sendRedirect(protocol+ "://"+ domain +"/email/verify-success?key=" + secretKey);
        } else {
            response.sendRedirect(protocol+ "://" + domain + "/email/verify-fail");
        }
    }

    @PatchMapping("/key-expire")
    public void keyExpire(@RequestBody Map<String, Object> keyInfo) {
        emailService.keyExpire(keyInfo);
    }

    @PostMapping("/send-find-password-email")
    public boolean sendFindPasswordEmail(@RequestBody Map<String, Object> emailInfo) throws MessagingException {
        return emailService.sendFindPasswordEmail(emailInfo);
    }

}
