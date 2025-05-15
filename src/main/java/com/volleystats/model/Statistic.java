package com.volleystats.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "statistics")
@Data
public class Statistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ActionType actionType;

    @Enumerated(EnumType.STRING)
    private ActionState actionState;

    // Point coordinates
    private double startX;
    private double startY;

    // Only used for attack (end point of line), null for reception/serve
    private Double endX;
    private Double endY;

    // Color for the action visualization
    private String color;

    @Column(length = 1000)
    private String notes;

    @ManyToOne
    @JoinColumn(name = "match_id")
    private Match match;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Integer quality = 1;

    // Enum to represent different volleyball actions
    public enum ActionType {
        ATTACK,
        RECEPTION,
        SERVE
    }

    public enum ActionState {
        // ATTACK
        KILL,
        SHOT,
        ATTACK_ERROR,

        // RECEPTION
        RECEPTION_ERROR,
        REGULAR_RECEPTION,

        // SERVE
        ACE,
        SERVE_ERROR,
        BREAK_POINT,
        FREEBALL
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public Double getEndX() {
        return endX;
    }

    public void setEndX(Double endX) {
        this.endX = endX;
    }

    public Double getEndY() {
        return endY;
    }

    public void setEndY(Double endY) {
        this.endY = endY;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper method to check if this is an attack (which requires end coordinates)
    public boolean isAttack() {
        return actionType == ActionType.ATTACK;
    }

    // Helper method to get a description for display
    public String getActionDescription() {
        StringBuilder description = new StringBuilder();

        if (player != null) {
            description.append(player.getName()).append(" - ");
        }

        description.append(actionType.toString());

        if (notes != null && !notes.isEmpty()) {
            description.append(" (").append(notes).append(")");
        }

        return description.toString();
    }
}