package com.ewaste.server.application.service;

import com.ewaste.server.api.dto.request.LoginRequestDto;
import com.ewaste.server.api.dto.request.RegisterRequestDto;
import com.ewaste.server.api.dto.response.AuthResponseDto;
import com.ewaste.server.common.exception.DuplicateResourceException;
import com.ewaste.server.common.exception.UnauthorizedException;
import com.ewaste.server.common.validation.UserValidator;
import com.ewaste.server.domain.model.user.User;
import com.ewaste.server.domain.model.user.UserRole;
import com.ewaste.server.domain.repository.UserRepository;
import com.ewaste.server.infrastructure.security.JwtProvider;
import com.ewaste.server.infrastructure.security.PasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Verifies authentication flows, password validation, token issuing, and duplicate protection.
 */
class AuthServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtProvider jwtProvider;
    private UserValidator userValidator;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        jwtProvider = Mockito.mock(JwtProvider.class);
        userValidator = Mockito.mock(UserValidator.class);

        authService = new AuthService(userRepository, passwordEncoder, jwtProvider, userValidator);
    }

    @Test
    @DisplayName("Should successfully authenticate valid credentials and issue JWT")
    void testLoginSuccess() {
        LoginRequestDto loginDto = new LoginRequestDto("alex@domain.org", "Secret123!");

        User existingUser = new User();
        existingUser.setUserId(5L);
        existingUser.setEmail("alex@domain.org");
        existingUser.setPasswordHash("hashed_pw");
        existingUser.setRole(UserRole.CUSTOMER);
        existingUser.setFullName("Alex Rivera");

        when(userRepository.findByEmail("alex@domain.org")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("Secret123!", "hashed_pw")).thenReturn(true);
        when(jwtProvider.generateToken(5L, "alex@domain.org", "CUSTOMER")).thenReturn("mocked.jwt.token");

        AuthResponseDto response = authService.login(loginDto);

        assertNotNull(response);
        assertEquals("mocked.jwt.token", response.getToken());
        assertEquals("alex@domain.org", response.getEmail());
        assertEquals("CUSTOMER", response.getRole());
    }

    @Test
    @DisplayName("Should reject invalid password and throw UnauthorizedException")
    void testLoginInvalidPasswordThrowsUnauthorized() {
        LoginRequestDto loginDto = new LoginRequestDto("alex@domain.org", "WrongPassword");

        User existingUser = new User();
        existingUser.setEmail("alex@domain.org");
        existingUser.setPasswordHash("hashed_pw");

        when(userRepository.findByEmail("alex@domain.org")).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches("WrongPassword", "hashed_pw")).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> authService.login(loginDto));
    }

    @Test
    @DisplayName("Should reject duplicate registration email with DuplicateResourceException")
    void testRegisterDuplicateEmailThrowsException() {
        RegisterRequestDto registerDto = new RegisterRequestDto();
        registerDto.setEmail("existing@domain.org");
        registerDto.setPassword("Secret123!");

        when(userRepository.findByEmail("existing@domain.org")).thenReturn(Optional.of(new User()));

        assertThrows(DuplicateResourceException.class, () -> authService.register(registerDto));
        verify(userRepository, never()).save(any());
    }
}