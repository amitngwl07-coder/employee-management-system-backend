
package com.example.employeeapi.controller;

import com.example.employeeapi.dto.LoginRequest;
import com.example.employeeapi.dto.LoginResponse;
import com.example.employeeapi.dto.RefreshTokenRequest;
import com.example.employeeapi.service.AuthService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {

        LoginResponse response =
                authService.refreshAccessToken(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            Authentication authentication) {

        authService.logout(authentication.getName());

        return ResponseEntity.ok("Logged out successfully.");
    }
}
