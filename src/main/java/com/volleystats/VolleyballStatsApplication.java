package com.volleystats;

import com.volleystats.model.Role;
import com.volleystats.model.User;
import com.volleystats.repository.RoleRepository;
import com.volleystats.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class VolleyballStatsApplication implements CommandLineRunner {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	public static void main(String[] args) {
		SpringApplication.run(VolleyballStatsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		User admin = userRepository.findByUsername("admin").orElse(null);
		if (admin == null) {
			User user = new User();
			user.setUsername("admin");
			user.setPassword(passwordEncoder.encode("admin"));
			user.setEmail("admin@admin.com");

			Set<Role> roles = new HashSet<>();
			Role role = new Role(Role.ERole.ROLE_ADMIN);
			Role adminRole = roleRepository.findByName(Role.ERole.ROLE_ADMIN)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(adminRole);
			user.setRoles(roles);

			userRepository.save(user);
		}
	}
}
