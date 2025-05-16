package com.volleystats.dto;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class StatisticSelection {
    private Long id;
    private String actionType;
    private String actionState;
    private Double startX;
    private Double startY;
    private Double endX;
    private Double endY;
    private Integer quality;
    private String color;
    private Long teamId;
    private Long playerId;

    // Getters and setters for all fields
    // ...
}