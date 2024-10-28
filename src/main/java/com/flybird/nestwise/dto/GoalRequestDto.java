package com.flybird.nestwise.dto;

import com.flybird.nestwise.domain.Goal;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Request DTO for {@link Goal}
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class GoalRequestDto implements Serializable {
    @Schema(description = "Optional parent goal id", example = "1")
    private Long parentId;

    @Schema(description = "Name of the goal", example = "Save for a car")
    @NotBlank
    private String name;

    @Schema(description = "Description of the goal", example = "Saving money to buy a new car")
    private String description;

    @Schema(description = "Start date of the goal", example = "2023-01-01T00:00:00Z")
    @NotNull
    private LocalDate startDate;

    @Schema(description = "End date of the goal", example = "2023-12-31T23:59:59Z")
    private LocalDate endDate;
}
