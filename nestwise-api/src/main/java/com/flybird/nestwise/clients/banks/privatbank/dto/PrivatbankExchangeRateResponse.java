package com.flybird.nestwise.clients.banks.privatbank.dto;

import lombok.Data;

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
        private Double saleRateNB;
        private Double purchaseRateNB;
        private Double saleRate;
        private Double purchaseRate;
    }
}