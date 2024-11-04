package com.flybird.nestwise.dto.banking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateDto {
    private Integer currencyCodeFrom;
    private Integer currencyCodeTo;
    private BigDecimal buyRate;
    private BigDecimal sellRate;
    private LocalDate date;
}
