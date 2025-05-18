package com.volleystats.service;

import com.volleystats.model.*;
import com.volleystats.repository.StatisticRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StatisticService {

    @Autowired
    private StatisticRepository statisticRepository;

    @Autowired
    private MatchService matchService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private TeamService teamService;

    // Create a new statistic
    public Statistic createStatistic(Statistic statistic) {
        // Validate based on action type
        if (statistic.getActionType() == Statistic.ActionType.ATTACK) {
            if (statistic.getEndX() == null || statistic.getEndY() == null) {
                throw new IllegalArgumentException("Attack statistics require end coordinates");
            }
        } else {
            // For reception and serve, end coordinates should be null
            statistic.setEndX(null);
            statistic.setEndY(null);
        }

        return statisticRepository.save(statistic);
    }

    // Find statistic by ID
    public Optional<Statistic> findById(Long id) {
        return statisticRepository.findById(id);
    }

    // Find statistics by match
    public List<Statistic> findByMatch(Match match) {
        return statisticRepository.findByMatch(match);
    }

    // Find statistics by match ID
    public List<Statistic> findByMatchId(Long matchId) {
        return statisticRepository.findByMatchId(matchId);
    }

    // Find statistics by player
    public List<Statistic> findByPlayer(Player player) {
        return statisticRepository.findByPlayer(player);
    }

    // Find statistics by player ID
    public List<Statistic> findByPlayerId(Long playerId) {
        return statisticRepository.findByPlayerId(playerId);
    }

    // Find statistics by team
    public List<Statistic> findByTeam(Team team) {
        return statisticRepository.findByTeam(team);
    }

    // Find statistics by team ID
    public List<Statistic> findByTeamId(Long teamId) {
        return statisticRepository.findByTeamId(teamId);
    }

    // Find statistics by action type
    public List<Statistic> findByActionType(Statistic.ActionType actionType) {
        return statisticRepository.findByActionType(actionType);
    }

    // Find statistics by match ID and action type
    public List<Statistic> findByMatchIdAndActionType(Long matchId, Statistic.ActionType actionType) {
        return statisticRepository.findByMatchIdAndActionType(matchId, actionType);
    }

    // Find statistics by player ID and action type
    public List<Statistic> findByPlayerIdAndActionType(Long playerId, Statistic.ActionType actionType) {
        return statisticRepository.findByPlayerIdAndActionType(playerId, actionType);
    }

    // Get default colors for different action types
    public String getDefaultColorForAction(Statistic.ActionType actionType) {
        switch (actionType) {
            case ATTACK:
                return "#FF0000"; // Red for attack
            case RECEPTION:
                return "#0000FF"; // Blue for reception
            case SERVE:
                return "#00FF00"; // Green for serve
            default:
                return "#000000"; // Black as fallback
        }
    }

    @Transactional
    public void deleteStatisticByPlayer(Player player) {
        statisticRepository.deleteByPlayer(player);
    }

    @Transactional
    public void deleteStatisticByCreatedBy(User createByUser) {
        statisticRepository.deleteByCreatedBy(createByUser);
    }
}