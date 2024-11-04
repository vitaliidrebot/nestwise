package com.flybird.nestwise.clients.banks.monobank;

import com.flybird.nestwise.clients.banks.monobank.dto.ClientInfoResponse;
import com.flybird.nestwise.clients.banks.monobank.dto.MonobankExchangeRateResponse;
import com.flybird.nestwise.clients.banks.monobank.dto.MonobankTransactionResponse;

import java.util.List;

public interface MonobankClient {
    List<MonobankExchangeRateResponse> getExchangeRates();

    List<MonobankTransactionResponse> getAccountStatement(String accountId, long from, long to, String authToken);

    ClientInfoResponse getClientInfo(String authToken);
}
