package com.scheduling.modules.schedule.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.scheduling.modules.schedule.dto.ScheduleResponseDTO;
import com.scheduling.modules.schedule.model.ScheduleStatus;
import com.scheduling.modules.schedule.service.CreateScheduleService;
import com.scheduling.modules.schedule.service.ListSchedulesService;
import com.scheduling.modules.schedule.service.UpdateScheduleStatusService;
import com.scheduling.shared.exception.GlobalExceptionHandler;
import com.scheduling.shared.security.AuthFilter;
import com.scheduling.shared.security.JwtService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Testes de integração/Controller do fluxo de agendamento (criação e listagem), complementando o
 * gap apontado em .claude/rules/testing.md: até então só AuthControllerTest cobria a camada HTTP
 * (validação de login). Serviços mockados na borda (padrão já usado em AuthControllerTest); filtros
 * de segurança desabilitados para isolar o comportamento do Controller/DTO.
 */
@WebMvcTest(controllers = ScheduleController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ScheduleControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private CreateScheduleService createScheduleService;
  @MockBean private ListSchedulesService listSchedulesService;
  @MockBean private UpdateScheduleStatusService updateScheduleStatusService;
  @MockBean private JwtService jwtService;
  @MockBean private AuthFilter authFilter;

  @Test
  @DisplayName("Deve retornar 400 ao criar agendamento sem os campos obrigatórios")
  void shouldReturn400WhenCreateScheduleBodyIsInvalid() throws Exception {
    mockMvc
        .perform(post("/api/v1/schedules").contentType("application/json").content("{}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Deve criar agendamento com sucesso quando o corpo é válido")
  void shouldReturn200WhenCreateScheduleBodyIsValid() throws Exception {
    ScheduleResponseDTO response =
        ScheduleResponseDTO.builder()
            .id(UUID.randomUUID())
            .clientId(UUID.randomUUID())
            .providerId(UUID.randomUUID())
            .serviceName("Corte de cabelo")
            .startDateTime(LocalDateTime.now().plusDays(1))
            .endDateTime(LocalDateTime.now().plusDays(1).plusMinutes(30))
            .status(ScheduleStatus.PENDING)
            .price(BigDecimal.TEN)
            .build();
    when(createScheduleService.execute(any())).thenReturn(response);

    String body =
        """
                {
                  "providerId": "%s",
                  "serviceId": "%s",
                  "startDateTime": "2030-01-01T10:00:00"
                }
                """
            .formatted(UUID.randomUUID(), UUID.randomUUID());

    mockMvc
        .perform(post("/api/v1/schedules").contentType("application/json").content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.serviceName").value("Corte de cabelo"));
  }

  @Test
  @DisplayName("Deve listar os agendamentos do usuário autenticado")
  void shouldReturn200WhenListingSchedules() throws Exception {
    Page<ScheduleResponseDTO> page = new PageImpl<>(List.of());
    when(listSchedulesService.execute(any())).thenReturn(page);

    mockMvc
        .perform(get("/api/v1/schedules/me"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));
  }
}
