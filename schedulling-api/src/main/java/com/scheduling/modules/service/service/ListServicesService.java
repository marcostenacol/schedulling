package com.scheduling.modules.service.service;

import com.scheduling.base.service.BaseService;
import com.scheduling.modules.service.dto.ServiceResponseDTO;
import com.scheduling.modules.service.model.ServiceOffered;
import com.scheduling.modules.service.repository.ServiceOfferedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListServicesService implements BaseService<ListServicesService.Input, Page<ServiceResponseDTO>> {

    private final ServiceOfferedRepository repository;

    @Override
    public Page<ServiceResponseDTO> execute(Input input) {
        Page<ServiceOffered> services = repository.findByProviderIdAndActiveTrue(input.providerId(), input.pageable());

        return services.map(s -> ServiceResponseDTO.builder()
                .id(s.getId())
                .name(s.getName())
                .description(s.getDescription())
                .price(s.getPrice())
                .durationMinutes(s.getDurationMinutes())
                .active(s.isActive())
                .providerId(s.getProvider().getId())
                .build());
    }

    public record Input(UUID providerId, Pageable pageable) {
    }
}
