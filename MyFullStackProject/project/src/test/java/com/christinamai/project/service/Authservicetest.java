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
        // Fake register request
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("christina");
        registerRequest.setEmail("christina@test.com");
        registerRequest.setPassword("password123");

        // Fake login request
        loginRequest = new LoginRequest();
        loginRequest.setEmail("christina@test.com");
        loginRequest.setPassword("password123");

        // Fake user in DB
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("christina");
        testUser.setEmail("christina@test.com");
        testUser.setPassword("$2a$10$encodedPassword");
        testUser.setRole(User.Role.ROLE_USER);
    }

    // ─────────────────────────────────────────────
    // TEST 1: Register successfully
    // ─────────────────────────────────────────────
    @Test
    void register_Success() {
        // GIVEN: username and email not taken, save works
        when(userRepository.existsByUsername("christina")).thenReturn(false);
        when(userRepository.existsByEmail("christina@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtils.generateToken("christina", "ROLE_USER")).thenReturn("fake.jwt.token");

        // WHEN
        AuthResponse result = authService.register(registerRequest);

        // THEN
        assertNotNull(result);
        assertEquals("fake.jwt.token", result.getToken());
        assertEquals("christina",      result.getUsername());
        assertEquals("ROLE_USER",      result.getRole());

        // Verify password was encoded
        verify(passwordEncoder, times(1)).encode("password123");
        // Verify user was saved
        verify(userRepository, times(1)).save(any(User.class));
    }

    // ─────────────────────────────────────────────
    // TEST 2: Register — username already taken
    // ─────────────────────────────────────────────
    @Test
    void register_UsernameTaken_ThrowsException() {
        // GIVEN: username already exists
        when(userRepository.existsByUsername("christina")).thenReturn(true);

        // WHEN + THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.register(registerRequest));

        assertEquals("Username already taken", ex.getMessage());

        // Verify we never tried to save
        verify(userRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────
    // TEST 3: Register — email already taken
    // ─────────────────────────────────────────────
    @Test
    void register_EmailTaken_ThrowsException() {
        // GIVEN: username free but email taken
        when(userRepository.existsByUsername("christina")).thenReturn(false);
        when(userRepository.existsByEmail("christina@test.com")).thenReturn(true);

        // WHEN + THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.register(registerRequest));

        assertEquals("Email already registered", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────
    // TEST 4: Login successfully
    // ─────────────────────────────────────────────
    @Test
    void login_Success() {
        // 1. Προετοιμασία: Ο χρήστης υπάρχει στη βάση
        when(userRepository.findByEmail("christina@test.com"))
                .thenReturn(Optional.of(testUser));

        // 2. Προετοιμασία: Ο authenticationManager εγκρίνει τη σύνδεση
        // Φτιάχνουμε ένα mock Authentication για να το επιστρέψει ο manager
        Authentication mockAuth = mock(Authentication.class);

        // Εδώ είναι το κλειδί: Χρησιμοποιούμε any() για να είμαστε σίγουροι ότι θα "πιάσει" την κλήση
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuth);

        // 3. Προετοιμασία: Το JWT παράγεται κανονικά
        // Χρησιμοποιούμε lenient() αν συνεχίζει να γκρινιάζει, αλλά με το σωστό flow δεν θα χρειαστεί
        when(jwtUtils.generateToken(eq("christina"), eq("ROLE_USER")))
                .thenReturn("fake.jwt.token");

        // Execution
        AuthResponse result = authService.login(loginRequest);

        // Assertions
        assertNotNull(result);
        assertEquals("fake.jwt.token", result.getToken());
        verify(authenticationManager).authenticate(any()); // Επιβεβαίωση ότι καλέστηκε [cite: 33]
    }
    // ─────────────────────────────────────────────
    // TEST 5: Login — email not found
    // ─────────────────────────────────────────────
    @Test
    void login_EmailNotFound_ThrowsException() {
        // GIVEN: email doesn't exist in DB
        when(userRepository.findByEmail("christina@test.com"))
                .thenReturn(Optional.empty());

        // WHEN + THEN
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.login(loginRequest));

        assertEquals("Email not found", ex.getMessage());

        // Verify authManager was NEVER called
        verify(authenticationManager, never()).authenticate(any());
    }

    // ─────────────────────────────────────────────
    // TEST 6: Login — wrong password
    // ─────────────────────────────────────────────
    @Test
    void login_WrongPassword_ThrowsException() {
        // GIVEN: email found but authManager throws (wrong password)
        when(userRepository.findByEmail("christina@test.com"))
                .thenReturn(Optional.of(testUser));
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // WHEN + THEN
        assertThrows(BadCredentialsException.class,
                () -> authService.login(loginRequest));

        // Verify token was NEVER generated
        verify(jwtUtils, never()).generateToken(anyString(), anyString());
    }

    // ─────────────────────────────────────────────
    // TEST 7: Register — new user always gets ROLE_USER
    // ─────────────────────────────────────────────
    @Test
    void register_NewUser_AlwaysGetsRoleUser() {
        // GIVEN
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtils.generateToken(anyString(), anyString())).thenReturn("token");

        // WHEN
        AuthResponse result = authService.register(registerRequest);

        // THEN: role is always ROLE_USER never ROLE_ADMIN
        assertEquals("ROLE_USER", result.getRole());
    }
}