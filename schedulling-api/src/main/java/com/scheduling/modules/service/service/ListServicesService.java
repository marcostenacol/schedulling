package com.scheduling.modules.service.service;

import com.scheduling.base.service.BaseService;
import com.scheduling.modules.service.dto.ServiceResponseDTO;
import com.scheduling.modules.service.model.ServiceOffered;
import com.scheduling.modules.service.repository.ServiceOfferedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListServicesService implements BaseService<UUID, List<ServiceResponseDTO>> {

    private final ServiceOfferedRepository repository;

    @Override
    @Cacheable(value = "services", key = "#providerId")
    public List<ServiceResponseDTO> execute(UUID providerId) {
        List<ServiceOffered> services = repository.findByProviderIdAndActiveTrue(providerId);

        return services.stream().map(s -> ServiceResponseDTO.builder()
                .id(s.getId())
                .name(s.getName())
                .description(s.getDescription())
                .price(s.getPrice())
                .durationMinutes(s.getDurationMinutes())
                .active(s.isActive())
                .providerId(s.getProvider().getId())
                .build()).collect(Collectors.toList());
    }
}
