package com.volleystats.repository;

import com.volleystats.model.Match;
import com.volleystats.model.Player;
import com.volleystats.model.Statistic;
import com.volleystats.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatisticRepository extends JpaRepository<Statistic, Long> {

    List<Statistic> findByMatch(Match match);

    List<Statistic> findByMatchId(Long matchId);

    List<Statistic> findByPlayer(Player player);

    List<Statistic> findByPlayerId(Long playerId);

    List<Statistic> findByTeam(Team team);

    List<Statistic> findByTeamId(Long teamId);

    List<Statistic> findByActionType(Statistic.ActionType actionType);

    List<Statistic> findByMatchIdAndActionType(Long matchId, Statistic.ActionType actionType);

    List<Statistic> findByPlayerIdAndActionType(Long playerId, Statistic.ActionType actionType);

    void deleteByPlayer(Player player);
}