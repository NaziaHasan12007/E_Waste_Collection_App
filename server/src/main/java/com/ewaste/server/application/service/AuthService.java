package com.ewaste.server.application.service;

import com.ewaste.server.api.dto.request.LoginRequestDto;
import com.ewaste.server.api.dto.request.RegisterRequestDto;
import com.ewaste.server.api.dto.response.AuthResponseDto;
import com.ewaste.server.domain.model.user.User;
import com.ewaste.server.domain.repository.UserRepository;
import com.ewaste.server.infrastructure.security.JwtTokenProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       JwtTokenProvider tokenProvider,
                       BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticate user with email and password
     */
    public AuthResponseDto login(LoginRequestDto loginRequest) {
        // Find user by email
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());

        if (userOptional.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }

        User user = userOptional.get();

        // Verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Generate JWT token
        String token = tokenProvider.generateToken(user.getUserId(), user.getEmail(), user.getRole());

        // Return auth response
        return new AuthResponseDto(
                token,
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    /**
     * Register a new user
     */
    @Transactional
    public AuthResponseDto register(RegisterRequestDto registerRequest) {
        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Create new user
        User user = new User();
        user.setFullName(registerRequest.getFullName());
        user.setEmail(registerRequest.getEmail());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(User.Role.valueOf(registerRequest.getRole().toUpperCase()));

        // Save user
        User savedUser = userRepository.save(user);

        // Generate JWT token
        String token = tokenProvider.generateToken(savedUser.getUserId(), savedUser.getEmail(), savedUser.getRole());

        // Return auth response
        return new AuthResponseDto(
                token,
                savedUser.getUserId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getRole().name()
        );
    }

    /**
     * Validate token and get user
     */
    public User validateTokenAndGetUser(String token) {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Token is required");
        }

        // Remove "Bearer " prefix if present
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Validate token
        if (!tokenProvider.validateToken(token)) {
            throw new RuntimeException("Invalid or expired token");
        }

        // Get user ID from token
        Long userId = tokenProvider.getUserIdFromToken(token);

        // Find user
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Logout user (invalidate token - client-side handling)
     */
    public void logout(String token) {
        // Token invalidation is handled client-side
        // Server can maintain a blacklist if needed
    }
}