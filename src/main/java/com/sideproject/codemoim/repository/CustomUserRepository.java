package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.domain.User;

import java.util.Optional;

public interface CustomUserRepository {
    Optional<User> searchUserByIdAndStatus(Long id);
    User usernameDuplicateCheck(String username);
    User verifySecretKey(String key);
}
