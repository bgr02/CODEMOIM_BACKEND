package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.domain.Role;
import com.sideproject.codemoim.domain.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long>, CustomRoleRepository {
    Role findByName(RoleName name);
}
