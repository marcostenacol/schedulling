package com.scheduling.modules.auth.service;

import com.scheduling.modules.auth.model.RefreshToken;
import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.auth.repository.RefreshTokenRepository;
import com.scheduling.shared.exception.AppException;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

  private static final int EXPIRY_DAYS = 7;

  private final RefreshTokenRepository refreshTokenRepository;

  @Transactional
  public RefreshToken createRefreshToken(User user) {
    // Delete any existing refresh token for this user
    refreshTokenRepository.deleteByUser(user);

    RefreshToken refreshToken =
        RefreshToken.builder()
            .token(UUID.randomUUID().toString())
            .user(user)
            .expiryDate(LocalDateTime.now().plusDays(EXPIRY_DAYS))
            .build();

    return refreshTokenRepository.save(refreshToken);
  }

  public RefreshToken validateRefreshToken(String token) {
    RefreshToken refreshToken =
        refreshTokenRepository
            .findByToken(token)
            .orElseThrow(
                () -> {
                  log.warn("Tentativa de uso de refresh token inválido");
                  return new AppException("Refresh token inválido", HttpStatus.UNAUTHORIZED);
                });

    if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
      refreshTokenRepository.delete(refreshToken);
      log.warn(
          "Tentativa de uso de refresh token expirado para usuário id={}",
          refreshToken.getUser().getId());
      throw new AppException("Refresh token expirado", HttpStatus.UNAUTHORIZED);
    }

    return refreshToken;
  }

  @Transactional
  public void deleteByUser(User user) {
    refreshTokenRepository.deleteByUser(user);
  }
}
