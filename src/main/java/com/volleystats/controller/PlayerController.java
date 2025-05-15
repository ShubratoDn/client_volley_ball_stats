package com.volleystats.controller;

import com.volleystats.model.Player;
import com.volleystats.model.User;
import com.volleystats.service.PlayerService;
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

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/players")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String listPlayers(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Player> players = playerService.findByUser(user);

        model.addAttribute("players", players);
        model.addAttribute("user", user);
        model.addAttribute("roles", userDetails.getAuthorities());

        return "players/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("player", new Player());
        model.addAttribute("user", user);
        model.addAttribute("roles", userDetails.getAuthorities());

        return "players/create";
    }

    @PostMapping("/create")
    public String createPlayer(@Valid @ModelAttribute("player") Player player,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("roles", userDetails.getAuthorities());
            return "players/create";
        }

        player.setCreatedBy(user);
        player.setCreatedAt(LocalDateTime.now());
        player.setUpdatedAt(LocalDateTime.now());

        playerService.createPlayer(player);

        redirectAttributes.addFlashAttribute("successMessage", "Player created successfully!");
        return "redirect:/players";
    }

    @GetMapping("/{id}")
    public String viewPlayer(@PathVariable("id") Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Player player = playerService.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        model.addAttribute("player", player);
        model.addAttribute("user", user);
        model.addAttribute("roles", userDetails.getAuthorities());

        return "players/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Player player = playerService.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        // Check if the user is the creator of the player or an admin
        if (!player.getCreatedBy().getId().equals(user.getId()) &&
                !userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/error/403";
        }

        model.addAttribute("player", player);
        model.addAttribute("user", user);
        model.addAttribute("roles", userDetails.getAuthorities());

        return "players/edit";
    }

    @PostMapping("/{id}/edit")
    public String updatePlayer(@PathVariable("id") Long id,
                               @Valid @ModelAttribute("player") Player playerUpdates,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Player existingPlayer = playerService.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        // Check if the user is the creator of the player or an admin
        if (!existingPlayer.getCreatedBy().getId().equals(user.getId()) &&
                !userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/error/403";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("roles", userDetails.getAuthorities());
            return "players/edit";
        }

        // Update player fields
        existingPlayer.setName(playerUpdates.getName());
        existingPlayer.setDateOfBirth(playerUpdates.getDateOfBirth());
        existingPlayer.setHeight(playerUpdates.getHeight());
        existingPlayer.setWeight(playerUpdates.getWeight());
        existingPlayer.setPosition(playerUpdates.getPosition());
        existingPlayer.setNationality(playerUpdates.getNationality());
        existingPlayer.setJerseyNumber(playerUpdates.getJerseyNumber());
        existingPlayer.setProfileImage(playerUpdates.getProfileImage());
        existingPlayer.setNotes(playerUpdates.getNotes());
        existingPlayer.setUpdatedAt(LocalDateTime.now());

        playerService.updatePlayer(existingPlayer);

        redirectAttributes.addFlashAttribute("successMessage", "Player updated successfully!");
        return "redirect:/players/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deletePlayer(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Player player = playerService.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        // Check if the user is the creator of the player or an admin
        if (!player.getCreatedBy().getId().equals(user.getId()) &&
                !userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/error/403";
        }

        playerService.deletePlayer(id);

        redirectAttributes.addFlashAttribute("successMessage", "Player deleted successfully!");
        return "redirect:/players";
    }

    @GetMapping("/search")
    public String searchPlayers(@RequestParam("name") String name, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Player> players = playerService.searchByName(name);

        model.addAttribute("players", players);
        model.addAttribute("searchTerm", name);
        model.addAttribute("user", user);
        model.addAttribute("roles", userDetails.getAuthorities());

        return "players/list";
    }
}