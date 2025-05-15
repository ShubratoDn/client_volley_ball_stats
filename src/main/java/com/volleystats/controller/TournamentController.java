package com.volleystats.controller;

import com.volleystats.model.Tournament;
import com.volleystats.model.User;
import com.volleystats.service.TournamentService;
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
@RequestMapping("/tournaments")
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String listTournaments(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Tournament> tournaments = tournamentService.findByUser(user);

        model.addAttribute("tournaments", tournaments);
        model.addAttribute("user", user);
        model.addAttribute("roles", userDetails.getAuthorities());

        return "tournaments/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("tournament", new Tournament());
        model.addAttribute("user", user);
        model.addAttribute("roles", userDetails.getAuthorities());

        return "tournaments/create";
    }

    @PostMapping("/create")
    public String createTournament(@Valid @ModelAttribute("tournament") Tournament tournament,
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
            return "tournaments/create";
        }

        tournament.setCreatedBy(user);
        tournament.setCreatedAt(LocalDateTime.now());
        tournament.setUpdatedAt(LocalDateTime.now());

        tournamentService.createTournament(tournament);

        redirectAttributes.addFlashAttribute("successMessage", "Tournament created successfully!");
        return "redirect:/tournaments";
    }

    @GetMapping("/{id}")
    public String viewTournament(@PathVariable("id") Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Tournament tournament = tournamentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        model.addAttribute("tournament", tournament);
        model.addAttribute("user", user);
        model.addAttribute("roles", userDetails.getAuthorities());

        return "tournaments/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Tournament tournament = tournamentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        // Check if the user is the creator of the tournament or an admin
        if (!tournament.getCreatedBy().getId().equals(user.getId()) &&
                !userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/error/403";
        }

        model.addAttribute("tournament", tournament);
        model.addAttribute("user", user);
        model.addAttribute("roles", userDetails.getAuthorities());

        return "tournaments/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateTournament(@PathVariable("id") Long id,
                                   @Valid @ModelAttribute("tournament") Tournament tournamentUpdates,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Tournament existingTournament = tournamentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        // Check if the user is the creator of the tournament or an admin
        if (!existingTournament.getCreatedBy().getId().equals(user.getId()) &&
                !userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/error/403";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);
            model.addAttribute("roles", userDetails.getAuthorities());
            return "tournaments/edit";
        }

        // Update tournament fields
        existingTournament.setName(tournamentUpdates.getName());
        existingTournament.setStartDate(tournamentUpdates.getStartDate());
        existingTournament.setEndDate(tournamentUpdates.getEndDate());
        existingTournament.setLocation(tournamentUpdates.getLocation());
        existingTournament.setDescription(tournamentUpdates.getDescription());
        existingTournament.setUpdatedAt(LocalDateTime.now());

        tournamentService.updateTournament(existingTournament);

        redirectAttributes.addFlashAttribute("successMessage", "Tournament updated successfully!");
        return "redirect:/tournaments/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteTournament(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Tournament tournament = tournamentService.findById(id)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        // Check if the user is the creator of the tournament or an admin
        if (!tournament.getCreatedBy().getId().equals(user.getId()) &&
                !userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/error/403";
        }

        tournamentService.deleteTournament(id);

        redirectAttributes.addFlashAttribute("successMessage", "Tournament deleted successfully!");
        return "redirect:/tournaments";
    }

    @GetMapping("/search")
    public String searchTournaments(@RequestParam("name") String name, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Tournament> tournaments = tournamentService.searchByName(name);

        model.addAttribute("tournaments", tournaments);
        model.addAttribute("searchTerm", name);
        model.addAttribute("user", user);
        model.addAttribute("roles", userDetails.getAuthorities());

        return "tournaments/list";
    }
}