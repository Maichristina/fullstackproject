package com.christinamai.project.service;

import com.christinamai.project.dto.RegisterRequest;
import com.christinamai.project.entity.User;
import com.christinamai.project.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ADMIN — get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ADMIN — get one user by id
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ADMIN — delete any user
    public void deleteUser(Long id, String adminUsername) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user);
        logger.info("Admin {} deleted user {}", adminUsername, user.getUsername());
    }

    // ADMIN — promote user to admin
    public User promoteToAdmin(Long id, String adminUsername) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRole(User.Role.ROLE_ADMIN);
        User updated = userRepository.save(user);
        logger.info("Admin {} promoted user {} to ROLE_ADMIN",
                adminUsername, user.getUsername());

        return updated;
    }

    // USER — get their own profile
    public User getMyProfile(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // USER — update their own profile
    public User updateMyProfile(String email, RegisterRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if new username is taken by someone else
        if (!user.getUsername().equals(request.getUsername()) &&
                userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        // Check if new email is taken by someone else
        if (!user.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        // Only update password if a new one was provided
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updated = userRepository.save(user);
        logger.info("User {} updated their profile", email);

        return updated;
    }
}