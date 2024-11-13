package com.flybird.nestwise.services;

import com.flybird.nestwise.dto.BudgetDto;
import com.flybird.nestwise.dto.GoalRequestDto;
import com.flybird.nestwise.dto.GoalResponseDto;

import java.util.List;

public interface GoalService {
    GoalResponseDto createGoal(GoalRequestDto requestDto);

    List<GoalResponseDto> getUserGoals();

    GoalResponseDto updateGoal(Long id, GoalRequestDto requestDto);

    void deleteGoal(Long id);

    GoalResponseDto createGoalBudget(Long goalId, BudgetDto requestDto);

    GoalResponseDto updateGoalBudget(Long goalId, BudgetDto requestDto);
}
