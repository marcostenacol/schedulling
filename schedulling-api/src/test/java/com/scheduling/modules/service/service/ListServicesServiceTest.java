package com.scheduling.modules.service.service;

import com.scheduling.modules.auth.model.User;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListServicesServiceTest {

    @Mock
    private ServiceOfferedRepository repository;

    @InjectMocks
    private ListServicesService listServicesService;

    private UUID providerId;

    @BeforeEach
    void setUp() {
        providerId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Deve listar serviços ativos do prestador")
    void shouldListActiveServices() {
        User provider = User.builder().id(providerId).build();
        ServiceOffered service = ServiceOffered.builder()
                .id(UUID.randomUUID())
                .provider(provider)
                .name("Serviço 1")
                .active(true)
                .build();

        when(repository.findByProviderIdAndActiveTrue(providerId)).thenReturn(List.of(service));

        List<ServiceResponseDTO> response = listServicesService.execute(providerId);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("Serviço 1", response.get(0).getName());

        verify(repository, times(1)).findByProviderIdAndActiveTrue(providerId);
    }
}
