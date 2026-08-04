package com.scheduling.modules.service.service;

import com.scheduling.base.service.BaseService;
import com.scheduling.modules.profile.repository.ProfileRepository;
import com.scheduling.modules.service.dto.ServiceResponseDTO;
import com.scheduling.modules.service.model.ServiceOffered;
import com.scheduling.modules.service.repository.ServiceOfferedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * Serviços públicos de UM prestador específico — usado por quem já tem o código/link do
 * prestador. Deliberadamente não existe um "listar todos os serviços de todos os prestadores":
 * exporia a base inteira de prestadores pra qualquer usuário autenticado, então a busca sempre
 * exige saber de antemão o {@code providerId} (ver {@link ListPublicServicesService.Input}).
 */
@Service
@RequiredArgsConstructor
public class ListPublicServicesService
    implements BaseService<ListPublicServicesService.Input, Page<ServiceResponseDTO>> {

  private final ServiceOfferedRepository repository;
  private final ProfileRepository profileRepository;

  @Override
  public Page<ServiceResponseDTO> execute(Input input) {
    Page<ServiceOffered> services =
        repository.findByProviderIdAndActiveTrue(input.providerId(), input.pageable());

    return services.map(this::toResponseDTO);
  }

  private ServiceResponseDTO toResponseDTO(ServiceOffered s) {
    String providerName =
        profileRepository
            .findByUserId(s.getProvider().getId())
            .map(p -> p.getName())
            .orElseGet(() -> s.getProvider().getEmail());

    return ServiceResponseDTO.builder()
        .id(s.getId())
        .name(s.getName())
        .description(s.getDescription())
        .price(s.getPrice())
        .durationMinutes(s.getDurationMinutes())
        .active(s.isActive())
        .providerId(s.getProvider().getId())
        .providerName(providerName)
        .build();
  }

  public record Input(java.util.UUID providerId, org.springframework.data.domain.Pageable pageable) {}
}
