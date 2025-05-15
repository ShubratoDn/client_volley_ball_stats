package com.volleystats.service;

import com.volleystats.model.Match;
import com.volleystats.model.MatchTeam;
import com.volleystats.model.Team;
import com.volleystats.repository.MatchRepository;
import com.volleystats.repository.MatchTeamRepository;
import com.volleystats.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MatchTeamService {

    private final MatchTeamRepository matchTeamRepository;
    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;

    @Autowired
    public MatchTeamService(MatchTeamRepository matchTeamRepository,
                            MatchRepository matchRepository,
                            TeamRepository teamRepository) {
        this.matchTeamRepository = matchTeamRepository;
        this.matchRepository = matchRepository;
        this.teamRepository = teamRepository;
    }

    @Transactional(readOnly = true)
    public List<MatchTeam> findAllMatchTeams() {
        return matchTeamRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<MatchTeam> findMatchTeamById(Long id) {
        return matchTeamRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<MatchTeam> findMatchTeamsByMatch(Match match) {
        return matchTeamRepository.findByMatch(match);
    }

    @Transactional(readOnly = true)
    public Optional<MatchTeam> findMatchTeamByMatchAndTeam(Match match, Team team) {
        return matchTeamRepository.findByMatchAndTeam(match, team);
    }

    @Transactional
    public MatchTeam createMatchTeam(Match match, Team team, boolean isHomeTeam) {
        // Check if the team is already part of the match
        Optional<MatchTeam> existingMatchTeam = matchTeamRepository.findByMatchAndTeam(match, team);
        if (existingMatchTeam.isPresent()) {
            throw new IllegalStateException("Team is already assigned to this match");
        }

        // Check if there's already a home team if we're trying to add another home team
        if (isHomeTeam) {
            long homeTeamCount = matchTeamRepository.countByMatchAndIsHomeTeam(match, true);
            if (homeTeamCount > 0) {
                throw new IllegalStateException("Match already has a home team");
            }
        }

        MatchTeam matchTeam = new MatchTeam();
        matchTeam.setMatch(match);
        matchTeam.setTeam(team);
        matchTeam.setScore(0); // Initialize with zero score

        return matchTeamRepository.save(matchTeam);
    }

    @Transactional
    public MatchTeam updateMatchTeamScore(Long matchTeamId, Integer score) {
        MatchTeam matchTeam = matchTeamRepository.findById(matchTeamId)
                .orElseThrow(() -> new IllegalArgumentException("MatchTeam not found with id: " + matchTeamId));

        matchTeam.setScore(score);
        return matchTeamRepository.save(matchTeam);
    }

    @Transactional
    public void deleteMatchTeam(Long id) {
        matchTeamRepository.deleteById(id);
    }

    @Transactional
    public void removeTeamFromMatch(Long matchId, Long teamId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with id: " + matchId));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found with id: " + teamId));

        Optional<MatchTeam> matchTeam = matchTeamRepository.findByMatchAndTeam(match, team);

        if (matchTeam.isPresent()) {
            matchTeamRepository.delete(matchTeam.get());

            // Remove from the teams Set as well
            match.getTeams().remove(team);
            matchRepository.save(match);
        }
    }
}