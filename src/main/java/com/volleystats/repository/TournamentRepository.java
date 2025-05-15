package com.volleystats.repository;

import com.volleystats.model.Tournament;
import com.volleystats.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {
    List<Tournament> findByCreatedBy(User user);

    List<Tournament> findByNameContaining(String name);

    List<Tournament> findByNameContainingIgnoreCase(String name);

    @Query("SELECT t FROM Tournament t WHERE t.startDate >= :startDate")
    List<Tournament> findUpcomingTournaments(@Param("startDate") LocalDate startDate);

    @Query("SELECT t FROM Tournament t WHERE t.endDate < :currentDate")
    List<Tournament> findPastTournaments(@Param("currentDate") LocalDate currentDate);

    @Query("SELECT t FROM Tournament t JOIN t.teams team WHERE team.id = :teamId")
    List<Tournament> findByTeamId(@Param("teamId") Long teamId);
}