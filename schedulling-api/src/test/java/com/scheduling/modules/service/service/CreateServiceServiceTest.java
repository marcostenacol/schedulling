package com.scheduling.modules.service.service;

import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.service.dto.CreateServiceDTO;
import com.scheduling.modules.service.dto.CreateServiceRequest;
import com.scheduling.modules.service.dto.ServiceResponseDTO;
import com.scheduling.modules.service.model.ServiceOffered;
import com.scheduling.modules.service.repository.ServiceOfferedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateServiceServiceTest {

    @Mock
    private ServiceOfferedRepository repository;

    @InjectMocks
    private CreateServiceService createServiceService;

    private User provider;
    private CreateServiceDTO createDTO;

    @BeforeEach
    void setUp() {
        provider = User.builder().id(UUID.randomUUID()).build();
        createDTO = new CreateServiceDTO();
        createDTO.setName("Corte de Cabelo");
        createDTO.setPrice(new BigDecimal("50.00"));
        createDTO.setDurationMinutes(30);
    }

    @Test
    @DisplayName("Deve criar um serviço com sucesso")
    void shouldCreateServiceSuccessfully() {
        ServiceOffered saved = ServiceOffered.builder()
                .id(UUID.randomUUID())
                .provider(provider)
                .name(createDTO.getName())
                .price(createDTO.getPrice())
                .durationMinutes(createDTO.getDurationMinutes())
                .active(true)
                .build();

        when(repository.save(any(ServiceOffered.class))).thenReturn(saved);

        ServiceResponseDTO response = createServiceService.execute(new CreateServiceRequest(provider, createDTO));

        assertNotNull(response);
        assertEquals(createDTO.getName(), response.getName());
        assertEquals(provider.getId(), response.getProviderId());

        verify(repository, times(1)).save(any(ServiceOffered.class));
    }
}
