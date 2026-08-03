package com.scheduling.modules.service.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.scheduling.modules.service.dto.ServiceResponseDTO;
import com.scheduling.modules.service.service.CreateServiceService;
import com.scheduling.modules.service.service.DeleteServiceService;
import com.scheduling.modules.service.service.ListPublicServicesService;
import com.scheduling.modules.service.service.ListServicesService;
import com.scheduling.modules.service.service.UpdateServiceService;
import com.scheduling.shared.exception.GlobalExceptionHandler;
import com.scheduling.shared.security.AuthFilter;
import com.scheduling.shared.security.JwtService;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Teste de integração/Controller da criação de serviço, complementando o gap de cobertura HTTP
 * apontado em .claude/rules/testing.md (antes só AuthControllerTest cobria a camada de Controller).
 * Serviços mockados na borda; filtros de segurança desabilitados para isolar validação/serialização
 * do Controller.
 */
@WebMvcTest(controllers = ServiceController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ServiceControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private CreateServiceService createServiceService;
  @MockBean private ListServicesService listServicesService;
  @MockBean private UpdateServiceService updateServiceService;
  @MockBean private DeleteServiceService deleteServiceService;
  @MockBean private ListPublicServicesService listPublicServicesService;
  @MockBean private JwtService jwtService;
  @MockBean private AuthFilter authFilter;

  @Test
  @DisplayName("Deve retornar 400 ao criar serviço sem nome/preço/duração")
  void shouldReturn400WhenCreateServiceBodyIsInvalid() throws Exception {
    mockMvc
        .perform(post("/api/v1/services").contentType("application/json").content("{}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Deve criar serviço com sucesso quando o corpo é válido")
  void shouldReturn200WhenCreateServiceBodyIsValid() throws Exception {
    ServiceResponseDTO response =
        ServiceResponseDTO.builder()
            .id(UUID.randomUUID())
            .name("Corte de cabelo")
            .description("Corte simples")
            .price(BigDecimal.valueOf(50))
            .durationMinutes(30)
            .active(true)
            .providerId(UUID.randomUUID())
            .build();
    when(createServiceService.execute(any())).thenReturn(response);

    String body =
        """
                {
                  "name": "Corte de cabelo",
                  "description": "Corte simples",
                  "price": 50,
                  "durationMinutes": 30
                }
                """;

    mockMvc
        .perform(post("/api/v1/services").contentType("application/json").content(body))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.name").value("Corte de cabelo"));
  }

  @Test
  @DisplayName("Deve retornar 400 ao criar serviço com preço negativo")
  void shouldReturn400WhenPriceIsNegative() throws Exception {
    String body =
        """
                {
                  "name": "Corte de cabelo",
                  "price": -10,
                  "durationMinutes": 30
                }
                """;

    mockMvc
        .perform(post("/api/v1/services").contentType("application/json").content(body))
        .andExpect(status().isBadRequest());
  }
}
