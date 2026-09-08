package com.ewaste.server.api.mapper;

import com.ewaste.server.api.dto.request.RegisterRequestDto;
import com.ewaste.server.api.dto.response.AuthResponseDto;
import com.ewaste.server.domain.model.user.Role;
import com.ewaste.server.domain.model.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    /**
     * Convert RegisterRequestDto to User entity
     */
    public User toEntity(RegisterRequestDto dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(dto.getPassword()); // Will be hashed by service
        user.setRole(Role.valueOf(dto.getRole().toUpperCase()));

        return user;
    }

    /**
     * Convert User entity to AuthResponseDto
     */
    public AuthResponseDto toAuthResponse(User user, String token) {
        if (user == null) {
            return null;
        }

        return new AuthResponseDto(
                token,
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    /**
     * Convert User entity to a simplified response (for non-auth responses)
     */
    public UserResponseDto toResponse(User user) {
        if (user == null) {
            return null;
        }

        UserResponseDto dto = new UserResponseDto();
        dto.setUserId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().name());
        dto.setCreatedAt(user.getCreatedAt());

        return dto;
    }

    // Inner class for user response
    public static class UserResponseDto {
        private Long userId;
        private String fullName;
        private String email;
        private String role;
        private Timestamp createdAt;

        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public Timestamp getCreatedAt() { return createdAt; }
        public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    }
}