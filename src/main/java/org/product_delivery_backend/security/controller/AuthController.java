package org.product_delivery_backend.security.controller;


import jakarta.security.auth.message.AuthException;
import org.product_delivery_backend.security.dto.AuthResponse;
import org.product_delivery_backend.security.dto.LoginRequestDto;
import org.product_delivery_backend.security.dto.RefreshRequestDto;
import org.product_delivery_backend.security.dto.TokenResponseDto;
import org.product_delivery_backend.security.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequestDto loginRequestDto) {
        try {
            return authService.login(loginRequestDto);
        } catch (AuthException e) {
            throw new RuntimeException(e);
        }
    }
    @PostMapping("/refresh")
    public TokenResponseDto refreshAccessToken(@RequestBody RefreshRequestDto refreshRequestDto) {

        try {
            return authService.refreshAccessToken(refreshRequestDto);
        } catch (AuthException e) {
            throw new RuntimeException(e);
        }
    }


}
