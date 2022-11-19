package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.domain.Email;

import java.util.List;

public interface CustomEmailRepository {
    Email searchEmailByUserId(Long userId);
    Email searchEmailBySecretKey(String secretKey);
    Email verifySecretKey(String secretKey);
    Email searchEmailByEmail(String email);
}
