package com.volleystats.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "teams")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Team name is required.")
    @Size(max = 100, message = "Team name must not exceed 100 characters.")
    private String name;


    @Size(max = 2000, message = "Description must not exceed 500 characters.")
    @Column(length = 2000)
    private String description;

    @Size(max = 2000, message = "Logo URL must not exceed 2000 characters.")
    @Column(length = 2000)
    private String logo;

    @Size(max = 254, message = "Location must not exceed 254 characters.")
    private String location;

    @Min(value = 1800, message = "Founded year must be a valid year after 1800.")
    private Integer foundedYear;

    @Size(max = 20, message = "Primary color must not exceed 20 characters.")
    private String colorPrimary;

    @Size(max = 20, message = "Secondary color must not exceed 20 characters.")
    private String colorSecondary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToMany
    @JoinTable(
            name = "team_players",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id"))
    private List<Player> players = new ArrayList<>();

    @ManyToMany(mappedBy = "teams")
    private Set<Tournament> tournaments = new HashSet<>();

    @ManyToMany(mappedBy = "teams")
    private Set<Match> matches = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}