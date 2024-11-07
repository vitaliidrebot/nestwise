package com.flybird.nestwise.services;

import com.flybird.nestwise.dto.GoalRequestDto;
import com.flybird.nestwise.dto.GoalResponseDto;

import java.util.List;

public interface GoalService {
    GoalResponseDto createGoal(GoalRequestDto requestDto, String username);

    List<GoalResponseDto> getUserGoals(String username);

    GoalResponseDto updateGoal(Long id, GoalRequestDto requestDto, String username);

    void deleteGoal(Long id, String username);
}
