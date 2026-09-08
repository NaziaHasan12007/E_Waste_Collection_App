package com.ewaste.client.dto.request;

public class LoginClientRequest {

    private String email;
    private String password;

    public LoginClientRequest() {}

    public LoginClientRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginClientRequest{" +
                "email='" + email + '\'' +
                '}';
    }
}