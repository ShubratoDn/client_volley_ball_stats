package com.volleystats.controller;

import com.volleystats.model.Role;
import com.volleystats.model.User;
import com.volleystats.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public String adminDashboard(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "admin/dashboard";
    }

    @GetMapping("/users/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        user.setPassword(null);

        model.addAttribute("user", user);
        model.addAttribute("allRoles", Role.ERole.values());
        return "admin/edit-user";
    }

    @PostMapping("/users/edit/{id}")
    public String updateUser(@PathVariable Long id, @Valid @ModelAttribute("user") User user,
                             BindingResult bindingResult,
                             @RequestParam(value = "roleNames", required = false) Set<String> roleNames,
                             RedirectAttributes redirectAttributes, Model model) {


        User existingUser = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        // Check for username uniqueness
        if (!existingUser.getUsername().equals(user.getUsername()) &&
                userService.existsByUsername(user.getUsername())) {
            bindingResult.rejectValue("username", "error.username", "Username already taken");
        }

        // Check for email uniqueness
        if (!existingUser.getEmail().equals(user.getEmail()) &&userService.existsByEmail(user.getEmail())) {
            bindingResult.rejectValue("email", "error.email", "Email already in use");
        }

        if (roleNames == null || roleNames.isEmpty()) {
            bindingResult.rejectValue("roles", "error.roles", "Select one or more roles.");
        }

        if (user.getPassword() != null && !user.getPassword().trim().isEmpty() && user.getPassword().length() < 4) {
            bindingResult.rejectValue("password", "error.password", "Password must be at least 4 characters.");
        }

        if (bindingResult.hasErrors()) {
            // Update roles
            if (roleNames != null) {
                Set<Role> newRoles = roleNames.stream()
                        .map(role -> Role.ERole.valueOf("ROLE_" + role.toUpperCase()))
                        .map(erole -> userService.getOrCreateRole(erole))
                        .collect(Collectors.toSet());
                user.setRoles(newRoles);
            }

            model.addAttribute("user", user);
            model.addAttribute("allRoles", Role.ERole.values());
            return "admin/edit-user";
        }

        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            existingUser.setPassword((passwordEncoder.encode(user.getPassword())));
        }

        // Update user details
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());

        // Update roles
        if (roleNames != null) {
            Set<Role> newRoles = roleNames.stream()
                    .map(role -> Role.ERole.valueOf("ROLE_" + role.toUpperCase()))
                    .map(erole -> userService.getOrCreateRole(erole))
                    .collect(Collectors.toSet());
            existingUser.setRoles(newRoles);
        }

        userService.updateUser(existingUser);
        redirectAttributes.addFlashAttribute("successMessage", "User updated successfully");
        return "redirect:/admin";
    }
}