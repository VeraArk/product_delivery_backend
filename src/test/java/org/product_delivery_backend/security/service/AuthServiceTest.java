package org.product_delivery_backend.security.service;

import io.jsonwebtoken.Claims;
import jakarta.security.auth.message.AuthException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.product_delivery_backend.dto.userDto.UserResponseDto;
import org.product_delivery_backend.mapper.UserMapper;
import org.product_delivery_backend.security.dto.AuthResponse;
import org.product_delivery_backend.security.dto.LoginRequestDto;
import org.product_delivery_backend.security.dto.RefreshRequestDto;
import org.product_delivery_backend.security.dto.TokenResponseDto;
import org.product_delivery_backend.service.UserService;
import org.product_delivery_backend.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private TokenService tokenService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void testLogin_Successful() throws AuthException {
        // Arrange
        LoginRequestDto loginRequestDto = new LoginRequestDto("john.doe@example.com", "password");
        User user = new User();// Mock user entity
        user.setEmail("john.doe@example.com");
        user.setPassword("encodedPassword");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("john.doe@example.com");
        when(userDetails.getPassword()).thenReturn("encodedPassword");

        when(userDetailsService.loadUserByUsername("john.doe@example.com")).thenReturn(userDetails);
        when(userService.getUserByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);

        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        when(tokenService.generateAccessToken(userDetails)).thenReturn(accessToken);
        when(tokenService.generateRefreshToken(userDetails)).thenReturn(refreshToken);

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setEmail("john.doe@example.com");
        when(userMapper.toResponse(user)).thenReturn(userResponseDto);

        // Act
        AuthResponse response = authService.login(loginRequestDto);

        // Assert
        assertNotNull(response);
        assertEquals(accessToken, response.getToken().getAccessToken());
        assertEquals(refreshToken, response.getToken().getRefreshToken());
        assertEquals("john.doe@example.com", response.getUser().getEmail());
    }

    @Test
    void testLogin_UserNotFound() {
        // Arrange
        LoginRequestDto loginRequestDto = new LoginRequestDto("john.doe@example.com", "password");
        when(userService.getUserByEmail("john.doe@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        AuthException exception = assertThrows(AuthException.class, () -> authService.login(loginRequestDto));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testLogin_IncorrectPassword() {
        // Arrange
        LoginRequestDto loginRequestDto = new LoginRequestDto("john.doe@example.com", "wrongPassword");
        User user = new User();
        user.setEmail("john.doe@example.com");
        user.setPassword("encodedPassword");

        UserDetails userDetails = mock(UserDetails.class);
        lenient().when(userDetails.getUsername()).thenReturn("john.doe@example.com");
        lenient().when(userDetails.getPassword()).thenReturn("encodedPassword");

        lenient().when(userDetailsService.loadUserByUsername("john.doe@example.com")).thenReturn(userDetails);
        lenient().when(userService.getUserByEmail("john.doe@example.com")).thenReturn(Optional.of(user));
        lenient().when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // Act & Assert
        AuthException exception = assertThrows(AuthException.class, () -> authService.login(loginRequestDto));
        assertEquals("Incorrect login and/or password", exception.getMessage());


    }

    @Test
    void testRefreshAccessToken_Successful() throws AuthException {
        // Arrange
        RefreshRequestDto refreshRequestDto = new RefreshRequestDto("refreshToken");
        Claims claims = mock(Claims.class);
        lenient().when(claims.getSubject()).thenReturn("john.doe@example.com");
        lenient().when(tokenService.validateRefreshToken("refreshToken")).thenReturn(true);
        lenient().when(tokenService.getRefreshClaims("refreshToken")).thenReturn(claims);

        UserDetails userDetails = mock(UserDetails.class);
        lenient().when(userDetails.getUsername()).thenReturn("john.doe@example.com");
        lenient().when(userDetailsService.loadUserByUsername("john.doe@example.com")).thenReturn(userDetails);

        Map<String, String> refreshStorage = new HashMap<>();
        refreshStorage.put("john.doe@example.com", "refreshToken");

        ReflectionTestUtils.setField(authService, "refreshStorage", refreshStorage);

        String newAccessToken = "newAccessToken";
        lenient().when(tokenService.generateAccessToken(userDetails)).thenReturn(newAccessToken);

        // Act
        TokenResponseDto response = authService.refreshAccessToken(refreshRequestDto);

        // Assert
        assertNotNull(response);
        assertEquals(newAccessToken, response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
    }

}