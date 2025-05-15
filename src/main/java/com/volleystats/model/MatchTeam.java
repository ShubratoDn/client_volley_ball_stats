package com.volleystats.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "match_teams")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    private Integer score;

    @Column(name = "is_winner")
    private Boolean isWinner;

    @Column(name = "is_home_team")
    private Boolean isHomeTeam;

    // For tracking set scores in a volleyball match
    private String setScores; // Format: "25-21,25-18,23-25,25-20"
}