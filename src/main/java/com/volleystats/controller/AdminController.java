package com.volleystats.controller;

import com.volleystats.model.Role;
import com.volleystats.model.User;
import com.volleystats.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

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

        model.addAttribute("user", user);
        model.addAttribute("allRoles", Role.ERole.values());
        return "admin/edit-user";
    }

    @PostMapping("/users/edit/{id}")
    public String updateUser(@PathVariable Long id, @Valid @ModelAttribute("user") User user,
                             BindingResult bindingResult,
                             @RequestParam(value = "roles", required = false) Set<String> roles,
                             RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "admin/edit-user";
        }

        User existingUser = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        // Check for username uniqueness
        if (!existingUser.getUsername().equals(user.getUsername()) &&
                userService.existsByUsername(user.getUsername())) {
            bindingResult.rejectValue("username", "error.username", "Username already taken");
            return "admin/edit-user";
        }

        // Check for email uniqueness
        if (!existingUser.getEmail().equals(user.getEmail()) &&
                userService.existsByEmail(user.getEmail())) {
            bindingResult.rejectValue("email", "error.email", "Email already in use");
            return "admin/edit-user";
        }

        // Update user details
        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());

        // Update roles
        if (roles != null) {
            Set<Role> newRoles = roles.stream()
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