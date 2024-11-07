package com.flybird.nestwise.clients.banks.monobank.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MonobankExchangeRateResponse {
    private Integer currencyCodeA;
    private Integer currencyCodeB;
    private Long date;
    private BigDecimal rateBuy;
    private BigDecimal rateSell;
    private BigDecimal rateCross;
}