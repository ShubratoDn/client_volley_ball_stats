package com.volleystats.repository;

import com.volleystats.model.Player;
import com.volleystats.model.Team;
import com.volleystats.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findByCreatedBy(User user);

    List<Player> findByNameContaining(String name);

    // Method to find players by team ID for many-to-many relationship
    @Query("SELECT p FROM Player p JOIN p.teams t WHERE t.id = :teamId")
    List<Player> findByTeamId(@Param("teamId") Long teamId);

    @Transactional
    void deleteByCreatedBy(User user);

}
