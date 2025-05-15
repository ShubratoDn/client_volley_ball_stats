package com.volleystats.controller;

import com.volleystats.model.Player;
import com.volleystats.model.Team;
import com.volleystats.model.User;
import com.volleystats.repository.TeamRepository;
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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/teams")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private UserService userService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private TeamRepository teamRepository;

    @GetMapping
    public String listTeams(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Team> teams = teamService.findByUser(user);

        model.addAttribute("teams", teams);
        model.addAttribute("user", user);
        model.addAttribute("roles", userDetails.getAuthorities());

        return "teams/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("team", new Team());
        model.addAttribute("players", playerService.findByUser(user));
        model.addAttribute("user", user);
        model.addAttribute("roles", userDetails.getAuthorities());

        return "teams/create";
    }

    @PostMapping("/create")
    public String createTeam(@Valid @ModelAttribute("team") Team team,
                             BindingResult bindingResult,
                             @RequestParam(value = "playerIds", required = false) List<Long> playerIds,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (bindingResult.hasErrors()) {
            model.addAttribute("players", playerService.findByUser(user));
            model.addAttribute("user", user);
            model.addAttribute("roles", userDetails.getAuthorities());
            return "teams/create";
        }

        team.setCreatedBy(user);
        team.setCreatedAt(LocalDateTime.now());
        team.setUpdatedAt(LocalDateTime.now());

        // Save the team first to get an ID
        Team savedTeam = teamService.createTeam(team);

        // Add players if selected
        if (playerIds != null && !playerIds.isEmpty()) {
            for (Long playerId : playerIds) {
                if (playerId != null) {
                    teamService.addPlayerToTeam(savedTeam.getId(), playerId);
                }
            }
        }

        redirectAttributes.addFlashAttribute("successMessage", "Team created successfully!");
        return "redirect:/teams";
    }

    @GetMapping("/{id}")
    public String viewTeam(@PathVariable("id") Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Team team = teamService.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        model.addAttribute("team", team);
        model.addAttribute("user", user);
        model.addAttribute("roles", userDetails.getAuthorities());
        model.addAttribute("availablePlayers", playerService.findPlayersNotInTeam(id, user));

        return "teams/view";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Team team = teamService.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        // Check if the user is the creator of the team or an admin
        if (!team.getCreatedBy().getId().equals(user.getId()) &&
                !userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/error/403";
        }

        model.addAttribute("team", team);
        model.addAttribute("players", playerService.findByUser(user));
        model.addAttribute("user", user);
        model.addAttribute("roles", userDetails.getAuthorities());

        return "teams/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateTeam(@PathVariable("id") Long id,
                             @Valid @ModelAttribute("team") Team teamUpdates,
                             BindingResult bindingResult,
                             @RequestParam(value = "playerIds", required = false) List<Long> playerIds,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Team existingTeam = teamService.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        // Check if the user is the creator of the team or an admin
        if (!existingTeam.getCreatedBy().getId().equals(user.getId()) &&
                !userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/error/403";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("players", playerService.findByUser(user));
            model.addAttribute("user", user);
            model.addAttribute("roles", userDetails.getAuthorities());
            return "teams/edit";
        }

        // Update team fields
        existingTeam.setName(teamUpdates.getName());
        existingTeam.setDescription(teamUpdates.getDescription());
        existingTeam.setLogo(teamUpdates.getLogo());
        existingTeam.setLocation(teamUpdates.getLocation());
        existingTeam.setFoundedYear(teamUpdates.getFoundedYear());
        existingTeam.setColorPrimary(teamUpdates.getColorPrimary());
        existingTeam.setColorSecondary(teamUpdates.getColorSecondary());
        existingTeam.setUpdatedAt(LocalDateTime.now());

        teamService.updateTeam(existingTeam);

        // Handle player updates if needed
        // For simplicity, we'll clear existing players and add the selected ones
        if (playerIds != null) {
            // We need to remove all existing players and add new ones
            // First, get current players and remove them
            Set<Player> currentPlayers = new HashSet<>(existingTeam.getPlayers());
            for (Player player : currentPlayers) {
                teamService.removePlayerFromTeam(existingTeam.getId(), player.getId());
            }

            // Add new players
            for (Long playerId : playerIds) {
                if (playerId != null) {
                    teamService.addPlayerToTeam(existingTeam.getId(), playerId);
                }
            }
        }

        redirectAttributes.addFlashAttribute("successMessage", "Team updated successfully!");
        return "redirect:/teams/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteTeam(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Team team = teamService.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        // Check if the user is the creator of the team or an admin
        if (!team.getCreatedBy().getId().equals(user.getId()) &&
                !userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/error/403";
        }

        teamService.deleteTeam(id);

        redirectAttributes.addFlashAttribute("successMessage", "Team deleted successfully!");
        return "redirect:/teams";
    }

    @PostMapping("/{id}/add-player")
    public String addPlayerToTeam(@PathVariable("id") Long id,
                                  @RequestParam("playerId") Long playerId,
                                  RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Team team = teamService.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        // Check permission
        if (!team.getCreatedBy().getId().equals(user.getId()) &&
                !userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/error/403";
        }

        teamService.addPlayerToTeam(id, playerId);
        redirectAttributes.addFlashAttribute("successMessage", "Player added to team successfully!");

        return "redirect:/teams/" + id;
    }

    @PostMapping("/{id}/remove-player")
    public String removePlayerFromTeam(@PathVariable("id") Long id,
                                       @RequestParam("playerId") Long playerId,
                                       RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Team team = teamService.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        // Check permission
        if (!team.getCreatedBy().getId().equals(user.getId()) &&
                !userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/error/403";
        }

        teamService.removePlayerFromTeam(id, playerId);
        redirectAttributes.addFlashAttribute("successMessage", "Player removed from team successfully!");

        return "redirect:/teams/" + id;
    }

    @GetMapping("/search")
    public String searchTeams(@RequestParam("name") String name, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Team> teams = teamService.searchByName(name);

        model.addAttribute("teams", teams);
        model.addAttribute("searchTerm", name);
        model.addAttribute("user", user);
        model.addAttribute("roles", userDetails.getAuthorities());

        return "teams/list";
    }

    // Note: This method requires adding findByLocation to TeamService
    @GetMapping("/location/{location}")
    public String listTeamsByLocation(@PathVariable("location") String location, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // You'll need to implement this method in TeamService and TeamRepository
        // Example implementation: return teamRepository.findByLocationContainingIgnoreCase(location);
        List<Team> teams = teamRepository.findByLocationContaining(location);

        model.addAttribute("teams", teams);
        model.addAttribute("location", location);
        model.addAttribute("user", user);
        model.addAttribute("roles", userDetails.getAuthorities());

        return "teams/location-teams";
    }

    @GetMapping("/player/{playerId}")
    public String listTeamsByPlayer(@PathVariable("playerId") Long playerId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Player player = playerService.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        List<Team> teams = teamService.findTeamsByPlayer(playerId);

        model.addAttribute("teams", teams);
        model.addAttribute("player", player);
        model.addAttribute("user", user);
        model.addAttribute("roles", userDetails.getAuthorities());

        return "teams/player-teams";
    }

    // Note: This method requires adding advancedSearch to TeamService
    @GetMapping("/advanced-search")
    public String advancedSearch(@RequestParam(value = "name", required = false) String name,
                                 @RequestParam(value = "location", required = false) String location,
                                 @RequestParam(value = "foundedYear", required = false) Integer foundedYear,
                                 Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create a basic implementation until the service method is created
        List<Team> teams;
        if (name != null && !name.isEmpty()) {
            teams = teamRepository.findByNameContaining(name);
        } else {
            teams = teamService.findByUser(user);
        }

        model.addAttribute("teams", teams);
        model.addAttribute("searchName", name);
        model.addAttribute("searchLocation", location);
        model.addAttribute("searchFoundedYear", foundedYear);
        model.addAttribute("user", user);
        model.addAttribute("roles", userDetails.getAuthorities());

        return "teams/advanced-search";
    }
}