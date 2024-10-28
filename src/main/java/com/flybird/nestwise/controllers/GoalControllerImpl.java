package com.flybird.nestwise.controllers;

import com.flybird.nestwise.dto.GoalRequestDto;
import com.flybird.nestwise.dto.GoalResponseDto;
import com.flybird.nestwise.security.Authenticated;
import com.flybird.nestwise.services.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    public ResponseEntity<GoalResponseDto> createGoal(@AuthenticationPrincipal User user,
                                                      @RequestBody GoalRequestDto requestDto) {
        GoalResponseDto responseDto = goalService.createGoal(requestDto, user.getUsername());
        return ResponseEntity.status(201).body(responseDto);
    }

    @Override
    @Authenticated
    @GetMapping
    public ResponseEntity<List<GoalResponseDto>> getUserGoals(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(goalService.getUserGoals(user.getUsername()));
    }
}
