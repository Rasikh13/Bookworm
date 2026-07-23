package com.bookworm.service.impl;
import com.bookworm.exception.EmailAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.bookworm.dto.LoginRequest;
import com.bookworm.entity.User;
import com.bookworm.repository.UserRepository;
import com.bookworm.service.UserService;
import com.bookworm.exception.EmailAlreadyExistsException;
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public User registerUser(User user) {

        // Check whether a user with the same email already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {

            // If email already exists, stop registration and throw an exception
        	throw new EmailAlreadyExistsException("Email already exists.");
        }

        // Encrypt the user's password before saving it to the database
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save the user in the database
        return userRepository.save(user);
    }
    @Override
    public String loginUser(LoginRequest request) {

        // Find user using email
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        // If email is not found
        if (user == null) {
            return "Invalid Email";
        }

        // Compare entered password with encrypted password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return "Invalid Password";
        }

        // Login successful
        return "Login Successful";
    }

}