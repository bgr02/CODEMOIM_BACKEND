package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.domain.Profile;
import com.sideproject.codemoim.domain.ProfileFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProfileFollowRepository extends JpaRepository<ProfileFollow, Long>, CustomProfileFollowRepository {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from ProfileFollow pf where pf.profile = :profile")
    int deleteFollowing(@Param("profile") Profile profile);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from ProfileFollow pf where pf.following = :profile")
    int deleteFollower(@Param("profile") Profile profile);

}
