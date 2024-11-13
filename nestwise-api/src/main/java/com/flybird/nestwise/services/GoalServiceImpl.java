package com.flybird.nestwise.services;

import com.flybird.nestwise.dto.BudgetDto;
import com.flybird.nestwise.dto.GoalRequestDto;
import com.flybird.nestwise.dto.GoalResponseDto;
import com.flybird.nestwise.repositories.CurrencyRepository;
import com.flybird.nestwise.repositories.GoalRepository;
import com.flybird.nestwise.repositories.UserRepository;
import com.flybird.nestwise.utils.MappingUtil;
import com.flybird.nestwise.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {
    private final GoalRepository goalRepository;
    private final MappingUtil mappingUtil;
    private final UserRepository userRepository;
    private final CurrencyRepository currencyRepository;

    @Override
    public GoalResponseDto createGoal(GoalRequestDto requestDto) {
        var username = SecurityUtil.getUsername();
        var user = userRepository.findByUsername(username)
                .orElseThrow(RuntimeException::new);
        var goalToSave = mappingUtil.toDomain(requestDto, user.getId(),
                goalRepository::getReferenceById, userRepository::getReferenceById);
        var goal = goalRepository.save(goalToSave);

        return mappingUtil.toDto(goal);
    }

    @Override
    public List<GoalResponseDto> getUserGoals() {
        var username = SecurityUtil.getUsername();
        var user = userRepository.findByUsername(username)
                .orElseThrow(RuntimeException::new);
        var goals = goalRepository.findByUserId(user.getId());

        return goals.stream().map(mappingUtil::toDto).toList();
    }

    @Override
    public GoalResponseDto updateGoal(Long id, GoalRequestDto requestDto) {
        var username = SecurityUtil.getUsername();
        var user = userRepository.findByUsername(username)
                .orElseThrow(RuntimeException::new);
        var goal = goalRepository.findById(id)
                .orElseThrow(RuntimeException::new);
        if (!goal.getUserId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        var goalToUpdate = mappingUtil.toDomain(requestDto, user.getId(),
                goalRepository::getReferenceById, userRepository::getReferenceById);
        goalToUpdate.setId(id);

        var updatedGoal = goalRepository.save(goalToUpdate);

        return mappingUtil.toDto(updatedGoal);
    }

    @Override
    public void deleteGoal(Long id) {
        var username = SecurityUtil.getUsername();
        var user = userRepository.findByUsername(username)
                .orElseThrow(RuntimeException::new);
        var goal = goalRepository.findById(id)
                .orElseThrow(RuntimeException::new);
        if (!goal.getUserId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        goalRepository.delete(goal);
    }

    @Override
    public GoalResponseDto createGoalBudget(Long goalId, BudgetDto budgetDto) {
        var goal = goalRepository.findById(goalId)
                .orElseThrow(RuntimeException::new);

        var budget = mappingUtil.toDomain(budgetDto, goalId, goalRepository::getReferenceById, currencyRepository::getReferenceById);

        goal.setBudget(budget);

        var updatedGoal = goalRepository.save(goal);

        return mappingUtil.toDto(updatedGoal);
    }
}
