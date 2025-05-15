package com.volleystats.service;

import com.volleystats.model.Player;
import com.volleystats.model.Team;
import com.volleystats.model.User;
import com.volleystats.repository.PlayerRepository;
import com.volleystats.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PlayerRepository playerRepository;

    public List<Team> findAllTeams() {
        return teamRepository.findAll();
    }

    public Optional<Team> findById(Long id) {
        return teamRepository.findById(id);
    }

    public List<Team> findByUser(User user) {
        return teamRepository.findByCreatedBy(user);
    }

    public List<Team> searchByName(String name) {
        return teamRepository.findByNameContaining(name);
    }

    public List<Team> findByLocation(String location) {
        return teamRepository.findByLocationContaining(location);
    }

    public List<Team> findTeamsByPlayer(Long playerId) {
        return teamRepository.findByPlayerId(playerId);
    }

    public List<Team> advancedSearch(String name, String location, Integer foundedYear) {
        return teamRepository.advancedSearch(name, location, foundedYear);
    }

    @Transactional
    public Team createTeam(Team team) {
        return teamRepository.save(team);
    }

    @Transactional
    public Team updateTeam(Team team) {
        return teamRepository.save(team);
    }

    @Transactional
    public void deleteTeam(Long id) {
        teamRepository.deleteById(id);
    }

    @Transactional
    public Team addPlayerToTeam(Long teamId, Long playerId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        team.getPlayers().add(player);
        return teamRepository.save(team);
    }

    @Transactional
    public Team removePlayerFromTeam(Long teamId, Long playerId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        team.getPlayers().removeIf(player -> player.getId().equals(playerId));
        return teamRepository.save(team);
    }

    public long countAllTeams() {
        return teamRepository.count();
    }
}