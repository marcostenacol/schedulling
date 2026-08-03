package com.scheduling.modules.service.service;

import com.scheduling.base.service.BaseService;
import com.scheduling.modules.profile.repository.ProfileRepository;
import com.scheduling.modules.service.dto.ServiceResponseDTO;
import com.scheduling.modules.service.model.ServiceOffered;
import com.scheduling.modules.service.repository.ServiceOfferedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/** Catálogo público de serviços ativos — usado pelo cliente para descobrir prestadores. */
@Service
@RequiredArgsConstructor
public class ListPublicServicesService implements BaseService<Pageable, Page<ServiceResponseDTO>> {

  private final ServiceOfferedRepository repository;
  private final ProfileRepository profileRepository;

  @Override
  public Page<ServiceResponseDTO> execute(Pageable pageable) {
    Page<ServiceOffered> services = repository.findByActiveTrue(pageable);

    return services.map(this::toResponseDTO);
  }

  private ServiceResponseDTO toResponseDTO(ServiceOffered s) {
    String providerName =
        profileRepository
            .findByUserId(s.getProvider().getId())
            .map(p -> p.getName())
            .orElse(s.getProvider().getEmail());

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
}
