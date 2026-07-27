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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

        Pageable pageable = PageRequest.of(0, 20);
        when(repository.findByProviderIdAndActiveTrue(eq(providerId), any())).thenReturn(new PageImpl<>(List.of(service)));

        Page<ServiceResponseDTO> response = listServicesService.execute(new ListServicesService.Input(providerId, pageable));

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        assertEquals("Serviço 1", response.getContent().get(0).getName());

        verify(repository, times(1)).findByProviderIdAndActiveTrue(eq(providerId), any());
    }
}
