package com.christinamai.project.service;

import com.christinamai.project.dto.RegisterRequest;
import com.christinamai.project.entity.User;
import com.christinamai.project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository  userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("john");
        testUser.setEmail("john@test.com");
        testUser.setPassword("encoded_password");
        testUser.setRole(User.Role.ROLE_USER);

        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@test.com");
        adminUser.setPassword("encoded_password");
        adminUser.setRole(User.Role.ROLE_ADMIN);
    }

    // ─────────────────────────────────────────────
    // TEST 1: Get all users
    // ─────────────────────────────────────────────
    @Test
    void getAllUsers_ReturnsList() {
        when(userRepository.findAll()).thenReturn(List.of(testUser, adminUser));

        List<User> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    // ─────────────────────────────────────────────
    // TEST 2: Get user by ID — found
    // ─────────────────────────────────────────────
    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("john", result.getUsername());
    }

    // ─────────────────────────────────────────────
    // TEST 3: Get user by ID — not found
    // ─────────────────────────────────────────────
    @Test
    void getUserById_NotFound_ThrowsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.getUserById(99L));

        assertEquals("User not found", ex.getMessage());
    }

    // ─────────────────────────────────────────────
    // TEST 4: Delete user successfully
    // ─────────────────────────────────────────────
    @Test
    void deleteUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        userService.deleteUser(1L, "admin");

        verify(userRepository, times(1)).delete(testUser);
    }

    // ─────────────────────────────────────────────
    // TEST 5: Delete user — not found
    // ─────────────────────────────────────────────
    @Test
    void deleteUser_NotFound_ThrowsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.deleteUser(99L, "admin"));

        assertEquals("User not found", ex.getMessage());
        verify(userRepository, never()).delete(any());
    }

    // ─────────────────────────────────────────────
    // TEST 6: Promote user to admin
    // ─────────────────────────────────────────────
    @Test
    void promoteToAdmin_Success() {
        User promotedUser = new User();
        promotedUser.setId(1L);
        promotedUser.setUsername("john");
        promotedUser.setEmail("john@test.com");
        promotedUser.setPassword("encoded");
        promotedUser.setRole(User.Role.ROLE_ADMIN); // ← promoted!

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(promotedUser);

        User result = userService.promoteToAdmin(1L, "admin");

        assertNotNull(result);
        assertEquals(User.Role.ROLE_ADMIN, result.getRole());
        verify(userRepository, times(1)).save(any());
    }

    // ─────────────────────────────────────────────
    // TEST 7: Get my profile
    // ─────────────────────────────────────────────
    @Test
    void getMyProfile_Success() {
        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(testUser));

        User result = userService.getMyProfile("john");

        assertNotNull(result);
        assertEquals("john",           result.getUsername());
        assertEquals("john@test.com",  result.getEmail());
    }

    // ─────────────────────────────────────────────
    // TEST 8: Update profile — username taken by someone else
    // ─────────────────────────────────────────────
    @Test
    void updateMyProfile_UsernameTaken_ThrowsException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("maria"); // ← different username
        request.setEmail("john@test.com");
        request.setPassword("");

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("maria"))
                .thenReturn(true); // ← taken!

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.updateMyProfile("john", request));

        assertEquals("Username already taken", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    // ─────────────────────────────────────────────
    // TEST 9: Update profile — password updated
    // ─────────────────────────────────────────────
    @Test
    void updateMyProfile_WithNewPassword_EncodesPassword() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setEmail("john@test.com");
        request.setPassword("newpassword123"); // ← new password

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("john");
        updatedUser.setEmail("john@test.com");
        updatedUser.setPassword("new_encoded_password");
        updatedUser.setRole(User.Role.ROLE_USER);

        when(userRepository.findByUsername("john"))
                .thenReturn(Optional.of(testUser));

        when(passwordEncoder.encode("newpassword123"))
                .thenReturn("new_encoded_password");
        when(userRepository.save(any(User.class)))
                .thenReturn(updatedUser);

        User result = userService.updateMyProfile("john", request);

        // Password should have been encoded
        verify(passwordEncoder, times(1)).encode("newpassword123");
        assertNotNull(result);
    }
}