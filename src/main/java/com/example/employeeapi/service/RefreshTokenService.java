
package com.example.employeeapi.service;

import com.example.employeeapi.entity.AppUser;
import com.example.employeeapi.entity.RefreshToken;
import com.example.employeeapi.exception.InvalidRefreshTokenException;
import com.example.employeeapi.repository.RefreshTokenRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token.expiration-ms}")
    private long refreshTokenExpirationMs;

    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public RefreshToken createRefreshToken(AppUser user) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByUser(user)
                .orElseGet(RefreshToken::new);

        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);

        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        refreshToken.setUser(user);
        refreshToken.setToken(token);
        refreshToken.setExpiryDate(
                Instant.now().plusMillis(refreshTokenExpirationMs)
        );

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {

        if (token.getExpiryDate().isBefore(Instant.now())) {

            refreshTokenRepository.deleteByToken(token.getToken());

            throw new InvalidRefreshTokenException(
                    "Refresh token has expired. Please sign in again."
            );
        }

        return token;
    }

    public RefreshToken findByToken(String token) {

        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() ->
                        new InvalidRefreshTokenException(
                                "Invalid refresh token."
                        )
                );
    }

    @Transactional
    public void deleteByUser(AppUser user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
