package com.podlive.tracker.goal.repository;

import com.podlive.tracker.goal.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Integer> {
}
