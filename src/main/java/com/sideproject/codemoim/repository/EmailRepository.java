package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.domain.Email;
import com.sideproject.codemoim.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailRepository extends JpaRepository<Email, Long>, CustomEmailRepository {
    Optional<Email> findByUser(User user);
}
