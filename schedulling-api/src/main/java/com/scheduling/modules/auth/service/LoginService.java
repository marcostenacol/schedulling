package com.scheduling.modules.auth.service;

import com.scheduling.base.service.BaseService;
import com.scheduling.modules.auth.dto.LoginDTO;
import com.scheduling.modules.auth.dto.TokenResponseDTO;
import com.scheduling.modules.auth.model.RefreshToken;
import com.scheduling.modules.auth.model.User;
import com.scheduling.modules.auth.repository.UserRepository;
import com.scheduling.shared.exception.AppException;
import com.scheduling.shared.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService implements BaseService<LoginDTO, TokenResponseDTO> {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    @Override
    public TokenResponseDTO execute(LoginDTO input) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            input.getEmail(),
                            input.getPassword()
                    )
            );
        } catch (Exception e) {
            log.warn("Falha de autenticação para email={}", input.getEmail());
            throw new AppException("Credenciais inválidas", HttpStatus.UNAUTHORIZED);
        }

        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new AppException("Usuário não encontrado", HttpStatus.NOT_FOUND));

        String jwtToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        log.info("Login bem-sucedido para usuário id={}", user.getId());

        return TokenResponseDTO.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }
}
