package com.volleystats.repository;

import com.volleystats.model.Match;
import com.volleystats.model.MatchTeam;
import com.volleystats.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchTeamRepository extends JpaRepository<MatchTeam, Long> {

    List<MatchTeam> findByMatch(Match match);

    List<MatchTeam> findByTeam(Team team);

    Optional<MatchTeam> findByMatchAndTeam(Match match, Team team);

    long countByMatchAndIsHomeTeam(Match match, boolean isHomeTeam);
}