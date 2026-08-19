package com.snipify.snipify.config;

import com.snipify.snipify.Enums.Roles;
import com.snipify.snipify.model.User;
import com.snipify.snipify.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@snipify.com");
            admin.setPassword(passwordEncoder.encode("Admin@12345"));
            admin.setRoles(Set.of(Roles.ADMIN));

            userRepository.save(admin);
            System.out.println("Default Admin Account Created!");
        }
    }
}