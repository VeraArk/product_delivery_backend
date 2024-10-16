package org.product_delivery_backend.security.controller;

import jakarta.security.auth.message.AuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.product_delivery_backend.dto.userDto.UserResponseDto;
import org.product_delivery_backend.security.dto.AuthResponse;
import org.product_delivery_backend.security.dto.LoginRequestDto;
import org.product_delivery_backend.security.dto.RefreshRequestDto;
import org.product_delivery_backend.security.dto.TokenResponseDto;
import org.product_delivery_backend.security.service.AuthService;
import org.product_delivery_backend.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testLoginSuccess() throws AuthException {
        // Arrange
        var loginRequestDto = new LoginRequestDto("testuser", "password");

        var tokenResponseDto = new TokenResponseDto();
        tokenResponseDto.setAccessToken("accessToken");
        tokenResponseDto.setRefreshToken("refreshToken");

        var userResponseDto = new UserResponseDto();
        userResponseDto.setEmail("testuser@example.com");

        var expectedResponse = new AuthResponse(tokenResponseDto, userResponseDto);
        when(authService.login(loginRequestDto)).thenReturn(expectedResponse);

        // Act
        AuthResponse actualResponse = authController.login(loginRequestDto);

        // Assert
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getToken().getAccessToken(), actualResponse.getToken().getAccessToken());
        assertEquals(expectedResponse.getToken().getRefreshToken(), actualResponse.getToken().getRefreshToken());
        assertEquals(expectedResponse.getUser().getEmail(), actualResponse.getUser().getEmail());
        verify(authService, times(1)).login(loginRequestDto);
    }

    @Test
    void testLoginThrowsException() throws AuthException {
        var loginRequestDto = new LoginRequestDto("testuser", "password");
        when(authService.login(loginRequestDto)).thenThrow(new AuthException("Authentication failed"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authController.login(loginRequestDto);
        });
        assertEquals("Authentication failed", exception.getCause().getMessage());
        verify(authService, times(1)).login(loginRequestDto);
    }

    @Test
    void testRefreshAccessTokenSuccess() throws AuthException {
        var refreshRequestDto = new RefreshRequestDto("validRefreshToken");

        var expectedResponse = new TokenResponseDto();
        expectedResponse.setAccessToken("newAccessToken");
        expectedResponse.setRefreshToken("newRefreshToken");

        when(authService.refreshAccessToken(refreshRequestDto)).thenReturn(expectedResponse);

        TokenResponseDto actualResponse = authController.refreshAccessToken(refreshRequestDto);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getAccessToken(), actualResponse.getAccessToken());
        assertEquals(expectedResponse.getRefreshToken(), actualResponse.getRefreshToken());
        verify(authService, times(1)).refreshAccessToken(refreshRequestDto);
    }

    @Test
    void testRefreshAccessTokenThrowsException() throws AuthException {

        var refreshRequestDto = new RefreshRequestDto("invalidRefreshToken");
        when(authService.refreshAccessToken(refreshRequestDto)).thenThrow(new AuthException("Token refresh failed"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authController.refreshAccessToken(refreshRequestDto);
        });
        assertEquals("Token refresh failed", exception.getCause().getMessage());
        verify(authService, times(1)).refreshAccessToken(refreshRequestDto);
    }

    @Test
    void testGetUserProfileSuccess() {

        String email = "testuser@example.com";
        UserResponseDto expectedUserResponse = new UserResponseDto();
        expectedUserResponse.setEmail(email);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(email);
        when(userService.getUserProfileByEmail(email)).thenReturn(expectedUserResponse);

        UserResponseDto actualUserResponse = authController.getUserProfile();

        assertNotNull(actualUserResponse);
        assertEquals(expectedUserResponse.getEmail(), actualUserResponse.getEmail());
        verify(userService, times(1)).getUserProfileByEmail(email);
    }
}