package com.flybird.nestwise.services;

import com.flybird.nestwise.clients.banks.monobank.MonobankClient;
import com.flybird.nestwise.clients.banks.monobank.dto.MonobankTransactionResponse;
import com.flybird.nestwise.config.settings.MonobankSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {
    private final MonobankClient monobankClient;
    private final MonobankSettings monobankSettings;

    /**
     * Calculates the total balance by summing all operations with money on the bank account.
     * @return the total balance
     */
    @Override
    public double calculateTotalBalance(String accountId, long from, long to) {
        List<MonobankTransactionResponse> accountStatement = monobankClient.getAccountStatement(accountId, from, to, monobankSettings.getToken());
        return accountStatement.stream()
                .mapToDouble(MonobankTransactionResponse::getAmount)
                .sum();
    }
}
