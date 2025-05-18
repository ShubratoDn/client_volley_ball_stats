package com.volleystats.repository;

import com.volleystats.model.Team;
import com.volleystats.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    List<Team> findByCreatedBy(User user);

    List<Team> findByNameContaining(String name);

    List<Team> findByLocationContaining(String location);

    @Query("SELECT t FROM Team t JOIN t.players p WHERE p.id = :playerId")
    List<Team> findByPlayerId(@Param("playerId") Long playerId);

    @Query("SELECT t FROM Team t WHERE t.id IN :teamIds")
    Set<Team> findByTeamIds(@Param("teamIds") List<Long> teamIds);

    // Advanced search method
    @Query("SELECT t FROM Team t WHERE " +
            "(:name IS NULL OR t.name LIKE %:name%) AND " +
            "(:location IS NULL OR t.location LIKE %:location%) AND " +
            "(:foundedYear IS NULL OR t.foundedYear = :foundedYear)")
    List<Team> advancedSearch(@Param("name") String name,
                              @Param("location") String location,
                              @Param("foundedYear") Integer foundedYear);

    @Transactional
    void deleteByCreatedBy(User user);
}