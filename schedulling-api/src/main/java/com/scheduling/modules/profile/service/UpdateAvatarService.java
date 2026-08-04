package com.scheduling.modules.profile.service;

import com.scheduling.base.service.BaseService;
import com.scheduling.modules.profile.dto.ProfileResponseDTO;
import com.scheduling.modules.profile.dto.UpdateAvatarRequest;
import com.scheduling.modules.profile.model.Profile;
import com.scheduling.modules.profile.repository.ProfileRepository;
import com.scheduling.shared.exception.AppException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateAvatarService implements BaseService<UpdateAvatarRequest, ProfileResponseDTO> {

  private static final List<String> ALLOWED_CONTENT_TYPES =
      List.of("image/jpeg", "image/png", "image/webp", "image/gif");

  private final ProfileRepository profileRepository;

  @Value("${app.uploads-dir}")
  private String uploadsDir;

  @Override
  @CacheEvict(value = "profiles", key = "#input.userId")
  public ProfileResponseDTO execute(UpdateAvatarRequest input) {
    Profile profile =
        profileRepository
            .findByUserId(input.getUserId())
            .orElseThrow(() -> new AppException("Perfil não encontrado", HttpStatus.NOT_FOUND));

    MultipartFile file = input.getFile();
    validateFile(file);

    String filename = storeFile(file);
    deleteOldAvatarFile(profile.getAvatar());

    profile.setAvatar("avatars/" + filename);
    profileRepository.save(profile);

    log.info("Avatar atualizado userId={}", input.getUserId());

    return ProfileResponseDTO.builder()
        .id(profile.getId())
        .userId(profile.getUser().getId())
        .name(profile.getName())
        .email(profile.getUser().getEmail())
        .avatar(profile.getAvatar())
        .bio(profile.getBio())
        .type(profile.getType())
        .build();
  }

  private void validateFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new AppException("Nenhum arquivo enviado", HttpStatus.BAD_REQUEST);
    }

    if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
      throw new AppException(
          "Formato de imagem inválido. Use JPEG, PNG, WEBP ou GIF", HttpStatus.BAD_REQUEST);
    }
  }

  private String storeFile(MultipartFile file) {
    String originalFilename = file.getOriginalFilename();
    String extension =
        originalFilename != null && originalFilename.contains(".")
            ? originalFilename.substring(originalFilename.lastIndexOf('.'))
            : "";
    String filename = UUID.randomUUID() + extension;

    try {
      Path avatarsDir = Path.of(uploadsDir, "avatars");
      Files.createDirectories(avatarsDir);
      Files.copy(
          file.getInputStream(), avatarsDir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      log.error("Falha ao salvar arquivo de avatar", e);
      throw new AppException("Falha ao salvar avatar", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    return filename;
  }

  private void deleteOldAvatarFile(String currentAvatar) {
    if (currentAvatar == null || currentAvatar.isBlank()) {
      return;
    }

    try {
      Files.deleteIfExists(Path.of(uploadsDir, currentAvatar));
    } catch (IOException e) {
      log.warn("Falha ao remover avatar antigo: {}", currentAvatar, e);
    }
  }
}
