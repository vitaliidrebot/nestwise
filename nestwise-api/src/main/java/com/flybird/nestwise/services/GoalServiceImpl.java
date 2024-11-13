package com.flybird.nestwise.services;

import com.flybird.nestwise.domain.Budget;
import com.flybird.nestwise.domain.Goal;
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
import java.util.Optional;
import java.util.function.Function;

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
        var goal = getGoal(id);
        var goalToUpdate = mappingUtil.toDomain(requestDto, goal.getUserId(),
                goalRepository::getReferenceById, userRepository::getReferenceById);
        goalToUpdate.setId(id);

        var updatedGoal = goalRepository.save(goalToUpdate);

        return mappingUtil.toDto(updatedGoal);
    }

    @Override
    public void deleteGoal(Long id) {
        var goal = getGoal(id);

        goalRepository.delete(goal);
    }

    @Override
    public GoalResponseDto createGoalBudget(Long goalId, BudgetDto budgetDto) {
        Function<Goal, Budget> createBudgetFunction = goal ->
                mappingUtil.toDomain(budgetDto, goal.getId(), goalRepository::getReferenceById, currencyRepository::getReferenceById);

        return upsertGoalBudget(goalId, createBudgetFunction);
    }

    @Override
    public GoalResponseDto updateGoalBudget(Long goalId, BudgetDto requestDto) {
        Function<Goal, Budget> updateBudgetFunction = goal -> {
            var budget = Optional.ofNullable(goal.getBudget())
                    .orElseThrow(RuntimeException::new);

            Optional.ofNullable(requestDto.getMinBudget()).ifPresent(budget::setMinBudget);
            Optional.ofNullable(requestDto.getMaxBudget()).ifPresent(budget::setMaxBudget);
            Optional.ofNullable(requestDto.getCurrency())
                    .map(mappingUtil::toCurrencyCode)
                    .ifPresent(code -> budget.setCurrency(currencyRepository.getReferenceById(code)));

            return budget;
        };

        return upsertGoalBudget(goalId, updateBudgetFunction);
    }

    @Override
    public void deleteGoalBudget(Long id) {
        var goal = getGoal(id);
        goal.setBudget(null);

        goalRepository.save(goal);
    }

    private Goal getGoal(Long id) {
        var username = SecurityUtil.getUsername();
        var user = userRepository.findByUsername(username)
                .orElseThrow(RuntimeException::new);
        var goal = goalRepository.findById(id)
                .orElseThrow(RuntimeException::new);
        if (!goal.getUserId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        return goal;
    }

    private GoalResponseDto upsertGoalBudget(Long goalId, Function<Goal, Budget> budgetFunction) {
        var goal = goalRepository.findById(goalId)
                .orElseThrow(RuntimeException::new);

        goal.setBudget(budgetFunction.apply(goal));

        var updatedGoal = goalRepository.save(goal);

        return mappingUtil.toDto(updatedGoal);
    }
}
