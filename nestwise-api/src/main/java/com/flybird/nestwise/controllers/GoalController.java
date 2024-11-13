package com.flybird.nestwise.controllers;

import com.flybird.nestwise.dto.BudgetDto;
import com.flybird.nestwise.dto.GoalRequestDto;
import com.flybird.nestwise.dto.GoalResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@Tag(name = "Goal", description = "Goal management endpoints")
public interface GoalController {

    @Operation(summary = "Create a new goal", tags = "Goal", description = "Create a new goal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to create the resource"),
    })
    ResponseEntity<GoalResponseDto> createGoal(
            @Valid GoalRequestDto requestDto
    );

    @Operation(summary = "Create a budget for a goal", tags = "Goal", description = "Create a budget for a specific goal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to create the resource"),
    })
    ResponseEntity<GoalResponseDto> createGoalBudget(Long goalId, @Valid BudgetDto requestDto);

    @Operation(summary = "Get all user goals", tags = "Goal", description = "Retrieve a list of all goals for user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    ResponseEntity<List<GoalResponseDto>> getUserGoals();

    @Operation(summary = "Update a goal", tags = "Goal", description = "Update a goal by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to update the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to update is not found")
    })
    ResponseEntity<GoalResponseDto> updateGoal(Long id, @Valid GoalRequestDto requestDto);

    @Operation(summary = "Delete user goal", tags = "Goal", description = "Delete user goal by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to delete the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to delete is not found")
    })
    ResponseEntity<Void> deleteGoal(Long id);
}
