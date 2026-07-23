package com.bookworm.dto;

/*
 * DTO (Data Transfer Object)
 * This class is used to receive only the login data
 * from the frontend (email & password).
 */
public class LoginRequest {

    // User's email entered on login page
    private String email;

    // User's password entered on login page
    private String password;

    // Default Constructor
    public LoginRequest() {
    }

    // Parameterized Constructor
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getter for Email
    public String getEmail() {
        return email;
    }

    // Setter for Email
    public void setEmail(String email) {
        this.email = email;
    }

    // Getter for Password
    public String getPassword() {
        return password;
    }

    // Setter for Password
    public void setPassword(String password) {
        this.password = password;
    }
}