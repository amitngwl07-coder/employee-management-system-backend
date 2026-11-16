
package com.example.employeeapi.service;

import com.example.employeeapi.dto.LoginRequest;
import com.example.employeeapi.dto.LoginResponse;
import com.example.employeeapi.dto.RefreshTokenRequest;
import com.example.employeeapi.entity.AppUser;
import com.example.employeeapi.entity.RefreshToken;
import com.example.employeeapi.repository.AppUserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AppUserRepository appUserRepository;

    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            AppUserRepository appUserRepository) {

        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.appUserRepository = appUserRepository;
    }

    public LoginResponse login(LoginRequest request) {

        // Verify username and password
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Load authenticated user details
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(request.getUsername());

        // Generate access token
        String accessToken = jwtService.generateToken(userDetails);

        // Find application user
        AppUser appUser = appUserRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new RuntimeException("User not found.")
                );

        // Generate refresh token
        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(appUser);

        return new LoginResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                jwtExpirationMs
        );
    }


    public LoginResponse refreshAccessToken(RefreshTokenRequest request) {

        // Find refresh token in database
        RefreshToken refreshToken =
                refreshTokenService.findByToken(request.getRefreshToken());

        // Verify refresh token expiration
        refreshTokenService.verifyExpiration(refreshToken);

        // Get associated application user
        AppUser appUser = refreshToken.getUser();

        // Load user details
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(appUser.getUsername());

        // Generate new access token
        String accessToken = jwtService.generateToken(userDetails);

        // Generate and save a new refresh token (rotation)
        RefreshToken newRefreshToken =
                refreshTokenService.createRefreshToken(appUser);

        // Return both tokens
        return new LoginResponse(
                accessToken,
                newRefreshToken.getToken(),
                "Bearer",
                jwtExpirationMs
        );
    }

    @Transactional
    public void logout(String username) {

        // Find application user
        AppUser appUser = appUserRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found.")
                );

        // Delete user's refresh token
        refreshTokenService.deleteByUser(appUser);
    }

}
