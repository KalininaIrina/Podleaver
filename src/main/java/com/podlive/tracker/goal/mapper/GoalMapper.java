package com.podlive.tracker.goal.mapper;

import com.podlive.tracker.goal.dto.GoalDTO;
import com.podlive.tracker.goal.model.Goal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    @Mapping(source = "account.id", target = "accountId")
    GoalDTO goalToGoalDTO(Goal goal);

    @Mapping(source = "accountId", target = "account.id")
    @Mapping(target = "id", ignore = true)
    Goal goalDTOToGoal(GoalDTO goalDTO);
}

