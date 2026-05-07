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

        when(repository.findByProviderIdAndStatusIn(any(), anyList())).thenReturn(Collections.singletonList(schedule));

        List<ScheduleResponseDTO> response = listSchedulesService.execute(provider);

        assertNotNull(response);
        assertEquals(1, response.size());
        verify(repository, times(1)).findByProviderIdAndStatusIn(any(), anyList());
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

        when(repository.findByClientIdAndStatusIn(any(), anyList())).thenReturn(Collections.singletonList(schedule));

        List<ScheduleResponseDTO> response = listSchedulesService.execute(client);

        assertNotNull(response);
        assertEquals(1, response.size());
        verify(repository, times(1)).findByClientIdAndStatusIn(any(), anyList());
    }
}
