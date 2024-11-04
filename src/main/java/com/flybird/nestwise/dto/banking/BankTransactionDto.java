package com.flybird.nestwise.dto.banking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BankTransactionDto {
    private String id;
    private String description;
    private BigDecimal amount;
    private Integer currencyCode;
    private Instant transactionTime;
}