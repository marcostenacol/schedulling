package com.scheduling.modules.availability.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.scheduling.modules.availability.dto.AvailabilityResponseDTO;
import com.scheduling.modules.availability.service.BlockAvailabilityService;
import com.scheduling.modules.availability.service.DeleteAvailabilityService;
import com.scheduling.modules.availability.service.GetAvailableSlotsService;
import com.scheduling.modules.availability.service.ListAvailabilityService;
import com.scheduling.modules.availability.service.SetAvailabilityService;
import com.scheduling.shared.exception.GlobalExceptionHandler;
import com.scheduling.shared.security.AuthFilter;
import com.scheduling.shared.security.JwtService;
import java.time.LocalTime;
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
 * Testes de integração/Controller do módulo de disponibilidade, complementando o gap apontado na
 * tarefa: antes só existia teste de Service (com mock) para a geração de slots ({@code
 * GetAvailableSlotsServiceTest}), sem nenhum teste de Controller para criação/bloqueio/exclusão.
 * Segue o mesmo padrão de {@code ServiceControllerTest}/{@code ScheduleControllerTest}
 * (@WebMvcTest, Service mockado na borda, filtros de segurança desabilitados para isolar
 * validação/serialização do Controller).
 */
@WebMvcTest(controllers = AvailabilityController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AvailabilityControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private SetAvailabilityService setAvailabilityService;
  @MockBean private ListAvailabilityService listAvailabilityService;
  @MockBean private BlockAvailabilityService blockAvailabilityService;
  @MockBean private GetAvailableSlotsService getAvailableSlotsService;
  @MockBean private DeleteAvailabilityService deleteAvailabilityService;
  @MockBean private JwtService jwtService;
  @MockBean private AuthFilter authFilter;

  @Test
  @DisplayName("Deve retornar 400 ao definir disponibilidade sem horário de início/término")
  void shouldReturn400WhenSetAvailabilityBodyIsInvalid() throws Exception {
    mockMvc
        .perform(post("/api/v1/availability").contentType("application/json").content("{}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Deve definir disponibilidade com sucesso quando o corpo é válido")
  void shouldReturn200WhenSetAvailabilityBodyIsValid() throws Exception {
    AvailabilityResponseDTO response =
        AvailabilityResponseDTO.builder()
            .id(UUID.randomUUID())
            .dayOfWeek(1)
            .startTime(LocalTime.of(9, 0))
            .endTime(LocalTime.of(10, 0))
            .active(true)
            .build();
    when(setAvailabilityService.execute(any())).thenReturn(response);

    String body =
        """
        {
          "dayOfWeek": 1,
          "startTime": "09:00:00",
          "endTime": "10:00:00"
        }
        """;

    mockMvc
        .perform(post("/api/v1/availability").contentType("application/json").content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.dayOfWeek").value(1));
  }

  @Test
  @DisplayName("Deve retornar 400 ao bloquear disponibilidade sem início/término")
  void shouldReturn400WhenBlockAvailabilityBodyIsInvalid() throws Exception {
    mockMvc
        .perform(post("/api/v1/availability/block").contentType("application/json").content("{}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Deve bloquear disponibilidade com sucesso quando o corpo é válido")
  void shouldReturn200WhenBlockAvailabilityBodyIsValid() throws Exception {
    String body =
        """
        {
          "startDateTime": "2030-01-01T10:00:00",
          "endDateTime": "2030-01-01T12:00:00",
          "reason": "Consulta médica"
        }
        """;

    mockMvc
        .perform(post("/api/v1/availability/block").contentType("application/json").content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true));

    verify(blockAvailabilityService).execute(any());
  }

  @Test
  @DisplayName("Deve excluir disponibilidade com sucesso")
  void shouldReturn200WhenDeletingAvailability() throws Exception {
    UUID id = UUID.randomUUID();

    mockMvc.perform(delete("/api/v1/availability/" + id)).andExpect(status().isOk());

    verify(deleteAvailabilityService).execute(any());
  }

  @Test
  @DisplayName("Deve retornar slots disponíveis paginados")
  void shouldReturn200WhenGettingAvailableSlots() throws Exception {
    Page<LocalTime> page = new PageImpl<>(List.of(LocalTime.of(9, 0), LocalTime.of(9, 30)));
    when(getAvailableSlotsService.execute(any())).thenReturn(page);

    mockMvc
        .perform(
            get("/api/v1/availability/slots")
                .param("providerId", UUID.randomUUID().toString())
                .param("serviceId", UUID.randomUUID().toString())
                .param("date", "2030-01-01"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.content.length()").value(2));
  }
}
