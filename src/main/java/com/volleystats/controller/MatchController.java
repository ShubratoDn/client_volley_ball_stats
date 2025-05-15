package com.volleystats.controller;

import com.volleystats.model.Match;
import com.volleystats.model.Team;
import com.volleystats.model.User;
import com.volleystats.service.MatchService;
import com.volleystats.service.TeamService;
import com.volleystats.service.TournamentService;
import com.volleystats.service.UserService;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/matches")
public class MatchController {

    private static final Logger logger = LoggerFactory.getLogger(MatchController.class);

    @Autowired
    private MatchService matchService;

    @Autowired
    private UserService userService;

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private TeamService teamService;

    @GetMapping
    public String listMatches(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Match> matches = matchService.findByUser(user);

        model.addAttribute("matches", matches);
        model.addAttribute("user", user);
        model.addAttribute("roles", userDetails.getAuthorities());

        return "matches/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("match", new Match());
        model.addAttribute("tournaments", tournamentService.findByUser(user));
        model.addAttribute("teams", teamService.findByUser(user));
        model.addAttribute("user", user);
        model.addAttribute("roles", userDetails.getAuthorities());

        return "matches/create";
    }

    @PostMapping("/create")
    public String createMatch(@Valid @ModelAttribute("match") Match match,
                              BindingResult bindingResult,
                              @RequestParam(value = "teamIds", required = false) List<Long> teamIds,
                              RedirectAttributes redirectAttributes,
                              Model model) {

        // Add debug logging to see what's coming in
        logger.debug("Received match object: {}", match);
        logger.debug("Team IDs received: {}", teamIds);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (bindingResult.hasErrors()) {
            logger.error("Form has validation errors: {}", bindingResult.getAllErrors());
            model.addAttribute("tournaments", tournamentService.findByUser(user));
            model.addAttribute("teams", teamService.findByUser(user));
            model.addAttribute("user", user);
            model.addAttribute("roles", userDetails.getAuthorities());
            return "matches/create";
        }

        // Set user and timestamps
        match.setCreatedBy(user);
        match.setCreatedAt(LocalDateTime.now());
        match.setUpdatedAt(LocalDateTime.now());

        // Make sure status has a default value if not set
        if (match.getStatus() == null || match.getStatus().isEmpty()) {
            match.setStatus(Match.STATUS_SCHEDULED);
        }

        try {
            // Save the match first to get an ID
            Match savedMatch = matchService.createMatch(match);
            logger.debug("Match saved with ID: {}", savedMatch.getId());

            // Add teams if selected
            if (teamIds != null && !teamIds.isEmpty()) {
                logger.debug("Processing {} team(s) to add to match", teamIds.size());
                for (Long teamId : teamIds) {
                    if (teamId != null) {
                        teamService.findById(teamId).ifPresent(team -> {
                            try {
                                logger.debug("Adding team ID {} to match", teamId);
                                matchService.addTeamToMatch(savedMatch.getId(), team);
                            } catch (IllegalStateException e) {
                                logger.error("Failed to add team: {}", e.getMessage());
                                // Max teams already added, ignore additional teams
                            }
                        });
                    }
                }
            }

            redirectAttributes.addFlashAttribute("successMessage", "Match created successfully!");
            return "redirect:/matches";

        } catch (Exception e) {
            logger.error("Error creating match: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Error creating match: " + e.getMessage());
            model.addAttribute("tournaments", tournamentService.findByUser(user));
            model.addAttribute("teams", teamService.findByUser(user));
            model.addAttribute("user", user);
            model.addAttribute("roles", userDetails.getAuthorities());
            return "matches/create";
        }
    }

    @GetMapping("/{id}")
    public String viewMatch(@PathVariable("id") Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Match match = matchService.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        model.addAttribute("match", match);
        model.addAttribute("user", user);
        model.addAttribute("roles", userDetails.getAuthorities());

        return "matches/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Match match = matchService.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        // Check if the user is the creator of the match or an admin
        if (!match.getCreatedBy().getId().equals(user.getId()) &&
                !userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/error/403";
        }

        model.addAttribute("match", match);
        model.addAttribute("tournaments", tournamentService.findByUser(user));
        model.addAttribute("teams", teamService.findByUser(user));
        model.addAttribute("user", user);
        model.addAttribute("roles", userDetails.getAuthorities());

        return "matches/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateMatch(@PathVariable("id") Long id,
                              @Valid @ModelAttribute("match") Match matchUpdates,
                              BindingResult bindingResult,
                              @RequestParam(value = "teamIds", required = false) List<Long> teamIds,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Match existingMatch = matchService.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        // Check if the user is the creator of the match or an admin
        if (!existingMatch.getCreatedBy().getId().equals(user.getId()) &&
                !userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/error/403";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("tournaments", tournamentService.findByUser(user));
            model.addAttribute("teams", teamService.findByUser(user));
            model.addAttribute("user", user);
            model.addAttribute("roles", userDetails.getAuthorities());
            return "matches/edit";
        }

        // Update match fields
        existingMatch.setMatchDate(matchUpdates.getMatchDate());
        existingMatch.setLocation(matchUpdates.getLocation());
        existingMatch.setStatus(matchUpdates.getStatus());
        existingMatch.setTournament(matchUpdates.getTournament());
        existingMatch.setNotes(matchUpdates.getNotes());
        existingMatch.setUpdatedAt(LocalDateTime.now());

        matchService.updateMatch(existingMatch);

        // Handle team updates if needed
        // For simplicity, we'll clear existing teams and add the selected ones
        if (teamIds != null && !teamIds.isEmpty()) {
            // Remove existing teams
            existingMatch.getTeams().forEach(team ->
                    matchService.removeTeamFromMatch(existingMatch.getId(), team));

            // Add new teams
            for (Long teamId : teamIds) {
                if (teamId != null) {
                    teamService.findById(teamId).ifPresent(team -> {
                        try {
                            matchService.addTeamToMatch(existingMatch.getId(), team);
                        } catch (IllegalStateException e) {
                            // Max teams already added, ignore additional teams
                        }
                    });
                }
            }
        }

        redirectAttributes.addFlashAttribute("successMessage", "Match updated successfully!");
        return "redirect:/matches/" + id;
    }

    // Rest of the methods remain unchanged...
    @PostMapping("/{id}/delete")
    public String deleteMatch(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Match match = matchService.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        // Check if the user is the creator of the match or an admin
        if (!match.getCreatedBy().getId().equals(user.getId()) &&
                !userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/error/403";
        }

        matchService.deleteMatch(id);

        redirectAttributes.addFlashAttribute("successMessage", "Match deleted successfully!");
        return "redirect:/matches";
    }

    @GetMapping("/tournament/{tournamentId}")
    public String listMatchesByTournament(@PathVariable("tournamentId") Long tournamentId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Match> matches = matchService.findByTournamentId(tournamentId);

        model.addAttribute("matches", matches);
        model.addAttribute("tournament", tournamentService.findById(tournamentId).orElse(null));
        model.addAttribute("user", user);
        model.addAttribute("roles", userDetails.getAuthorities());

        return "matches/tournament-matches";
    }

    @GetMapping("/search")
    public String searchMatches(@RequestParam(value = "query", required = false) String query,
                                @RequestParam(value = "status", required = false) String status,
                                @RequestParam(value = "tournamentId", required = false) Long tournamentId,
                                Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Match> matches;

        if (query != null && !query.isEmpty()) {
            matches = matchService.searchByLocation(query);
        } else if (status != null && !status.isEmpty()) {
            matches = matchService.findByStatus(status);
        } else if (tournamentId != null) {
            matches = matchService.findByTournamentId(tournamentId);
        } else {
            matches = matchService.findByUser(user);
        }

        model.addAttribute("matches", matches);
        model.addAttribute("query", query);
        model.addAttribute("status", status);
        model.addAttribute("tournamentId", tournamentId);
        model.addAttribute("tournaments", tournamentService.findByUser(user));
        model.addAttribute("teams", teamService.findByUser(user));
        model.addAttribute("user", user);
        model.addAttribute("roles", userDetails.getAuthorities());

        return "matches/list";
    }

    @PostMapping("/{id}/update-status")
    public String updateMatchStatus(@PathVariable("id") Long id,
                                    @RequestParam("status") String status,
                                    RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Match match = matchService.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        // Check permission
        if (!match.getCreatedBy().getId().equals(user.getId()) &&
                !userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/error/403";
        }

        match.setStatus(status);
        match.setUpdatedAt(LocalDateTime.now());

        matchService.updateMatch(match);

        redirectAttributes.addFlashAttribute("successMessage", "Match status updated successfully!");
        return "redirect:/matches/" + id;
    }

    @PostMapping("/{id}/add-team")
    public String addTeamToMatch(@PathVariable("id") Long id,
                                 @RequestParam("teamId") Long teamId,
                                 RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Match match = matchService.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        // Check permission
        if (!match.getCreatedBy().getId().equals(user.getId()) &&
                !userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/error/403";
        }

        Team team = teamService.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        try {
            matchService.addTeamToMatch(id, team);
            redirectAttributes.addFlashAttribute("successMessage", "Team added to match successfully!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/matches/" + id;
    }

    @PostMapping("/{id}/remove-team")
    public String removeTeamFromMatch(@PathVariable("id") Long id,
                                      @RequestParam("teamId") Long teamId,
                                      RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Match match = matchService.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        // Check permission
        if (!match.getCreatedBy().getId().equals(user.getId()) &&
                !userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/error/403";
        }

        Team team = teamService.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        matchService.removeTeamFromMatch(id, team);
        redirectAttributes.addFlashAttribute("successMessage", "Team removed from match successfully!");

        return "redirect:/matches/" + id;
    }

    @PostMapping("/{id}/start")
    public String startMatch(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Match match = matchService.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        // Check permission
        if (!match.getCreatedBy().getId().equals(user.getId()) &&
                !userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/error/403";
        }

        try {
            matchService.startMatch(id);
            redirectAttributes.addFlashAttribute("successMessage", "Match started successfully!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/matches/" + id;
    }

    @PostMapping("/{id}/complete")
    public String completeMatch(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Match match = matchService.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        // Check permission
        if (!match.getCreatedBy().getId().equals(user.getId()) &&
                !userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/error/403";
        }

        try {
            matchService.completeMatch(id);
            redirectAttributes.addFlashAttribute("successMessage", "Match completed successfully!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/matches/" + id;
    }
}