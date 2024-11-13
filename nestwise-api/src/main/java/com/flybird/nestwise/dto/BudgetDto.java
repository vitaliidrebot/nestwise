package com.flybird.nestwise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BudgetDto {
    private Integer minBudget;
    private Integer maxBudget;
    private String currency;
}