package com.volleystats.dto;

import lombok.Data;

import java.util.List;

@Data
public class ActionChartDTO {
    private String actionType;
    private List<String> actionStates;
    private List<Integer> counts;

    public ActionChartDTO(String actionType, List<String> actionStates, List<Integer> counts) {
        this.actionType = actionType;
        this.actionStates = actionStates;
        this.counts = counts;
    }

}
