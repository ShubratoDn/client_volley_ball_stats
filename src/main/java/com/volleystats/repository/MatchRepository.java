package com.volleystats.repository;

import com.volleystats.model.Match;
import com.volleystats.model.Tournament;
import com.volleystats.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByCreatedBy(User user);

    List<Match> findByTournament(Tournament tournament);

    List<Match> findByTournamentId(Long tournamentId);

    List<Match> findByMatchDate(LocalDate date);

    List<Match> findByStatus(String status);

    List<Match> findByLocationContainingIgnoreCase(String location);

    @Query("SELECT m FROM Match m WHERE m.matchDate >= :currentDate ORDER BY m.matchDate ASC")
    List<Match> findUpcomingMatches(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT m FROM Match m WHERE m.matchDate < :currentDate ORDER BY m.matchDate DESC")
    List<Match> findPastMatches(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT m FROM Match m JOIN m.teams t WHERE t.id = :teamId")
    List<Match> findByTeamId(@Param("teamId") Long teamId);

    @Query("SELECT m FROM Match m JOIN m.teams t JOIN t.players p WHERE p.id = :playerId")
    List<Match> findByPlayerId(@Param("playerId") Long playerId);
}