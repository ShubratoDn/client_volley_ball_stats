package com.volleystats.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "matches")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Match date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") // Add this annotation to match the flatpickr format
    private LocalDateTime matchDate;

    @Size(max = 200)
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @Column(name = "status")
    private String status;

    // Keep only one relationship approach - using the direct Many-to-Many is simpler
    @ManyToMany
    @JoinTable(
            name = "match_teams",
            joinColumns = @JoinColumn(name = "match_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id"))
    private List<Team> teams = new ArrayList<>();

    // Comment out or remove this to avoid relationship conflicts
    // @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<MatchTeam> matchTeams = new ArrayList<>();

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Statistic> statistics = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(length = 1000)
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_team_id")
    private Team homeTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_team_id")
    private Team awayTeam;

    // Status constants to ensure consistency across the application
    public static final String STATUS_SCHEDULED = "SCHEDULED";
    public static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_CANCELLED = "CANCELLED";

    public Team getHomeTeam() {
        if(this.teams != null && !this.teams.isEmpty()){
            return this.teams.get(0);
        }
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }

    public Team getAwayTeam() {
        if(this.teams != null && !this.teams.isEmpty() && this.teams.size() > 1){
            return this.teams.get(1);
        }
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }
}