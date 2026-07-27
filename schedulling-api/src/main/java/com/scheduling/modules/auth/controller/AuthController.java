package com.scheduling.modules.auth.controller;

import com.scheduling.base.controller.BaseController;
import com.scheduling.base.traits.ApiResponse;
import com.scheduling.modules.auth.dto.LoginDTO;
import com.scheduling.modules.auth.dto.RefreshTokenRequestDTO;
import com.scheduling.modules.auth.dto.RegisterDTO;
import com.scheduling.modules.auth.dto.TokenResponseDTO;
import com.scheduling.modules.auth.model.RefreshToken;
import com.scheduling.modules.auth.service.LoginService;
import com.scheduling.modules.auth.service.RefreshTokenService;
import com.scheduling.modules.auth.service.RegisterService;
import com.scheduling.shared.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final RegisterService registerService;
    private final LoginService loginService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

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

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponseDTO>> refresh(@Valid @RequestBody RefreshTokenRequestDTO request) {
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(request.getRefreshToken());
        String newAccessToken = jwtService.generateToken(refreshToken.getUser());

        TokenResponseDTO response = TokenResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken.getToken())
                .build();

        return success("Token renovado com sucesso", response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails instanceof com.scheduling.modules.auth.model.User user) {
            refreshTokenService.deleteByUser(user);
        }
        return success("Logout realizado com sucesso", null);
    }
}
