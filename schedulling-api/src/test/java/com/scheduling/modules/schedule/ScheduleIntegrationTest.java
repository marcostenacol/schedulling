package com.scheduling.modules.schedule;

import static org.assertj.core.api.Assertions.assertThat;

import com.scheduling.AbstractIntegrationTest;
import com.scheduling.modules.auth.enums.RoleEnum;
import com.scheduling.modules.auth.model.Role;
import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.auth.repository.RoleRepository;
import com.scheduling.modules.auth.repository.UserRepository;
import com.scheduling.modules.service.model.ServiceOffered;
import com.scheduling.modules.service.repository.ServiceOfferedRepository;
import com.scheduling.shared.security.JwtService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Teste de integração ponta a ponta do fluxo de agendamento (criar + listar), contra um Postgres
 * real via Testcontainers (ver {@link AbstractIntegrationTest}) — Flyway roda as migrations reais
 * (baseline + incrementais) no container antes de qualquer teste, cobrindo o gap apontado na tarefa
 * de que só existiam testes de Service com mock/`@WebMvcTest` (Controller com Service mockado), sem
 * nenhum teste batendo em endpoint real contra banco real.
 */
class ScheduleIntegrationTest extends AbstractIntegrationTest {

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;
  @Autowired private RoleRepository roleRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private ServiceOfferedRepository serviceOfferedRepository;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private JwtService jwtService;

  private User client;
  private User provider;
  private ServiceOffered serviceOffered;

  @BeforeEach
  void setUp() {
    Role clientRole = findOrCreateRole(RoleEnum.ROLE_CLIENT);
    Role providerRole = findOrCreateRole(RoleEnum.ROLE_PROVIDER);

    client =
        userRepository.save(
            User.builder()
                .email("cliente-" + System.nanoTime() + "@example.com")
                .password(passwordEncoder.encode("senha123"))
                .role(clientRole)
                .build());

    provider =
        userRepository.save(
            User.builder()
                .email("prestador-" + System.nanoTime() + "@example.com")
                .password(passwordEncoder.encode("senha123"))
                .role(providerRole)
                .build());

    serviceOffered =
        serviceOfferedRepository.save(
            ServiceOffered.builder()
                .provider(provider)
                .name("Corte de cabelo")
                .description("Corte simples")
                .price(BigDecimal.valueOf(50))
                .durationMinutes(30)
                .active(true)
                .build());
  }

  private Role findOrCreateRole(RoleEnum name) {
    return roleRepository
        .findByName(name)
        .orElseGet(() -> roleRepository.save(Role.builder().name(name).build()));
  }

  private HttpHeaders authHeadersFor(User user) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(jwtService.generateToken(user));
    return headers;
  }

  private String url(String path) {
    return "http://localhost:" + port + path;
  }

  @Test
  void deveCriarAgendamentoComSucessoParaUmClienteAutenticado() {
    String body =
        """
        {
          "providerId": "%s",
          "serviceId": "%s",
          "startDateTime": "%s"
        }
        """
            .formatted(provider.getId(), serviceOffered.getId(), LocalDateTime.now().plusDays(1));

    HttpEntity<String> request = new HttpEntity<>(body, authHeadersFor(client));

    ResponseEntity<String> response =
        restTemplate.exchange(url("/api/v1/schedules"), HttpMethod.POST, request, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).contains("\"success\":true");
    assertThat(response.getBody()).contains("\"serviceName\":\"Corte de cabelo\"");
  }

  @Test
  void deveListarAgendamentosDoClienteAutenticadoDepoisDeCriado() {
    String body =
        """
        {
          "providerId": "%s",
          "serviceId": "%s",
          "startDateTime": "%s"
        }
        """
            .formatted(provider.getId(), serviceOffered.getId(), LocalDateTime.now().plusDays(2));
    restTemplate.exchange(
        url("/api/v1/schedules"),
        HttpMethod.POST,
        new HttpEntity<>(body, authHeadersFor(client)),
        String.class);

    ResponseEntity<String> response =
        restTemplate.exchange(
            url("/api/v1/schedules/me"),
            HttpMethod.GET,
            new HttpEntity<>(null, authHeadersFor(client)),
            String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).contains("\"success\":true");
    assertThat(response.getBody()).contains("Corte de cabelo");
  }

  @Test
  void deveRetornar400AoCriarAgendamentoSemCamposObrigatorios() {
    HttpEntity<String> request = new HttpEntity<>("{}", authHeadersFor(client));

    ResponseEntity<String> response =
        restTemplate.exchange(url("/api/v1/schedules"), HttpMethod.POST, request, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }
}
