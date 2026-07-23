package com.bookworm.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.bookworm.entity.Role;
import com.bookworm.entity.User;
import com.bookworm.service.UserService;
import com.bookworm.dto.LoginRequest;
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {

    	System.out.println(">>> REGISTER API HIT <<<");
        user.setRole(Role.CUSTOMER);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        return userService.registerUser(user);
    }
    /*
     * Login API
     */
    @PostMapping("/login")
    public String loginUser(@RequestBody LoginRequest request) {

        // Call service layer
        return userService.loginUser(request);

    }
    
}