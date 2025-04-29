package com.podlive.tracker.goal.controller;

import com.podlive.tracker.goal.dto.GoalDTO;  // Импортировать GoalDTO
import com.podlive.tracker.goal.model.Goal;
import com.podlive.tracker.goal.service.GoalService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    // Принимаем GoalDTO вместо Goal
    @PostMapping
    public Goal createGoal(@RequestBody GoalDTO goalDTO) {
        return goalService.createGoal(goalDTO);  // Передаем GoalDTO в сервис
    }

    @GetMapping
    public List<Goal> getAllGoals() {
        return goalService.getAllGoals();
    }

    @GetMapping("/{id}")
    public Goal getGoalById(@PathVariable Integer id) {
        return goalService.getGoalById(id);
    }

    @GetMapping("/{id}/progress")
    public BigDecimal getGoalProgress(@PathVariable Integer id) {
        return goalService.getGoalProgress(id);
    }

    @GetMapping("/{id}/overspending")
    public boolean isOverspending(@PathVariable Integer id,
                                  @RequestParam("limit") BigDecimal spendingLimit) {
        return goalService.isOverspending(id, spendingLimit);
    }
}
