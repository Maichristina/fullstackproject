//AuthService handles two jobs:
//
//register():
//├── check no duplicate username/email
//├── encode password with BCrypt      ← NEVER save plain password!
//├── save new User with ROLE_USER
//├── generate token immediately
//└── return token (user is logged in!)
//
//login():
//├── authManager verifies credentials  ← Spring does the hard work
//├── if wrong → automatic 401 error
//├── if correct → get username + role
//├── generate token
//└── return token
package com.christinamai.project.service;

import com.christinamai.project.dto.AuthResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.server.ResponseStatusException;
import com.christinamai.project.dto.LoginRequest;
import com.christinamai.project.dto.RegisterRequest;
import com.christinamai.project.entity.User;
import com.christinamai.project.repository.UserRepository;
import com.christinamai.project.security.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // BCrypt from SecurityConfig

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    //Input: RegisterRequest (username, email, password)
    // Output: AuthResponse (token, username, role, message
    public AuthResponse register(RegisterRequest request) {

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already taken");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // All new users get ROLE_USER by default
        user.setRole(User.Role.ROLE_USER);

        // Save to database
        userRepository.save(user);

        logger.info("New user registered: {}", request.getUsername());

        // Generate token immediately so user is logged in after register
        String token = jwtUtils.generateToken(
                user.getEmail(),
                user.getRole().name()



        );

        //Return everything the frontend needs
        return new AuthResponse(
                token,
                user.getEmail(),
                user.getRole().name(),


                "Registration successful"
        );
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid credentials"  // ← 401, not RuntimeException
                ));

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtUtils.generateToken(user.getEmail(),user.getRole().name() );
        return new AuthResponse(
                token, user.getEmail(),user.getRole().name(),  "Login successful");
    }
}