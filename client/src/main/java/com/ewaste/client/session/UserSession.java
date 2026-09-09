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

    // ========== GETTERS ==========
    public Long getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getToken() { return token; }
    public LocalDateTime getLoginTime() { return loginTime; }

    // ========== SETTERS (Added for ApiClient compatibility) ==========

    /**
     * Set the authentication token
     * Used for token validation and temporary token switching
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Set user ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * Set full name
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Set email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Set user role
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Set authentication status
     */
    public void setAuthenticated(boolean authenticated) {
        this.isAuthenticated = authenticated;
    }

    /**
     * Set login time
     */
    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    // ========== ADDITIONAL HELPER METHODS ==========

    /**
     * Check if session is active (not expired)
     * Session expires after 8 hours
     */
    public boolean isSessionActive() {
        if (!isAuthenticated() || loginTime == null) return false;
        LocalDateTime expiryTime = loginTime.plusHours(8);
        return LocalDateTime.now().isBefore(expiryTime);
    }

    /**
     * Refresh session to extend expiry
     */
    public void refreshSession() {
        this.loginTime = LocalDateTime.now();
    }

    /**
     * Get user's display name (full name or email)
     */
    public String getDisplayName() {
        return fullName != null && !fullName.isBlank() ? fullName : email;
    }

    /**
     * Clear sensitive data (token and email) for security
     */
    public void clearSensitiveData() {
        this.token = null;
        this.email = null;
    }

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