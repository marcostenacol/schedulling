package com.scheduling.modules.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.scheduling.modules.auth.dto.RegisterDTO;
import com.scheduling.modules.auth.enums.RoleEnum;
import com.scheduling.modules.auth.model.Role;
import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.auth.repository.RoleRepository;
import com.scheduling.modules.auth.repository.UserRepository;
import com.scheduling.modules.profile.model.Profile;
import com.scheduling.modules.profile.repository.ProfileRepository;
import com.scheduling.shared.exception.AppException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private RoleRepository roleRepository;

  @Mock private ProfileRepository profileRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private RegisterService registerService;

  private RegisterDTO registerDTO;
  private Role role;

  @BeforeEach
  void setUp() {
    registerDTO = new RegisterDTO();
    registerDTO.setName("Teste");
    registerDTO.setEmail("test@example.com");
    registerDTO.setPassword("password123");
    registerDTO.setRole(RoleEnum.ROLE_CLIENT);

    role = Role.builder().name(RoleEnum.ROLE_CLIENT).build();
  }

  @Test
  @DisplayName("Deve registrar um usuário com sucesso")
  void shouldRegisterUserSuccessfully() {
    when(userRepository.existsByEmail(registerDTO.getEmail())).thenReturn(false);
    when(roleRepository.findByName(registerDTO.getRole())).thenReturn(Optional.of(role));
    when(passwordEncoder.encode(registerDTO.getPassword())).thenReturn("encodedPassword");

    User user = User.builder().email(registerDTO.getEmail()).build();
    when(userRepository.save(any(User.class))).thenReturn(user);

    assertDoesNotThrow(() -> registerService.execute(registerDTO));

    verify(userRepository, times(1)).existsByEmail(registerDTO.getEmail());
    verify(roleRepository, times(1)).findByName(registerDTO.getRole());
    verify(passwordEncoder, times(1)).encode(registerDTO.getPassword());
    verify(userRepository, times(1)).save(any(User.class));
    verify(profileRepository, times(1)).save(any(Profile.class));
  }

  @Test
  @DisplayName("Deve lançar exceção quando o email já existe")
  void shouldThrowExceptionWhenEmailExists() {
    when(userRepository.existsByEmail(registerDTO.getEmail())).thenReturn(true);

    AppException exception =
        assertThrows(AppException.class, () -> registerService.execute(registerDTO));

    assertEquals("Email já cadastrado no sistema", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

    verify(userRepository, times(1)).existsByEmail(registerDTO.getEmail());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("Deve lançar exceção ao tentar se auto-registrar como administrador")
  void shouldThrowExceptionWhenRegisteringAsAdmin() {
    registerDTO.setRole(RoleEnum.ROLE_ADMIN);

    AppException exception =
        assertThrows(AppException.class, () -> registerService.execute(registerDTO));

    assertEquals("Role inválida ou não encontrada", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

    verify(userRepository, never()).existsByEmail(any());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  @DisplayName("Deve lançar exceção quando a role não é encontrada")
  void shouldThrowExceptionWhenRoleNotFound() {
    when(userRepository.existsByEmail(registerDTO.getEmail())).thenReturn(false);
    when(roleRepository.findByName(registerDTO.getRole())).thenReturn(Optional.empty());

    AppException exception =
        assertThrows(AppException.class, () -> registerService.execute(registerDTO));

    assertEquals("Role inválida ou não encontrada", exception.getMessage());
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

    verify(roleRepository, times(1)).findByName(registerDTO.getRole());
    verify(userRepository, never()).save(any(User.class));
  }
}
