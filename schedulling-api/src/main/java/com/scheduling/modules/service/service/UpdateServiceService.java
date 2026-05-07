package com.scheduling.modules.service.service;

import com.scheduling.base.service.BaseService;
import com.scheduling.modules.service.dto.ServiceResponseDTO;
import com.scheduling.modules.service.dto.UpdateServiceRequest;
import com.scheduling.modules.service.model.ServiceOffered;
import com.scheduling.modules.service.repository.ServiceOfferedRepository;
import com.scheduling.shared.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateServiceService implements BaseService<UpdateServiceRequest, ServiceResponseDTO> {

    private final ServiceOfferedRepository repository;

    @Override
    @CacheEvict(value = "services", key = "#input.provider.id")
    public ServiceResponseDTO execute(UpdateServiceRequest input) {
        ServiceOffered service = repository.findById(input.getServiceId())
                .orElseThrow(() -> new AppException("Serviço não encontrado", HttpStatus.NOT_FOUND));

        if (!service.getProvider().getId().equals(input.getProvider().getId())) {
            throw new AppException("Você não tem permissão para editar este serviço", HttpStatus.FORBIDDEN);
        }

        if (input.getData().getName() != null) service.setName(input.getData().getName());
        if (input.getData().getDescription() != null) service.setDescription(input.getData().getDescription());
        if (input.getData().getPrice() != null) service.setPrice(input.getData().getPrice());
        if (input.getData().getDurationMinutes() != null) service.setDurationMinutes(input.getData().getDurationMinutes());
        if (input.getData().getActive() != null) service.setActive(input.getData().getActive());

        ServiceOffered updated = repository.save(service);

        return ServiceResponseDTO.builder()
                .id(updated.getId())
                .name(updated.getName())
                .description(updated.getDescription())
                .price(updated.getPrice())
                .durationMinutes(updated.getDurationMinutes())
                .active(updated.isActive())
                .providerId(updated.getProvider().getId())
                .build();
    }
}
