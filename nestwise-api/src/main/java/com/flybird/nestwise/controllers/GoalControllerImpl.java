package com.flybird.nestwise.controllers;

import com.flybird.nestwise.dto.BudgetDto;
import com.flybird.nestwise.dto.GoalRequestDto;
import com.flybird.nestwise.dto.GoalResponseDto;
import com.flybird.nestwise.security.Authenticated;
import com.flybird.nestwise.services.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalControllerImpl implements GoalController {
    private final GoalService goalService;

    @Override
    @Authenticated
    @PostMapping
    public ResponseEntity<GoalResponseDto> createGoal(@RequestBody GoalRequestDto requestDto) {
        var responseDto = goalService.createGoal(requestDto);

        return ResponseEntity.status(201).body(responseDto);
    }

    @Override
    @Authenticated
    @PostMapping("/{id}/budget")
    public ResponseEntity<GoalResponseDto> createGoalBudget(@PathVariable("id") Long goalId,
                                                            @RequestBody BudgetDto requestDto) {
        var responseDto = goalService.createGoalBudget(goalId, requestDto);

        return ResponseEntity.status(201).body(responseDto);
    }

    @Override
    @Authenticated
    @GetMapping
    public ResponseEntity<List<GoalResponseDto>> getUserGoals() {
        return ResponseEntity.ok(goalService.getUserGoals());
    }

    @Override
    @Authenticated
    @PutMapping("/{id}")
    public ResponseEntity<GoalResponseDto> updateGoal(@PathVariable("id") Long id,
                                                      @RequestBody GoalRequestDto requestDto) {
        var responseDto = goalService.updateGoal(id, requestDto);

        return ResponseEntity.ok(responseDto);
    }

    @Override
    @Authenticated
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable("id") Long id) {
        goalService.deleteGoal(id);

        return ResponseEntity.ok().build();
    }
}
