
package com.example.employeeapi.service;

import com.example.employeeapi.dto.UserProfileResponse;
import com.example.employeeapi.entity.AppUser;
import com.example.employeeapi.repository.AppUserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class UserProfileService {

    private final AppUserRepository appUserRepository;

    public UserProfileService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public UserProfileResponse getCurrentUserProfile() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found: " + username
                        ));

        String role = appUser.getRoles()
                .stream()
                .map(roleEntity -> roleEntity.getName())
                .collect(Collectors.joining(", "));

        String status = appUser.isEnabled()
                ? "ACTIVE"
                : "INACTIVE";

        return new UserProfileResponse(
                appUser.getUsername(),
                role,
                status
        );
    }
}
