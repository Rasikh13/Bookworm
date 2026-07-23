package com.bookworm.service;

import com.bookworm.dto.LoginRequest;
import com.bookworm.entity.User;

public interface UserService {

    /*
     * Register a new user
     */
    User registerUser(User user);

    /*
     * Login existing user
     */
    String loginUser(LoginRequest request);

}