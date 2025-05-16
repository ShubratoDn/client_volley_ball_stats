package com.volleystats.dto;
import com.volleystats.model.Statistic.ActionType;
import com.volleystats.model.Statistic.ActionState;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ActionTypeStateMapper {

    private static final Map<ActionType, List<ActionState>> mapping = new EnumMap<>(ActionType.class);

    static {
        mapping.put(ActionType.ATTACK, List.of(
                ActionState.KILL,
                ActionState.SHOT,
                ActionState.ATTACK_ERROR
        ));
        mapping.put(ActionType.RECEPTION, List.of(
                ActionState.RECEPTION_ERROR,
                ActionState.REGULAR_RECEPTION
        ));
        mapping.put(ActionType.SERVE, List.of(
                ActionState.ACE,
                ActionState.SERVE_ERROR,
                ActionState.BREAK_POINT,
                ActionState.FREEBALL
        ));
    }

    public static List<ActionState> getStatesForActionType(ActionType actionType) {
        return mapping.getOrDefault(actionType, List.of());
    }
}
