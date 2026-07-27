package com.scheduling.modules.schedule.service;

import com.scheduling.modules.auth.enums.RoleEnum;
import com.scheduling.modules.auth.model.Role;
import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.schedule.dto.ScheduleResponseDTO;
import com.scheduling.modules.schedule.model.Schedule;
import com.scheduling.modules.schedule.model.ScheduleStatus;
import com.scheduling.modules.schedule.repository.ScheduleRepository;
import com.scheduling.modules.service.model.ServiceOffered;
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

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListSchedulesServiceTest {

    @Mock
    private ScheduleRepository repository;

    @InjectMocks
    private ListSchedulesService listSchedulesService;

    @Test
    @DisplayName("Deve listar agendamentos como prestador")
    void shouldListSchedulesAsProvider() {
        User provider = User.builder()
                .id(UUID.randomUUID())
                .role(Role.builder().name(RoleEnum.ROLE_PROVIDER).build())
                .build();

        ServiceOffered service = ServiceOffered.builder().name("Serviço").build();
        Schedule schedule = Schedule.builder()
                .id(UUID.randomUUID())
                .client(User.builder().id(UUID.randomUUID()).build())
                .provider(provider)
                .service(service)
                .status(ScheduleStatus.CONFIRMED)
                .build();

        Pageable pageable = PageRequest.of(0, 20);
        when(repository.findByProviderIdAndStatusIn(any(), anyList(), any())).thenReturn(new PageImpl<>(Collections.singletonList(schedule)));

        Page<ScheduleResponseDTO> response = listSchedulesService.execute(new ListSchedulesService.Input(provider, pageable));

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        verify(repository, times(1)).findByProviderIdAndStatusIn(any(), anyList(), any());
    }

    @Test
    @DisplayName("Deve listar agendamentos como cliente")
    void shouldListSchedulesAsClient() {
        User client = User.builder()
                .id(UUID.randomUUID())
                .role(Role.builder().name(RoleEnum.ROLE_CLIENT).build())
                .build();

        ServiceOffered service = ServiceOffered.builder().name("Serviço").build();
        Schedule schedule = Schedule.builder()
                .id(UUID.randomUUID())
                .client(client)
                .provider(User.builder().id(UUID.randomUUID()).build())
                .service(service)
                .status(ScheduleStatus.CONFIRMED)
                .build();

        Pageable pageable = PageRequest.of(0, 20);
        when(repository.findByClientIdAndStatusIn(any(), anyList(), any())).thenReturn(new PageImpl<>(Collections.singletonList(schedule)));

        Page<ScheduleResponseDTO> response = listSchedulesService.execute(new ListSchedulesService.Input(client, pageable));

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
        verify(repository, times(1)).findByClientIdAndStatusIn(any(), anyList(), any());
    }
}
