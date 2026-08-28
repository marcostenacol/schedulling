package com.scheduling.modules.auth.service;

import com.scheduling.base.service.BaseService;
import com.scheduling.modules.auth.dto.RegisterDTO;
import com.scheduling.modules.auth.enums.RoleEnum;
import com.scheduling.modules.auth.model.Role;
import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.auth.repository.RoleRepository;
import com.scheduling.modules.auth.repository.UserRepository;
import com.scheduling.modules.profile.model.Profile;
import com.scheduling.modules.profile.repository.ProfileRepository;
import com.scheduling.shared.exception.AppException;
import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterService implements BaseService<RegisterDTO, Void> {

  private static final String CODE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
  private static final int CODE_LENGTH = 6;
  private static final SecureRandom RANDOM = new SecureRandom();

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final ProfileRepository profileRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public Void execute(RegisterDTO input) {
    if (input.getRole() == RoleEnum.ROLE_ADMIN) {
      log.warn("Tentativa de auto-registro com role de administrador: {}", input.getEmail());
      throw new AppException("Role inválida ou não encontrada", HttpStatus.BAD_REQUEST);
    }

    if (userRepository.existsByEmail(input.getEmail())) {
      log.warn("Tentativa de registro com email já cadastrado: {}", input.getEmail());
      throw new AppException("Não foi possível concluir o cadastro", HttpStatus.BAD_REQUEST);
    }

    Role role =
        roleRepository
            .findByName(input.getRole())
            .orElseThrow(
                () -> new AppException("Role inválida ou não encontrada", HttpStatus.BAD_REQUEST));

    User user =
        User.builder()
            .email(input.getEmail())
            .password(passwordEncoder.encode(input.getPassword()))
            .role(role)
            .build();

    User savedUser = userRepository.save(user);

    Profile profile =
        Profile.builder()
            .user(savedUser)
            .name(input.getName())
            .type(input.getRole().toProfileType())
            .code(generateUniqueCode())
            .build();

    profileRepository.save(profile);

    log.info("Novo usuário registrado id={}, role={}", savedUser.getId(), role.getName());

    return null;
  }

  private String generateUniqueCode() {
    String code;
    do {
      code = randomCode();
    } while (profileRepository.existsByCode(code));
    return code;
  }

  private String randomCode() {
    StringBuilder sb = new StringBuilder(CODE_LENGTH);
    for (int i = 0; i < CODE_LENGTH; i++) {
      sb.append(CODE_ALPHABET.charAt(RANDOM.nextInt(CODE_ALPHABET.length())));
    }
    return sb.toString();
  }
}
