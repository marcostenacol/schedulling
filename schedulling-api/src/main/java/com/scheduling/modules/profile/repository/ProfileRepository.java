package com.scheduling.modules.profile.repository;

import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.profile.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    Optional<Profile> findByUser(User user);
    Optional<Profile> findByUserId(UUID userId);
}
