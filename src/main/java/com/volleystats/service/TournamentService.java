package com.volleystats.service;

import com.volleystats.model.Team;
import com.volleystats.model.Tournament;
import com.volleystats.model.User;
import com.volleystats.repository.TeamRepository;
import com.volleystats.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TournamentService {

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TeamRepository teamRepository;

    public List<Tournament> findAllTournaments() {
        return tournamentRepository.findAll();
    }

    public Optional<Tournament> findById(Long id) {
        return tournamentRepository.findById(id);
    }

    public List<Tournament> findByUser(User user) {
        return tournamentRepository.findByCreatedBy(user);
    }

    public List<Tournament> searchByName(String name) {
        return tournamentRepository.findByNameContaining(name);
    }

    public List<Tournament> findUpcomingTournaments() {
        return tournamentRepository.findUpcomingTournaments(LocalDate.now());
    }

    public List<Tournament> findPastTournaments() {
        return tournamentRepository.findPastTournaments(LocalDate.now());
    }

    public List<Tournament> findTournamentsByTeam(Long teamId) {
        return tournamentRepository.findByTeamId(teamId);
    }

    @Transactional
    public Tournament createTournament(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    @Transactional
    public Tournament updateTournament(Tournament tournament) {
        return tournamentRepository.save(tournament);
    }

    @Transactional
    public void deleteTournament(Long id) {
        tournamentRepository.deleteById(id);
    }

    @Transactional
    public Tournament addTeamToTournament(Long tournamentId, Long teamId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        tournament.getTeams().add(team);
        return tournamentRepository.save(tournament);
    }

    @Transactional
    public Tournament removeTeamFromTournament(Long tournamentId, Long teamId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        tournament.getTeams().removeIf(team -> team.getId().equals(teamId));
        return tournamentRepository.save(tournament);
    }
}