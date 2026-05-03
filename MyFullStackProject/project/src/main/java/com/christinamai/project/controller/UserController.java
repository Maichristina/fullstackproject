//Frontend sends request
//        │
//        ▼
//Controller  ← WE ARE HERE
//"I receive the request and
// pass it to the right Service method"
//        │
//        ▼
//Service (does the logic)
//        │
//        ▼
//Repository (talks to DB)
package com.christinamai.project.controller;

import com.christinamai.project.dto.RegisterRequest;
import com.christinamai.project.entity.User;
import com.christinamai.project.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // any website can send requests here
public class UserController {

    @Autowired
    private UserService userService;

    // GET /api/users — admin sees all users
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // GET /api/users/σ{id} — admin gets any user by id
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // DELETE /api/users/{id} — admin deletes any user
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deleteUser(
            @PathVariable Long id,
            Authentication authentication) {
        userService.deleteUser(id, authentication.getName());
        return ResponseEntity.ok("User deleted successfully");
    }

    // PUT /api/users/{id}/promote — admin promotes user to admin
    @PutMapping("/{id}/promote")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<User> promoteToAdmin( //Αλλάζει το status ενός χρήστη σε Admin.
            @PathVariable Long id,
            Authentication authentication) {
        return ResponseEntity.ok(
                userService.promoteToAdmin(id, authentication.getName()));
    }

    // GET /api/users/me — user gets their own profile
    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(Authentication authentication) {
        return ResponseEntity.ok(
                userService.getMyProfile(authentication.getName()));
    }

    // PUT /api/users/me — user updates their own profile
    @PutMapping("/me")
    public ResponseEntity<User> updateMyProfile(
            @Valid @RequestBody RegisterRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(
                userService.updateMyProfile(authentication.getName(), request));
    }
}