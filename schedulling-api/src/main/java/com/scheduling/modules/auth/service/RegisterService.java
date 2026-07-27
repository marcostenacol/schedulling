package com.scheduling.modules.auth.service;

import com.scheduling.base.service.BaseService;
import com.scheduling.modules.auth.dto.RegisterDTO;
import com.scheduling.modules.auth.model.Role;
import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.auth.repository.RoleRepository;
import com.scheduling.modules.auth.repository.UserRepository;
import com.scheduling.shared.exception.AppException;
import com.scheduling.modules.profile.model.Profile;
import com.scheduling.modules.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterService implements BaseService<RegisterDTO, Void> {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Void execute(RegisterDTO input) {
        if (userRepository.existsByEmail(input.getEmail())) {
            log.warn("Tentativa de registro com email já cadastrado: {}", input.getEmail());
            throw new AppException("Email já cadastrado no sistema", HttpStatus.BAD_REQUEST);
        }

        Role role = roleRepository.findByName(input.getRole())
                .orElseThrow(() -> new AppException("Role inválida ou não encontrada", HttpStatus.BAD_REQUEST));

        User user = User.builder()
                .email(input.getEmail())
                .password(passwordEncoder.encode(input.getPassword()))
                .role(role)
                .build();

        User savedUser = userRepository.save(user);

        Profile profile = Profile.builder()
                .user(savedUser)
                .name(savedUser.getEmail().split("@")[0]) // Nome inicial baseado no email
                .type(input.getRole().name().replace("ROLE_", "").toLowerCase())
                .build();

        profileRepository.save(profile);

        log.info("Novo usuário registrado id={}, role={}", savedUser.getId(), role.getName());

        return null;
    }
}
