package com.scheduling.modules.service.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.service.dto.ServiceResponseDTO;
import com.scheduling.modules.service.dto.UpdateServiceDTO;
import com.scheduling.modules.service.dto.UpdateServiceRequest;
import com.scheduling.modules.service.model.ServiceOffered;
import com.scheduling.modules.service.repository.ServiceOfferedRepository;
import com.scheduling.shared.exception.AppException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class UpdateServiceServiceTest {

  @Mock private ServiceOfferedRepository repository;

  @InjectMocks private UpdateServiceService updateServiceService;

  private User provider;
  private ServiceOffered service;
  private UUID serviceId;

  @BeforeEach
  void setUp() {
    provider = User.builder().id(UUID.randomUUID()).build();
    serviceId = UUID.randomUUID();
    service = ServiceOffered.builder().id(serviceId).provider(provider).name("Corte").build();
  }

  @Test
  @DisplayName("Deve atualizar um serviço com sucesso")
  void shouldUpdateServiceSuccessfully() {
    UpdateServiceDTO dto = new UpdateServiceDTO();
    dto.setName("Corte Premium");

    when(repository.findById(serviceId)).thenReturn(Optional.of(service));
    when(repository.save(any(ServiceOffered.class))).thenReturn(service);

    ServiceResponseDTO response =
        updateServiceService.execute(new UpdateServiceRequest(serviceId, provider, dto));

    assertNotNull(response);
    assertEquals("Corte Premium", response.getName());

    verify(repository, times(1)).save(any(ServiceOffered.class));
  }

  @Test
  @DisplayName("Deve lançar exceção ao tentar atualizar serviço de outro prestador")
  void shouldThrowExceptionWhenForbidden() {
    User anotherProvider = User.builder().id(UUID.randomUUID()).build();
    UpdateServiceDTO dto = new UpdateServiceDTO();

    when(repository.findById(serviceId)).thenReturn(Optional.of(service));

    AppException exception =
        assertThrows(
            AppException.class,
            () ->
                updateServiceService.execute(
                    new UpdateServiceRequest(serviceId, anotherProvider, dto)));

    assertEquals(HttpStatus.FORBIDDEN, exception.getStatus());
  }
}
