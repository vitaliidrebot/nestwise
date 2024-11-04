package com.flybird.nestwise.dto.banking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class BankBalanceResponseDto {
    private String currency;
    private BigDecimal balance;
    private List<BankBalance> banks;
}