
package com.example.employeeapi.repository;

import com.example.employeeapi.entity.RefreshToken;
import com.example.employeeapi.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(AppUser user);

    void deleteByUser(AppUser user);

    void deleteByToken(String token);
}
