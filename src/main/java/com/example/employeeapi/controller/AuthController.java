
package com.example.employeeapi.controller;

import com.example.employeeapi.dto.LoginRequest;
import com.example.employeeapi.dto.LoginResponse;
import com.example.employeeapi.dto.RefreshTokenRequest;
import com.example.employeeapi.dto.UserProfileResponse;
import com.example.employeeapi.service.AuthService;

import com.example.employeeapi.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserProfileService userProfileService;

    public AuthController(
            AuthService authService,
            UserProfileService userProfileService) {

        this.authService = authService;
        this.userProfileService = userProfileService;
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

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile() {

        UserProfileResponse response =
                userProfileService.getCurrentUserProfile();

        return ResponseEntity.ok(response);
    }
}
