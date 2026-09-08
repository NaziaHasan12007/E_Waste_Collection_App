package com.ewaste.client.session;

/**
 * Thread-safe singleton tracking the current authenticated user identity,
 * active authorization role, and JWT token for the client runtime.
 */
public final class UserSession {

    private static volatile UserSession instance;

    private Long userId;
    private String email;
    private String fullName;
    private String role;
    private String authToken;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            synchronized (UserSession.class) {
                if (instance == null) {
                    instance = new UserSession();
                }
            }
        }
        return instance;
    }

    public synchronized void setSession(Long userId, String email, String fullName, String role, String authToken) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.role = (role != null) ? role.toUpperCase() : null;
        this.authToken = authToken;
    }

    public synchronized void clear() {
        this.userId = null;
        this.email = null;
        this.fullName = null;
        this.role = null;
        this.authToken = null;
    }

    public synchronized boolean isAuthenticated() {
        return authToken != null && !authToken.isBlank();
    }

    public synchronized Long getUserId() {
        return userId;
    }

    public synchronized String getEmail() {
        return email;
    }

    public synchronized String getUserName() {
        return fullName != null ? fullName : email;
    }

    public synchronized String getFullName() {
        return fullName;
    }

    public synchronized String getRole() {
        return role;
    }

    public synchronized String getAuthToken() {
        return authToken;
    }

    public synchronized boolean isCustomer() {
        return "CUSTOMER".equalsIgnoreCase(this.role);
    }

    public synchronized boolean isCollector() {
        return "COLLECTOR".equalsIgnoreCase(this.role);
    }

    public synchronized boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.role);
    }
}