package com.volleystats.config;

import com.volleystats.model.Role;
import com.volleystats.model.Role.ERole;
import com.volleystats.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        // Initialize roles if not exists
        if (roleRepository.count() == 0) {
            System.out.println("Initializing roles...");

            Role userRole = new Role(ERole.ROLE_USER);
            Role modRole = new Role(ERole.ROLE_MODERATOR);
            Role adminRole = new Role(ERole.ROLE_ADMIN);

            roleRepository.saveAll(Arrays.asList(userRole, modRole, adminRole));

            System.out.println("Roles initialized successfully!");
        }
    }
}