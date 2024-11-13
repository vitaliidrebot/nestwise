package com.flybird.nestwise.dto;

import com.flybird.nestwise.domain.Goal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Response DTO for {@link Goal}
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class GoalResponseDto {
    @Schema(description = "Unique identifier of the goal", example = "1")
    private Integer id;

    @Schema(description = "Name of the goal", example = "Save for a car")
    private String name;

    @Schema(description = "Description of the goal", example = "Saving money to buy a new car")
    private String description;

    @Schema(description = "Start date of the goal", example = "2023-01-01T00:00:00Z")
    private LocalDate startDate;

    @Schema(description = "End date of the goal", example = "2023-12-31T23:59:59Z")
    private LocalDate endDate;

    @Schema(description = "Creation timestamp of the goal", example = "2023-01-01T00:00:00Z")
    private Instant createdAt;

    @Schema(description = "Identifier of the user who created the goal", example = "1")
    private Long userId;

    @Schema(description = "Goal estimated budget")
    private BudgetDto budget;

    @Schema(description = "List of child goals")
    private List<GoalResponseDto> children;
}
