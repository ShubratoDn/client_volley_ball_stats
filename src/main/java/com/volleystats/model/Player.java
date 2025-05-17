package com.volleystats.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "players")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required.")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters.")
    private String name;

    @NotNull(message = "Birth date is required.")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @NotNull(message = "Height is required.")
    @Column(name = "height")
    private Integer height;  // in cm

    @NotNull(message = "Weight is required.")
    @Column(name = "weight")
    private Integer weight;  // in kg

    @NotBlank(message = "Position is required.")
    @Size(max = 50, message = "Position cannot exceed 50 characters.")
    @Column(name = "position")
    private String position;

    @Size(max = 50, message = "Nationality cannot exceed 50 characters.")
    @Column(name = "nationality")
    private String nationality;

    @Column(name = "jersey_number")
    private Integer jerseyNumber;

    @Size(max = 1500, message = "Image URL must be 1500 characters or fewer.")
    @Column(name = "profile_image", length = 1500)
    private String profileImage;

    @Size(max = 1500, message = "Notes must be 1500 characters or fewer.")
    @Column(name = "notes", columnDefinition = "TEXT", length = 1500)
    private String notes;

    @ManyToMany(mappedBy = "players")
    private Set<Team> teams = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Calculate age based on date of birth
    @Transient
    public Integer getAge() {
        if (dateOfBirth == null) {
            return null;
        }
        return LocalDate.now().getYear() - dateOfBirth.getYear();
    }
}