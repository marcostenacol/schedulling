package com.scheduling.modules.profile.service;

import com.scheduling.base.service.BaseService;
import com.scheduling.modules.profile.dto.ProfileResponseDTO;
import com.scheduling.modules.profile.model.Profile;
import com.scheduling.modules.profile.repository.ProfileRepository;
import com.scheduling.shared.exception.AppException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DetailProfileService implements BaseService<UUID, ProfileResponseDTO> {

  private final ProfileRepository profileRepository;

  @Override
  @Cacheable(value = "profiles", key = "#userId")
  public ProfileResponseDTO execute(UUID userId) {
    Profile profile =
        profileRepository
            .findByUserId(userId)
            .orElseThrow(() -> new AppException("Perfil não encontrado", HttpStatus.NOT_FOUND));

    return ProfileResponseDTO.builder()
        .id(profile.getId())
        .userId(profile.getUser().getId())
        .name(profile.getName())
        .email(profile.getUser().getEmail())
        .avatar(profile.getAvatar())
        .bio(profile.getBio())
        .type(profile.getType())
        .code(profile.getCode())
        .build();
  }
}
