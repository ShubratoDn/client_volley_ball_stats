package com.volleystats.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.volleystats.dto.ActionChartDTO;
import com.volleystats.dto.ActionTypeStateMapper;
import com.volleystats.dto.StatisticSelection;
import com.volleystats.model.Match;
import com.volleystats.model.Player;
import com.volleystats.model.Statistic;
import com.volleystats.model.Statistic.ActionType;
import com.volleystats.model.Team;
import com.volleystats.model.User;

import com.volleystats.service.MatchService;
import com.volleystats.service.PlayerService;
import com.volleystats.service.StatisticService;
import com.volleystats.service.TeamService;
import com.volleystats.service.UserService;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/statistics")
public class StatisticsController {

    private static final Logger logger = LoggerFactory.getLogger(StatisticsController.class);

    @Autowired
    private StatisticService statisticService;

    @Autowired
    private UserService userService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private PlayerService playerService;

    /**
     * List all statistics for the logged-in user's matches
     */
    @GetMapping
    public String listStatistics(Model model) {
        User user = getCurrentUser();

        // We don't have a direct way to get statistics by user, so instead:
        // 1. Get all matches for the user
        List<Match> userMatches = matchService.findByUser(user);

        // Prepare the statistics for all these matches
        model.addAttribute("matches", userMatches);
        model.addAttribute("user", user);
        model.addAttribute("roles", getUserAuthorities());

        return "statistics/list";
    }

    /**
     * Show match statistics
     */
    @GetMapping("/match/{matchId}")
    public String viewMatchStatistics(@PathVariable("matchId") Long matchId, Model model) throws JsonProcessingException {
        User user = getCurrentUser();

        Match match = matchService.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        List<Statistic> statistics = statisticService.findByMatchId(matchId);
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

        model.addAttribute("match", match);
        model.addAttribute("statistics", statistics);
        model.addAttribute("user", user);
        model.addAttribute("roles", getUserAuthorities());

        ObjectMapper mapper = new ObjectMapper();
        // Register JavaTimeModule to handle Java 8 Date/Time types
        mapper.registerModule(new JavaTimeModule());
        // Optionally, disable writing dates as timestamps (e.g., 1626876870000)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


        String statisticsJson = mapper.writeValueAsString(newStatistics);
        model.addAttribute("statisticsJson", statisticsJson);

        // Count statistics by type
        long attackCount = statistics.stream().filter(s -> s.getActionType() == ActionType.ATTACK).count();
        long receptionCount = statistics.stream().filter(s -> s.getActionType() == ActionType.RECEPTION).count();
        long serveCount = statistics.stream().filter(s -> s.getActionType() == ActionType.SERVE).count();

        model.addAttribute("attackCount", attackCount);
        model.addAttribute("receptionCount", receptionCount);
        model.addAttribute("serveCount", serveCount);

        return "statistics/view-match";
    }

    /**
     * Show create new statistic form
     */
    @GetMapping("/create/{matchId}")
    public String showCreateForm(@PathVariable("matchId") Long matchId, Model model) {
        User user = getCurrentUser();

        Match match = matchService.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        Statistic statistic = new Statistic();
        statistic.setMatch(match);

        model.addAttribute("statistic", statistic);
        model.addAttribute("match", match);
        model.addAttribute("actionTypes", ActionType.values());
        model.addAttribute("teams", match.getTeams());
        model.addAttribute("players", playerService.findByUser(user));


        // We'll populate players based on team selection via AJAX in the frontend
        model.addAttribute("user", user);
        model.addAttribute("roles", getUserAuthorities());

        return "statistics/create";
    }

    /**
     * Create a new statistic
     */
    @PostMapping("/create")
    public String createStatistic(@Valid @ModelAttribute("statistic") Statistic statisticPayload,
                                  BindingResult bindingResult,
                                  @RequestParam("matchId") Long matchId,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {

        User user = getCurrentUser();


        if (bindingResult.hasErrors()) {
            logger.error("Form has validation errors: {}", bindingResult.getAllErrors());

            Match match = matchService.findById(matchId)
                    .orElseThrow(() -> new RuntimeException("Match not found"));

            model.addAttribute("match", match);
            model.addAttribute("actionTypes", ActionType.values());
            model.addAttribute("teams", teamService.findByUser(user));
            model.addAttribute("players", playerService.findByUser(user));
            model.addAttribute("user", user);
            model.addAttribute("roles", getUserAuthorities());
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating statistic");

            return "statistics/create";
        }

        try {

            for (StatisticSelection statisticSelection: statisticPayload.getSelections()){


                Statistic statistic = new Statistic();


                ActionType actionType = ActionType.valueOf(statisticSelection.getActionType());
                Statistic.ActionState actionState = Statistic.ActionState.valueOf(statisticSelection.getActionState());

                double startX = statisticSelection.getStartX();
                double startY = statisticSelection.getStartY();

                Double endX = statisticSelection.getEndX();
                Double endY = statisticSelection.getEndY();

                String color = statisticSelection.getColor();

                Long teamId = statisticSelection.getTeamId();
                Long playerId = statisticSelection.getPlayerId();

                // Set user and timestamps
                statistic.setCreatedBy(user);
                statistic.setCreatedAt(LocalDateTime.now());
                statistic.setUpdatedAt(LocalDateTime.now());

                // Set action type
                statistic.setActionType(actionType);
                statistic.setActionState(actionState);

                // Set coordinates
                statistic.setStartX(startX);
                statistic.setStartY(startY);



                // For attacks, we need end coordinates
                if (actionType == ActionType.ATTACK) {
                    if (endX == null || endY == null) {
                        throw new IllegalArgumentException("Attack statistics require end coordinates");
                    }
                    statistic.setEndX(endX);
                    statistic.setEndY(endY);
                } else {
                    // For other action types, make sure end coordinates are null
                    statistic.setEndX(null);
                    statistic.setEndY(null);
                }

                // Set color (use default if not provided)
                if (color == null || color.isEmpty()) {
                    color = statisticService.getDefaultColorForAction(actionType);
                }
                statistic.setColor(color);

                // Set related entities
                Match match = matchService.findById(matchId)
                        .orElseThrow(() -> new RuntimeException("Match not found"));
                statistic.setMatch(match);

                Team team = teamService.findById(teamId)
                        .orElseThrow(() -> new RuntimeException("Team not found"));
                statistic.setTeam(team);

                Player player = playerService.findById(playerId)
                        .orElseThrow(() -> new RuntimeException("Player not found"));
                statistic.setPlayer(player);

                // Save the statistic
                Statistic savedStatistic = statisticService.createStatistic(statistic);
                logger.debug("Statistic saved with ID: {}", savedStatistic.getId());

            }


            redirectAttributes.addFlashAttribute("successMessage", "Statistic added successfully!");
            return "redirect:/statistics/match/" + matchId;

        } catch (Exception e) {
            logger.error("Error creating statistic: {}", e.getMessage(), e);

            redirectAttributes.addFlashAttribute("errorMessage", "Error creating statistic: " + e.getMessage());
            return "redirect:/statistics/create/" + matchId;
        }
    }

    /**
     * View statistic details
     */
    @GetMapping("/{id}")
    public String viewStatistic(@PathVariable("id") Long id, Model model) throws JsonProcessingException {
        User user = getCurrentUser();

        Statistic statistic = statisticService.findById(id)
                .orElseThrow(() -> new RuntimeException("Statistic not found"));


        ObjectMapper mapper = new ObjectMapper();
        // Register JavaTimeModule to handle Java 8 Date/Time types
        mapper.registerModule(new JavaTimeModule());
        // Optionally, disable writing dates as timestamps (e.g., 1626876870000)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Statistic newStatistic = new Statistic();
        newStatistic.setStartX(statistic.getStartX());
        newStatistic.setStartY(statistic.getStartY());
        newStatistic.setEndX(statistic.getEndX());
        newStatistic.setEndY(statistic.getEndY());
        newStatistic.setActionType(statistic.getActionType());
        newStatistic.setActionState(statistic.getActionState());

        String statisticsJson = mapper.writeValueAsString(newStatistic);
        model.addAttribute("statisticCoordinate", statisticsJson);


        model.addAttribute("statistic", statistic);
        model.addAttribute("user", user);
        model.addAttribute("roles", getUserAuthorities());

        return "statistics/view";
    }

    /**
     * Get players by team ID (AJAX endpoint)
     */
    @GetMapping("/players-by-team")
    @ResponseBody
    public List<Player> getPlayersByTeam(@RequestParam("teamId") Long teamId) {
        try {
            // Use the service method to find players by team ID
            List<Player> byTeamId = playerService.findByTeamId(teamId);
            List<Player> players = new ArrayList<>();
            for (Player player : byTeamId) {
                Player newPlayer = new Player();
                newPlayer.setId(player.getId());
                newPlayer.setName(player.getName());
                players.add(newPlayer);
            }

            return players;
        } catch (Exception e) {
            logger.error("Error retrieving players for team ID {}: {}", teamId, e.getMessage());
            return List.of(); // Return empty list on error
        }
    }

    /**
     * View player statistics
     */
    @GetMapping("/player/{playerId}")
    public String viewPlayerStatistics(@PathVariable("playerId") Long playerId, Model model) {
        User user = getCurrentUser();

        Player player = playerService.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        List<Statistic> statistics = statisticService.findByPlayerId(playerId);

        model.addAttribute("player", player);
        model.addAttribute("statistics", statistics);
        model.addAttribute("user", user);
        model.addAttribute("roles", getUserAuthorities());

        // Count statistics by type
        long attackCount = statistics.stream().filter(s -> s.getActionType() == ActionType.ATTACK).count();
        long receptionCount = statistics.stream().filter(s -> s.getActionType() == ActionType.RECEPTION).count();
        long serveCount = statistics.stream().filter(s -> s.getActionType() == ActionType.SERVE).count();

        model.addAttribute("attackCount", attackCount);
        model.addAttribute("receptionCount", receptionCount);
        model.addAttribute("serveCount", serveCount);

        return "statistics/view-player";
    }

    /**
     * Filter statistics by action type
     */
    @GetMapping("/match/{matchId}/filter")
    public String filterMatchStatistics(@PathVariable("matchId") Long matchId,
                                        @RequestParam("actionType") ActionType actionType,
                                        Model model) throws JsonProcessingException {
        User user = getCurrentUser();

        Match match = matchService.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        List<Statistic> statistics = statisticService.findByMatchIdAndActionType(matchId, actionType);

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


        model.addAttribute("match", match);
        model.addAttribute("statistics", statistics);
        model.addAttribute("filteredActionType", actionType);
        model.addAttribute("user", user);
        model.addAttribute("roles", getUserAuthorities());

        ObjectMapper mapper = new ObjectMapper();
        // Register JavaTimeModule to handle Java 8 Date/Time types
        mapper.registerModule(new JavaTimeModule());
        // Optionally, disable writing dates as timestamps (e.g., 1626876870000)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);


        String statisticsJson = mapper.writeValueAsString(newStatistics);
        model.addAttribute("statisticsJson", statisticsJson);


        return "statistics/view-match";
    }

    // Helper methods

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userService.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Object getUserAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getAuthorities();
    }


    @ResponseBody
    @GetMapping("/chart/{field}/{id}/{actionType}")
    public ResponseEntity<ActionChartDTO> getChartData(
            @PathVariable Long id,
            @PathVariable String actionType,
            @PathVariable String field) {
        Statistic.ActionType type = Statistic.ActionType.valueOf(actionType.toUpperCase());

        List<Statistic> statistics = new ArrayList<>();

        if(field.equalsIgnoreCase("PLAYER")){
            statistics = statisticService.findByPlayerIdAndActionType(id, type);
        }else if (field.equalsIgnoreCase("MATCH")){
            statistics = statisticService.findByMatchIdAndActionType(id, type);
        }


        // Determine valid ActionStates for the ActionType
        List<Statistic.ActionState> validStates = getStatesForActionType(type);

        Map<Statistic.ActionState, Long> countMap = statistics.stream()
                .filter(stat -> stat.getActionState() != null)
                .collect(Collectors.groupingBy(Statistic::getActionState, Collectors.counting()));

        List<String> stateNames = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();

        for (Statistic.ActionState state : validStates) {
            stateNames.add(state.name());
            counts.add(countMap.getOrDefault(state, 0L).intValue());
        }

        ActionChartDTO dto = new ActionChartDTO(type.name(), stateNames, counts);
        return ResponseEntity.ok(dto);
    }

    private List<Statistic.ActionState> getStatesForActionType(Statistic.ActionType type) {
        return switch (type) {
            case ATTACK -> List.of(
                    Statistic.ActionState.KILL,
                    Statistic.ActionState.SHOT,
                    Statistic.ActionState.ATTACK_ERROR
            );
            case RECEPTION -> List.of(
                    Statistic.ActionState.RECEPTION_ERROR,
                    Statistic.ActionState.REGULAR_RECEPTION
            );
            case SERVE -> List.of(
                    Statistic.ActionState.ACE,
                    Statistic.ActionState.SERVE_ERROR,
                    Statistic.ActionState.BREAK_POINT,
                    Statistic.ActionState.FREEBALL
            );
        };
    }
}