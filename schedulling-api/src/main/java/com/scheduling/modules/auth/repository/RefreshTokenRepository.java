package com.scheduling.modules.auth.repository;

import com.scheduling.modules.auth.model.RefreshToken;
import com.scheduling.modules.auth.model.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
  Optional<RefreshToken> findByToken(String token);

  void deleteByUser(User user);
}
