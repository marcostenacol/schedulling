package com.scheduling.modules.admin.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.scheduling.modules.admin.service.ListAllUsersService;
import com.scheduling.modules.auth.enums.RoleEnum;
import com.scheduling.modules.auth.model.Role;
import com.scheduling.modules.auth.model.User;
import com.scheduling.shared.exception.GlobalExceptionHandler;
import com.scheduling.shared.security.AuthFilter;
import com.scheduling.shared.security.JwtService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Testes de integração/Controller da listagem administrativa de usuários, seguindo o mesmo padrão
 * de {@code ServiceControllerTest}/{@code ScheduleControllerTest} (@WebMvcTest com Service mockado,
 * {@code addFilters = false} para isolar do filtro real de autenticação). Diferente daqueles, aqui
 * o objetivo inclui comprovar que {@code @PreAuthorize("hasRole('ADMIN')")} bloqueia quem não é
 * admin — para isso habilita method security explicitamente ({@code @EnableMethodSecurity} nesta
 * classe de teste) e simula a identidade via {@code @WithMockUser} (que popula o {@code
 * SecurityContext} diretamente, sem depender do filtro real de JWT).
 */
@WebMvcTest(controllers = AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
@EnableMethodSecurity
@Import(GlobalExceptionHandler.class)
class AdminControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private ListAllUsersService listAllUsersService;
  @MockBean private JwtService jwtService;
  @MockBean private AuthFilter authFilter;

  @Test
  @DisplayName("Deve listar usuários paginados quando o requisitante é admin")
  @WithMockUser(roles = "ADMIN")
  void shouldReturn200WhenRequesterIsAdmin() throws Exception {
    User user =
        User.builder()
            .email("cliente@example.com")
            .password("hash")
            .role(Role.builder().name(RoleEnum.ROLE_CLIENT).build())
            .createdAt(LocalDateTime.now())
            .build();
    Page<User> page = new PageImpl<>(List.of(user));
    when(listAllUsersService.execute(any())).thenReturn(page);

    mockMvc
        .perform(get("/api/v1/admin/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.content[0].email").value("cliente@example.com"));
  }

  @Test
  @DisplayName("Deve retornar 403 quando o requisitante não é admin")
  @WithMockUser(roles = "CLIENT")
  void shouldReturn403WhenRequesterIsNotAdmin() throws Exception {
    mockMvc.perform(get("/api/v1/admin/users")).andExpect(status().isForbidden());
  }
}
