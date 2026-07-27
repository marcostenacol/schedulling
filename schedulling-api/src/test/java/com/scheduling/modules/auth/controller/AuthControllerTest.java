package com.scheduling.modules.auth.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.scheduling.modules.auth.service.LoginService;
import com.scheduling.modules.auth.service.RefreshTokenService;
import com.scheduling.modules.auth.service.RegisterService;
import com.scheduling.shared.exception.GlobalExceptionHandler;
import com.scheduling.shared.security.AuthFilter;
import com.scheduling.shared.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Testes de validação de entrada do AuthController#login. Cobre o gap de teste de
 * integração/Controller apontado como débito técnico (só havia testes unitários de Service antes).
 * Filtros de segurança desabilitados (addFilters = false) para isolar a validação de @Valid/Bean
 * Validation do fluxo de autenticação em si.
 */
@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private RegisterService registerService;
  @MockBean private LoginService loginService;
  @MockBean private RefreshTokenService refreshTokenService;
  @MockBean private JwtService jwtService;
  @MockBean private AuthFilter authFilter;

  @Test
  @DisplayName("Deve retornar 400 ao logar com corpo vazio (sem email/senha)")
  void shouldReturn400WhenLoginBodyIsEmpty() throws Exception {
    mockMvc
        .perform(post("/auth/login").contentType("application/json").content("{}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Deve retornar 400 ao logar com email inválido")
  void shouldReturn400WhenEmailIsInvalid() throws Exception {
    mockMvc
        .perform(
            post("/auth/login")
                .contentType("application/json")
                .content("{\"email\":\"not-an-email\",\"password\":\"123456\"}"))
        .andExpect(status().isBadRequest());
  }
}
