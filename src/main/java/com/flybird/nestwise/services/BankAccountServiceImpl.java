package com.flybird.nestwise.services;

import com.flybird.nestwise.clients.banks.monobank.MonobankClient;
import com.flybird.nestwise.clients.banks.monobank.dto.AccountStatementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {
    private final MonobankClient monobankClient;

    /**
     * Calculates the total balance by summing all operations with money on the bank account.
     * @return the total balance
     */
    @Override
    public double calculateTotalBalance(String accountId, long from, long to) {
        List<AccountStatementResponse> accountStatement = monobankClient.getAccountStatement(accountId, from, to);
        return accountStatement.stream()
                .mapToDouble(AccountStatementResponse::getAmount)
                .sum();
    }
}
