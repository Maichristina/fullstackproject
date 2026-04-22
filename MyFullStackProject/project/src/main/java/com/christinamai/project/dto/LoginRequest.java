package com.christinamai.project.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank
    @Email                        // ← validate email format
    private String email;         // ← was username

    @NotBlank
    private String password;
}