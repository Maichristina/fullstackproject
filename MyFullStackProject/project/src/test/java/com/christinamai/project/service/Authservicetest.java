package com.christinamai.project.service;

import com.christinamai.project.dto.AuthResponse;
import com.christinamai.project.dto.LoginRequest;
import com.christinamai.project.dto.RegisterRequest;
import com.christinamai.project.entity.User;
import com.christinamai.project.repository.UserRepository;
import com.christinamai.project.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository       userRepository;
    @Mock private PasswordEncoder      passwordEncoder;
    @Mock private JwtUtils             jwtUtils;
    @Mock private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest    loginRequest;
    private User            testUser;

    @BeforeEach
    void setUp() {

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("christina");
        registerRequest.setEmail("christina@test.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("christina@test.com");
        loginRequest.setPassword("password123");


        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("christina");
        testUser.setEmail("christina@test.com");
        testUser.setPassword("$2a$10$encodedPassword");
        testUser.setRole(User.Role.ROLE_USER);
    }


    @Test
    void register_Success() {

        when(userRepository.existsByUsername("christina")).thenReturn(false);
        when(userRepository.existsByEmail("christina@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtils.generateToken("christina", "ROLE_USER")).thenReturn("fake.jwt.token");


        AuthResponse result = authService.register(registerRequest);


        assertNotNull(result);
        assertEquals("fake.jwt.token", result.getToken());
        assertEquals("christina",      result.getUsername());
        assertEquals("ROLE_USER",      result.getRole());

        verify(passwordEncoder, times(1)).encode("password123");

        verify(userRepository, times(1)).save(any(User.class));
    }


    @Test
    void register_UsernameTaken_ThrowsException() {

        when(userRepository.existsByUsername("christina")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.register(registerRequest));

        assertEquals("Username already taken", ex.getMessage());


        verify(userRepository, never()).save(any());
    }


    @Test
    void register_EmailTaken_ThrowsException() {

        when(userRepository.existsByUsername("christina")).thenReturn(false);
        when(userRepository.existsByEmail("christina@test.com")).thenReturn(true);


        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.register(registerRequest));

        assertEquals("Email already registered", ex.getMessage());
        verify(userRepository, never()).save(any());
    }


    @Test
    void login_Success() {

        when(userRepository.findByEmail("christina@test.com"))
                .thenReturn(Optional.of(testUser));

        Authentication mockAuth = mock(Authentication.class);


        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);


        when(jwtUtils.generateToken(eq("christina"), eq("ROLE_USER")))
                .thenReturn("fake.jwt.token");


        AuthResponse result = authService.login(loginRequest);


        assertNotNull(result);
        assertEquals("fake.jwt.token", result.getToken());
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void login_EmailNotFound_ThrowsException() {

        when(userRepository.findByEmail("christina@test.com"))
                .thenReturn(Optional.empty());


        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.login(loginRequest));

        assertEquals("Email not found", ex.getMessage());


        verify(authenticationManager, never()).authenticate(any());
    }


    @Test
    void login_WrongPassword_ThrowsException() {

        when(userRepository.findByEmail("christina@test.com"))
                .thenReturn(Optional.of(testUser));
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class,
                () -> authService.login(loginRequest));


        verify(jwtUtils, never()).generateToken(anyString(), anyString());
    }


    @Test
    void register_NewUser_AlwaysGetsRoleUser() {

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtils.generateToken(anyString(), anyString())).thenReturn("token");


        AuthResponse result = authService.register(registerRequest);


        assertEquals("ROLE_USER", result.getRole());
    }
}