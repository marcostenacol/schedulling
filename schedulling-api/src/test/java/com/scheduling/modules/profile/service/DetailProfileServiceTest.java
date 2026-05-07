package com.scheduling.modules.profile.service;

import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.profile.dto.ProfileResponseDTO;
import com.scheduling.modules.profile.model.Profile;
import com.scheduling.modules.profile.repository.ProfileRepository;
import com.scheduling.shared.exception.AppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DetailProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private DetailProfileService detailProfileService;

    private UUID userId;
    private Profile profile;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        User user = User.builder().id(userId).email("test@example.com").build();
        profile = Profile.builder()
                .id(UUID.randomUUID())
                .user(user)
                .name("Test User")
                .type("client")
                .build();
    }

    @Test
    @DisplayName("Deve retornar detalhes do perfil com sucesso")
    void shouldReturnProfileDetailsSuccessfully() {
        when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));

        ProfileResponseDTO response = detailProfileService.execute(userId);

        assertNotNull(response);
        assertEquals(profile.getName(), response.getName());
        assertEquals(profile.getUser().getEmail(), response.getEmail());

        verify(profileRepository, times(1)).findByUserId(userId);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o perfil não for encontrado")
    void shouldThrowExceptionWhenProfileNotFound() {
        when(profileRepository.findByUserId(userId)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> detailProfileService.execute(userId));

        assertEquals("Perfil não encontrado", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(profileRepository, times(1)).findByUserId(userId);
    }
}
