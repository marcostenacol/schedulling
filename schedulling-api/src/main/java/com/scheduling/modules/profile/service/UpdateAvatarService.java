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
import java.util.Map;
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

  private static final Map<String, String> ALLOWED_CONTENT_TYPES_EXTENSIONS =
      Map.of(
          "image/jpeg", ".jpg",
          "image/png", ".png",
          "image/webp", ".webp",
          "image/gif", ".gif");

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
        .code(profile.getCode())
        .build();
  }

  private void validateFile(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new AppException("Nenhum arquivo enviado", HttpStatus.BAD_REQUEST);
    }

    if (!ALLOWED_CONTENT_TYPES_EXTENSIONS.containsKey(file.getContentType())) {
      throw new AppException(
          "Formato de imagem inválido. Use JPEG, PNG, WEBP ou GIF", HttpStatus.BAD_REQUEST);
    }
  }

  private String storeFile(MultipartFile file) {
    String extension = ALLOWED_CONTENT_TYPES_EXTENSIONS.get(file.getContentType());
    String filename = UUID.randomUUID() + extension;

    try {
      Path avatarsDir = Path.of(uploadsDir, "avatars").normalize();
      Files.createDirectories(avatarsDir);

      Path destination = avatarsDir.resolve(filename).normalize();
      if (!destination.startsWith(avatarsDir)) {
        throw new AppException("Nome de arquivo inválido", HttpStatus.BAD_REQUEST);
      }

      Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
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
