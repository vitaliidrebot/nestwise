package com.flybird.nestwise.clients.banks.kredobank.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class KredobankExchangeRateResponse {
    private String currency;
    private BigDecimal buyRate;
    private BigDecimal sellRate;
    private String startDate;
    private String endDate;
}