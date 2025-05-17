package com.volleystats.controller;

import com.volleystats.model.User;
import com.volleystats.service.MatchService;
import com.volleystats.service.PlayerService;
import com.volleystats.service.TeamService;
import com.volleystats.service.UserService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        // Check if user is already authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "register";
        }

        if (userService.existsByUsername(user.getUsername())) {
            bindingResult.rejectValue("username", "error.username", "Username is already taken");
            return "register";
        }

        if (userService.existsByEmail(user.getEmail())) {
            bindingResult.rejectValue("email", "error.email", "Email is already in use");
            return "register";
        }

        // Create new user's account
        try {
            Set<String> roles = new HashSet<>();

            userService.registerUser(
                    user.getUsername(),
                    user.getEmail(),
                    user.getPassword(),
                    roles
            );

            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please log in.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Registration failed: " + e.getMessage());
            return "redirect:/register";
        }
    }

    @Autowired
    private PlayerService playerService;
    @Autowired
    private TeamService teamService;
    @Autowired
    private MatchService matchService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/login";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);
        model.addAttribute("roles", userDetails.getAuthorities());

        Long totalPlayers = Optional.ofNullable(playerService.countAllPlayers()).orElse(0L);
        Long totalTeams = Optional.ofNullable(teamService.countAllTeams()).orElse(0L);
        Long totalMatches = Optional.ofNullable(matchService.countAllMatches()).orElse(0L);
        model.addAttribute("totalPlayers", totalPlayers);
        model.addAttribute("totalTeams", totalTeams);
        model.addAttribute("totalMatches", totalTeams);

        return "dashboard";
    }


    @GetMapping("/error/403")
    public String accessDenied() {
        return "error/403";
    }
}