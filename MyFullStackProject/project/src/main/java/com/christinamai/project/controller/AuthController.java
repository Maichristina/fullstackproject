//Ο χρήστης στέλνει username/password στον AuthController.
//
//Ο Controller τα δίνει στον AuthService.
//
//Ο Service ελέγχει τη βάση και "χτυπάει" το password με το BCrypt.
//
//Αν όλα είναι οκ, ο JwtUtils φτιάχνει ένα Token.
//
//Ο Controller επιστρέφει το Token στον χρήστη με κωδικό 200 OK.
package com.christinamai.project.controller;

import com.christinamai.project.dto.AuthResponse;
import com.christinamai.project.dto.LoginRequest;
import com.christinamai.project.dto.RegisterRequest;
import com.christinamai.project.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // TEMPORARY TEST ENDPOINT — no security needed
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Controller is working!");
    }

}
