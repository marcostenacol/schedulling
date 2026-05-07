package com.scheduling.modules.auth.controller;

import com.scheduling.base.controller.BaseController;
import com.scheduling.base.traits.ApiResponse;
import com.scheduling.modules.auth.dto.LoginDTO;
import com.scheduling.modules.auth.dto.RegisterDTO;
import com.scheduling.modules.auth.dto.TokenResponseDTO;
import com.scheduling.modules.auth.service.LoginService;
import com.scheduling.modules.auth.service.RegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final RegisterService registerService;
    private final LoginService loginService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterDTO registerDTO) {
        registerService.execute(registerDTO);
        return success("Usuário registrado com sucesso", null);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponseDTO>> login(@Valid @RequestBody LoginDTO loginDTO) {
        TokenResponseDTO response = loginService.execute(loginDTO);
        return success("Login realizado com sucesso", response);
    }
}
