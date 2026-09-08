package com.ewaste.client.session;

import java.time.LocalDateTime;

/**
 * Manages the current user session state
 */
public class UserSession {

    private static UserSession instance;

    private Long userId;
    private String fullName;
    private String email;
    private String role;
    private String token;
    private LocalDateTime loginTime;
    private boolean isAuthenticated;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void startSession(Long userId, String fullName, String email, String role, String token) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.token = token;
        this.loginTime = LocalDateTime.now();
        this.isAuthenticated = true;
    }

    public void endSession() {
        this.userId = null;
        this.fullName = null;
        this.email = null;
        this.role = null;
        this.token = null;
        this.loginTime = null;
        this.isAuthenticated = false;
    }

    public boolean isAuthenticated() {
        return isAuthenticated && token != null && !token.isEmpty();
    }

    public boolean hasRole(String roleName) {
        return isAuthenticated && role != null && role.equalsIgnoreCase(roleName);
    }

    public boolean isCustomer() {
        return hasRole("CUSTOMER");
    }

    public boolean isCollector() {
        return hasRole("COLLECTOR");
    }

    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    // Getters
    public Long getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getToken() { return token; }
    public LocalDateTime getLoginTime() { return loginTime; }

    @Override
    public String toString() {
        return "UserSession{" +
                "userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", loginTime=" + loginTime +
                ", isAuthenticated=" + isAuthenticated +
                '}';
    }
}