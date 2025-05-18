package com.volleystats.controller;

import com.volleystats.model.Player;
import com.volleystats.model.Role;
import com.volleystats.model.Team;
import com.volleystats.model.User;
import com.volleystats.repository.PlayerRepository;
import com.volleystats.repository.TeamRepository;
import com.volleystats.repository.TournamentRepository;
import com.volleystats.repository.UserRepository;
import com.volleystats.service.MatchService;
import com.volleystats.service.StatisticService;
import com.volleystats.service.TournamentService;
import com.volleystats.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StatisticService statisticService;
    @Autowired
    private MatchService matchService;
    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private TournamentService tournamentService;
    @Autowired
    private TeamRepository teamRepository;
    @Autowired
    private PlayerRepository playerRepository;

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


    @PostMapping("/users/delete")
    public String delete(@RequestParam(required = true) Long id, RedirectAttributes redirectAttributes, Model model, HttpServletRequest request) {

        User existingUser = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));

        try{
            userRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "User removed successfully");
        }catch(Exception e){
            redirectAttributes.addFlashAttribute("errorMessage", "User removed failed. Maybe the user is associated with other features. <a href='/admin/users/force-delete?id="+id+"'>Click here</a> to remove the user with all data");
            // In your deletePlayer method:
            request.getSession().setAttribute("forceDeleteUserId", id);
        }

        return "redirect:/admin";
    }


    @GetMapping("/users/force-delete")
    public String foreceDeleteUser(@RequestParam(required = false) Long id, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify session token matches URL token
        HttpSession session = request.getSession();
        Long sessionUserId = (Long) session.getAttribute("forceDeleteUserId");

        if (id == null || !id.equals(sessionUserId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid force delete request");
            return "redirect:/admin";
        }

        // Clean up session
        session.removeAttribute("forceDeleteUserId");


        statisticService.deleteStatisticByCreatedBy(user);
        matchService.deleteMatchByCreatedBy(user);
        tournamentService.deleteByCreatedBy(user);
        teamRepository.deleteByCreatedBy(user);
        playerRepository.deleteByCreatedBy(user);
        userRepository.delete(user);

        redirectAttributes.addFlashAttribute("successMessage", "Player deleted successfully!");
        return "redirect:/admin";
    }

}