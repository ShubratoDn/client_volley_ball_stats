package com.volleystats.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.volleystats.model.*;
import com.volleystats.service.PlayerService;
import com.volleystats.service.StatisticService;
import com.volleystats.service.TeamService;
import com.volleystats.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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

import java.net.http.HttpRequest;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/players")
public class PlayerController {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private UserService userService;

    @Autowired
    private StatisticService statisticService;

    @Autowired
    private TeamService teamService;

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
    public String viewPlayer(@PathVariable("id") Long id,
                             @RequestParam(value = "actionType", required = false, defaultValue = "") Statistic.ActionType actionType,
                             Model model) throws JsonProcessingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Player player = playerService.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        model.addAttribute("player", player);
        model.addAttribute("user", user);
        model.addAttribute("roles", userDetails.getAuthorities());


        List<Statistic> statistics = new ArrayList<>();

        if(actionType != null){
            statistics = statisticService.findByPlayerIdAndActionType(player.getId(), actionType);
        }else {
            statistics = statisticService.findByPlayer(player);
        }
        List<Statistic> newStatistics = new ArrayList<>();
        for (Statistic statistic : statistics) {

            Statistic newStatistic = new Statistic();
            newStatistic.setId(statistic.getId());
            newStatistic.setActionType(statistic.getActionType());
            newStatistic.setActionState(statistic.getActionState());
            newStatistic.setStartX(statistic.getStartX());
            newStatistic.setStartY(statistic.getStartY());
            newStatistic.setEndX(statistic.getEndX());
            newStatistic.setEndY(statistic.getEndY());
            newStatistic.setColor(statistic.getColor());
            newStatistic.setNotes(statistic.getNotes());

            newStatistics.add(newStatistic);
        }


        model.addAttribute("statistics", statistics);

        ObjectMapper mapper = new ObjectMapper();
        // Register JavaTimeModule to handle Java 8 Date/Time types
        mapper.registerModule(new JavaTimeModule());
        // Optionally, disable writing dates as timestamps (e.g., 1626876870000)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


        String statisticsJson = mapper.writeValueAsString(newStatistics);
        model.addAttribute("statisticsJson", statisticsJson);

        // Count statistics by type
        long attackCount = statistics.stream().filter(s -> s.getActionType() == Statistic.ActionType.ATTACK).count();
        long receptionCount = statistics.stream().filter(s -> s.getActionType() == Statistic.ActionType.RECEPTION).count();
        long serveCount = statistics.stream().filter(s -> s.getActionType() == Statistic.ActionType.SERVE).count();

        model.addAttribute("attackCount", attackCount);
        model.addAttribute("receptionCount", receptionCount);
        model.addAttribute("serveCount", serveCount);
                
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
    public String deletePlayer(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, HttpServletRequest request) {
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

        redirectAttributes.addFlashAttribute("requestToDelete", "A_DEMO_TEXT");

        try{
            playerService.deletePlayer(id);
            redirectAttributes.addFlashAttribute("successMessage", "Player deleted successfully!");
        }catch(Exception e){
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete player! Maybe the player is associated with Teams and Statistics. <a href='/players/force-delete?id="+id+"'>Click here</a> to remove the player with all data");
            // In your deletePlayer method:
            request.getSession().setAttribute("forceDeletePlayerId", id);
        }

        return "redirect:/players";
    }

    @GetMapping("/force-delete")
    public String foreceDeletePlayer(@RequestParam(required = false) Long id, RedirectAttributes redirectAttributes, Model model, HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify session token matches URL token
        HttpSession session = request.getSession();
        Long sessionPlayerId = (Long) session.getAttribute("forceDeletePlayerId");

        if (id == null || !id.equals(sessionPlayerId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid force delete request");
            return "redirect:/players";
        }
        // Clean up session
        session.removeAttribute("forceDeletePlayerId");
        Player player = playerService.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        // Check if the user is the creator of the player or an admin
        if (!player.getCreatedBy().getId().equals(user.getId()) &&
                !userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/error/403";
        }

        statisticService.deleteStatisticByPlayer(player);

        List<Team> teamsByPlayer = teamService.findTeamsByPlayer(player.getId());
        for(Team team : teamsByPlayer) {
            teamService.removePlayerFromTeam(team.getId(), player.getId());
        }

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