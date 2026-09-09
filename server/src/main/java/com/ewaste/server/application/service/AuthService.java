package com.ewaste.server.application.service;

import com.ewaste.server.api.dto.request.LoginRequestDto;
import com.ewaste.server.api.dto.request.RegisterRequestDto;
import com.ewaste.server.api.dto.response.AuthResponseDto;
import com.ewaste.server.domain.model.user.User;
import com.ewaste.server.domain.model.user.Role;
import com.ewaste.server.domain.model.collector.Collector;
import com.ewaste.server.domain.model.collector.VehicleType;
import com.ewaste.server.domain.repository.CollectorRepository;
import com.ewaste.server.domain.repository.UserRepository;
import com.ewaste.server.infrastructure.security.JwtProvider;
import com.ewaste.server.infrastructure.security.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final CollectorRepository collectorRepository;

    public AuthService(UserRepository userRepository,
                       JwtProvider tokenProvider,
                       PasswordEncoder passwordEncoder,
                       CollectorRepository collectorRepository) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.collectorRepository = collectorRepository;
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
        String token = tokenProvider.generateToken(user.getUserId(), user.getEmail(), user.getRole().name());

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
        user.setRole(Role.valueOf(registerRequest.getRole().toUpperCase()));

        // Save user
        User savedUser = userRepository.save(user);

        if (savedUser.getRole() == Role.COLLECTOR) {
            Collector collector = new Collector();
            collector.setUserId(savedUser.getUserId());
            collector.setVehicleType(resolveVehicleType(registerRequest.getVehicleType()));
            collector.setMaxCapacityKg(registerRequest.getMaxCapacityKg() != null
                    ? registerRequest.getMaxCapacityKg() : 500.0);
            collector.setCurrentWorkloadKg(0.0);
            collector.setAvailable(true);
            collectorRepository.save(collector);
        }

        // Generate JWT token
        String token = tokenProvider.generateToken(savedUser.getUserId(), savedUser.getEmail(), savedUser.getRole().name());

        // Return auth response
        return new AuthResponseDto(
                token,
                savedUser.getUserId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getRole().name()
        );
    }

    private VehicleType resolveVehicleType(String vehicleType) {
        if (vehicleType == null || vehicleType.isBlank()) {
            return VehicleType.VAN;
        }
        return VehicleType.fromString(vehicleType);
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
        Long userId = tokenProvider.getUserId(token);

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