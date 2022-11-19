package com.sideproject.codemoim.controller;

import com.sideproject.codemoim.annotation.AccessTokenUse;
import com.sideproject.codemoim.exception.InvalidSecretKeyException;
import com.sideproject.codemoim.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/username-duplicate-check")
    public boolean usernameDuplicateCheck(@RequestParam(name = "username") String username) throws UnsupportedEncodingException {
        return userService.usernameDuplicateCheck(username);
    }

    @PostMapping("/signup")
    public void signUp(@RequestBody Map<String, Object> userInfo) {
        userService.signUp(userInfo);
    }

    @AccessTokenUse
    @PatchMapping("/password-change")
    public void passwordChange(@RequestBody Map<String, Object> passwordInfo, Long userId) {
        userService.passwordChange(passwordInfo, userId);
    }

    @AccessTokenUse
    @PatchMapping("/withdrawal")
    public boolean withdrawal(@RequestBody Map<String, Object> passwordInfo, Long userId) {
        return userService.withdrawal(passwordInfo, userId);
    }

    @PatchMapping("/key-expired")
    public void keyExpire(@RequestBody Map<String, Object> keyInfo) throws InvalidSecretKeyException {
        userService.keyExpired(keyInfo);
    }

    @PatchMapping("/find-password")
    public void findPassword(@RequestBody Map<String, Object> passwordInfo) {
        userService.findPassword(passwordInfo);
    }

}