package com.scheduling.modules.service.service;

import com.scheduling.base.service.BaseService;
import com.scheduling.modules.service.dto.CreateServiceRequest;
import com.scheduling.modules.service.dto.ServiceResponseDTO;
import com.scheduling.modules.service.model.ServiceOffered;
import com.scheduling.modules.service.repository.ServiceOfferedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateServiceService implements BaseService<CreateServiceRequest, ServiceResponseDTO> {

    private final ServiceOfferedRepository repository;

    @Override
    @CacheEvict(value = "services", key = "#input.provider.id")
    public ServiceResponseDTO execute(CreateServiceRequest input) {
        ServiceOffered serviceOffered = ServiceOffered.builder()
                .provider(input.getProvider())
                .name(input.getData().getName())
                .description(input.getData().getDescription())
                .price(input.getData().getPrice())
                .durationMinutes(input.getData().getDurationMinutes())
                .active(true)
                .build();

        ServiceOffered saved = repository.save(serviceOffered);

        return ServiceResponseDTO.builder()
                .id(saved.getId())
                .name(saved.getName())
                .description(saved.getDescription())
                .price(saved.getPrice())
                .durationMinutes(saved.getDurationMinutes())
                .active(saved.isActive())
                .providerId(saved.getProvider().getId())
                .build();
    }
}
