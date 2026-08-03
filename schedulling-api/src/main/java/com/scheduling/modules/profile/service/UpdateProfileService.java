package com.scheduling.modules.profile.service;

import com.scheduling.base.service.BaseService;
import com.scheduling.modules.profile.dto.ProfileResponseDTO;
import com.scheduling.modules.profile.dto.UpdateProfileRequest;
import com.scheduling.modules.profile.model.Profile;
import com.scheduling.modules.profile.repository.ProfileRepository;
import com.scheduling.shared.exception.AppException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateProfileService implements BaseService<UpdateProfileRequest, ProfileResponseDTO> {

  private final ProfileRepository profileRepository;

  @Override
  @CacheEvict(value = "profiles", key = "#input.userId")
  public ProfileResponseDTO execute(UpdateProfileRequest input) {
    Profile profile =
        profileRepository
            .findByUserId(input.getUserId())
            .orElseThrow(() -> new AppException("Perfil não encontrado", HttpStatus.NOT_FOUND));

    profile.setName(input.getData().getName());
    profile.setBio(input.getData().getBio());

    profileRepository.save(profile);

    log.info("Perfil atualizado userId={}", input.getUserId());

    return ProfileResponseDTO.builder()
        .id(profile.getId())
        .name(profile.getName())
        .email(profile.getUser().getEmail())
        .avatar(profile.getAvatar())
        .bio(profile.getBio())
        .type(profile.getType())
        .build();
  }
}
