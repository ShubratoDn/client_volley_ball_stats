package com.volleystats.service;

import com.volleystats.model.Player;
import com.volleystats.model.Team;
import com.volleystats.model.User;
import com.volleystats.repository.PlayerRepository;
import com.volleystats.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private TeamRepository teamRepository;

    public List<Player> findAllPlayers() {
        return playerRepository.findAll();
    }

    public Optional<Player> findById(Long id) {
        return playerRepository.findById(id);
    }

    public List<Player> findByUser(User user) {
        return playerRepository.findByCreatedBy(user);
    }

    public List<Player> searchByName(String name) {
        return playerRepository.findByNameContaining(name);
    }

    /**
     * Find players created by the user that are not already in the specified team
     *
     * @param teamId the team ID to check against
     * @param user the user who created the players
     * @return list of players not in the team
     */
    public List<Player> findPlayersNotInTeam(Long teamId, User user) {
        // Get the team
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        // Get all players created by this user
        List<Player> userPlayers = playerRepository.findByCreatedBy(user);

        // Filter out players that are already in the team
        return userPlayers.stream()
                .filter(player -> !team.getPlayers().contains(player))
                .collect(Collectors.toList());
    }

    @Transactional
    public Player createPlayer(Player player) {
        return playerRepository.save(player);
    }

    @Transactional
    public Player updatePlayer(Player player) {
        return playerRepository.save(player);
    }

    @Transactional
    public void deletePlayer(Long id) {
        playerRepository.deleteById(id);
    }

    public long countAllPlayers() {
        return playerRepository.count();
    }

    // Method to find players by team ID
    public List<Player> findByTeamId(Long teamId) {
        return playerRepository.findByTeamId(teamId);
    }


}