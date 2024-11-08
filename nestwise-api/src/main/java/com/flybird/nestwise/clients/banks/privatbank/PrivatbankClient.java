package com.flybird.nestwise.clients.banks.privatbank;

import com.flybird.nestwise.clients.banks.privatbank.dto.PrivatbankExchangeRateResponse;

import java.time.LocalDate;

public interface PrivatbankClient {
    PrivatbankExchangeRateResponse getHistoricalExchangeRates(LocalDate date);
}
