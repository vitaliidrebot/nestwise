package com.flybird.nestwise.clients.banks.monobank;

import com.flybird.nestwise.clients.banks.monobank.dto.AccountStatementResponse;

import java.util.List;

public abstract class MonobankClient {
    public abstract List<AccountStatementResponse> getAccountStatement(String accountId, long from, long to);
}
