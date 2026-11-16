
package com.example.employeeapi.config;

import com.example.employeeapi.entity.AppUser;
import com.example.employeeapi.entity.Role;
import com.example.employeeapi.repository.AppUserRepository;
import com.example.employeeapi.repository.RoleRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initializeData(
            RoleRepository roleRepository,
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.username:}") String adminUsername,
            @Value("${app.admin.password:}") String adminPassword,
            @Value("${app.user.username:}") String userUsername,
            @Value("${app.user.password:}") String userPassword) {

        return args -> {

            // Create default roles
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() ->
                            roleRepository.save(new Role("ROLE_ADMIN")));

            Role userRole = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() ->
                            roleRepository.save(new Role("ROLE_USER")));

            // Create initial admin account
            if (!adminUsername.isBlank()
                    && !adminPassword.isBlank()
                    && !appUserRepository.existsByUsername(adminUsername)) {

                AppUser admin = new AppUser();

                admin.setUsername(adminUsername);
                admin.setPassword(
                        passwordEncoder.encode(adminPassword)
                );
                admin.setEnabled(true);
                admin.getRoles().add(adminRole);

                appUserRepository.save(admin);
            }

            // Create initial user account
            if (!userUsername.isBlank()
                    && !userPassword.isBlank()
                    && !appUserRepository.existsByUsername(userUsername)) {

                AppUser user = new AppUser();

                user.setUsername(userUsername);
                user.setPassword(
                        passwordEncoder.encode(userPassword)
                );
                user.setEnabled(true);
                user.getRoles().add(userRole);

                appUserRepository.save(user);
            }
        };
    }
}
