package com.scheduling.modules.profile.repository;

import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.profile.model.Profile;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {

  @Query("SELECT p FROM Profile p JOIN FETCH p.user WHERE p.user = :user")
  Optional<Profile> findByUser(User user);

  @Query("SELECT p FROM Profile p JOIN FETCH p.user WHERE p.user.id = :userId")
  Optional<Profile> findByUserId(UUID userId);
}
