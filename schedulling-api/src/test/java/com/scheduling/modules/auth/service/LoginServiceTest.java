package com.scheduling.modules.auth.service;

import com.scheduling.modules.auth.dto.LoginDTO;
import com.scheduling.modules.auth.dto.TokenResponseDTO;
import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.auth.repository.UserRepository;
import com.scheduling.shared.exception.AppException;
import com.scheduling.shared.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private LoginService loginService;

    private LoginDTO loginDTO;
    private User user;

    @BeforeEach
    void setUp() {
        loginDTO = new LoginDTO();
        loginDTO.setEmail("test@example.com");
        loginDTO.setPassword("password123");

        user = User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .build();
    }

    @Test
    @DisplayName("Deve realizar login com sucesso e retornar tokens")
    void shouldLoginSuccessfully() {
        when(userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwtToken");

        TokenResponseDTO response = loginService.execute(loginDTO);

        assertNotNull(response);
        assertEquals("jwtToken", response.getAccessToken());
        assertEquals("to-be-implemented", response.getRefreshToken());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByEmail(loginDTO.getEmail());
        verify(jwtService, times(1)).generateToken(user);
    }

    @Test
    @DisplayName("Deve lançar exceção quando as credenciais são inválidas")
    void shouldThrowExceptionWhenCredentialsAreInvalid() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Bad credentials"));

        AppException exception = assertThrows(AppException.class, () -> loginService.execute(loginDTO));

        assertEquals("Credenciais inválidas", exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o usuário não é encontrado após autenticação")
    void shouldThrowExceptionWhenUserNotFoundAfterAuth() {
        when(userRepository.findByEmail(loginDTO.getEmail())).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> loginService.execute(loginDTO));

        assertEquals("Usuário não encontrado", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByEmail(loginDTO.getEmail());
        verify(jwtService, never()).generateToken(any(User.class));
    }
}
