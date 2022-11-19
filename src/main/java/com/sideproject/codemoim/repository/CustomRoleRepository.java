package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.domain.Role;

import java.util.List;

public interface CustomRoleRepository {
    List<Role> searchRoleByUserId(Long userId);
}
