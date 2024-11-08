package com.flybird.nestwise.clients.banks.privatbank.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PrivatbankExchangeRateResponse {
    private String date;
    private String bank;
    private Integer baseCurrency;
    private String baseCurrencyLit;
    private List<ExchangeRate> exchangeRate;

    @Data
    public static class ExchangeRate {
        private String baseCurrency;
        private String currency;
        private BigDecimal saleRateNB;
        private BigDecimal purchaseRateNB;
        private BigDecimal saleRate;
        private BigDecimal purchaseRate;
    }
}