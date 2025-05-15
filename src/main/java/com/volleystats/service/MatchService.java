package com.volleystats.service;

import com.volleystats.model.Match;
import com.volleystats.model.Team;
import com.volleystats.model.Tournament;
import com.volleystats.model.User;
import com.volleystats.repository.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class MatchService {

    private final MatchRepository matchRepository;

    @Autowired
    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @Transactional(readOnly = true)
    public List<Match> findAllMatches() {
        return matchRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Match> findAllMatches(Pageable pageable) {
        return matchRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Match> findById(Long id) {
        return matchRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Match> findByTournamentId(Long tournamentId) {
        return matchRepository.findByTournamentId(tournamentId);
    }

    @Transactional(readOnly = true)
    public List<Match> findByUser(User user) {
        return matchRepository.findByCreatedBy(user);
    }

    @Transactional(readOnly = true)
    public List<Match> searchByLocation(String location) {
        return matchRepository.findByLocationContainingIgnoreCase(location);
    }

    @Transactional(readOnly = true)
    public List<Match> findByStatus(String status) {
        return matchRepository.findByStatus(status);
    }

    @Transactional
    public Match createMatch(Match match) {
        return matchRepository.save(match);
    }

    @Transactional
    public Match updateMatch(Match match) {
        return matchRepository.save(match);
    }

    @Transactional
    public void deleteMatch(Long id) {
        matchRepository.deleteById(id);
    }

    @Transactional
    public Match addTeamToMatch(Long matchId, Team team) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with id: " + matchId));

        List<Team> teams = match.getTeams();
        if (teams.size() >= 2) {
            throw new IllegalStateException("Match already has the maximum number of teams (2)");
        }

        teams.add(team);
        match.setTeams(teams);
        return matchRepository.save(match);
    }

    @Transactional
    public Match removeTeamFromMatch(Long matchId, Team team) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with id: " + matchId));

        match.getTeams().remove(team);
        return matchRepository.save(match);
    }

    @Transactional
    public Match updateMatchStatus(Long matchId, String status) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with id: " + matchId));

        match.setStatus(status);
        return matchRepository.save(match);
    }

    @Transactional
    public Match startMatch(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with id: " + matchId));

        if (match.getTeams().size() != 2) {
            throw new IllegalStateException("Match must have exactly 2 teams to start");
        }

        match.setStatus("IN_PROGRESS");
        return matchRepository.save(match);
    }

    @Transactional
    public Match completeMatch(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with id: " + matchId));

        if (!match.getStatus().equals("IN_PROGRESS")) {
            throw new IllegalStateException("Only matches in progress can be completed");
        }

        match.setStatus("COMPLETED");
        return matchRepository.save(match);
    }

    @Transactional
    public Match cancelMatch(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with id: " + matchId));

        match.setStatus("CANCELLED");
        return matchRepository.save(match);
    }

    public long countAllMatches() {
        return matchRepository.count();
    }

}