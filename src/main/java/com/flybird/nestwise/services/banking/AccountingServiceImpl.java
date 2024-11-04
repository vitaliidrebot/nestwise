package com.flybird.nestwise.services.banking;

import com.flybird.nestwise.dto.banking.AccountBalance;
import com.flybird.nestwise.dto.banking.AuthType;
import com.flybird.nestwise.dto.banking.BankBalance;
import com.flybird.nestwise.dto.banking.BankBalanceResponseDto;
import com.flybird.nestwise.dto.banking.BankTransactionDto;
import com.flybird.nestwise.dto.banking.ExchangeRateDto;
import com.flybird.nestwise.dto.banking.LoginRequestDto;
import com.flybird.nestwise.dto.banking.LoginStatusResponseDto;
import com.flybird.nestwise.utils.CurrencyConversionUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static com.flybird.nestwise.utils.MappingUtil.CURRENCY_MAPPING;

@Service
@RequiredArgsConstructor
public class AccountingServiceImpl implements AccountingService {
    private final Map<String, BankService> bankServices;

    @Override
    public LoginStatusResponseDto bankLogin(String bankId, AuthType authType, LoginRequestDto requestDto) {
        return bankServices.get(bankId).bankLogin(bankId, authType, requestDto);
    }

    @Override
    public BankBalanceResponseDto calculateBalanceChange(long from, long to, String currency, Set<String> bankIds) {
        if (!CURRENCY_MAPPING.containsKey(currency)) {
            throw new RuntimeException("Unsupported currency: " + currency);
        }

        var banks = bankServices.entrySet().stream()
                .filter(b -> bankIds.isEmpty() || bankIds.contains(b.getKey()))
                .map(bankService -> {
                    List<AccountBalance> accountBalances = bankService.getValue().getTransactions(from, to).entrySet().stream()
                            .map(entry -> AccountBalance.builder()
                                    .accountId(entry.getKey())
                                    .balance(calculateBalance(entry.getValue(), bankService.getKey(), currency))
                                    .build()
                            )
                            .toList();

                    var totalBalance = accountBalances.stream()
                            .map(AccountBalance::getBalance)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return BankBalance.builder()
                            .bankId(bankService.getKey())
                            .balance(totalBalance)
                            .accounts(accountBalances)
                            .build();
                })
                .toList();

        var balance = banks.stream()
                .map(BankBalance::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return BankBalanceResponseDto.builder()
                .currency(currency)
                .balance(balance)
                .banks(banks)
                .build();
    }

    private BigDecimal calculateBalance(List<BankTransactionDto> transactions, String bankId, String currency) {
        var exchangeRates = bankServices.get(bankId).getExchangeRates();

        return transactions.stream()
                .map(transaction -> toCurrency(currency, transaction, exchangeRates))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static BigDecimal toCurrency(String currency, BankTransactionDto transaction, Map<Pair<Integer, Integer>, ExchangeRateDto> exchangeRates) {
        Function<BankTransactionDto, BigDecimal> balanceFunc = BankTransactionDto::getAmount;
        Function<BankTransactionDto, Integer> currencyCodeFunc = BankTransactionDto::getCurrencyCode;

        return CurrencyConversionUtil.toCurrency(currency, transaction, balanceFunc, currencyCodeFunc, exchangeRates);
    }

    @Override
    public BankBalanceResponseDto calculateCurrentBalance(String currency, Set<String> bankIds) {
        var banks = bankServices.entrySet().stream()
                .filter(b -> bankIds.isEmpty() || bankIds.contains(b.getKey()))
                .map(bankService -> {
                    var accounts = bankService.getValue().getAccounts(currency);

                    var totalBalance = accounts.stream()
                            .map(AccountBalance::getBalance)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return BankBalance.builder()
                            .bankId(bankService.getKey())
                            .balance(totalBalance)
                            .accounts(accounts)
                            .build();
                })
                .toList();

        var balance = banks.stream()
                .map(BankBalance::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return BankBalanceResponseDto.builder()
                .currency(currency)
                .balance(balance)
                .banks(banks)
                .build();
    }
}
