package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long>, CustomProfileRepository {

}
