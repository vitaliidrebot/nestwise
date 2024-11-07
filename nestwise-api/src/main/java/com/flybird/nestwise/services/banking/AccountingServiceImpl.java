package com.flybird.nestwise.services.banking;

import com.flybird.nestwise.domain.Account;
import com.flybird.nestwise.dto.banking.AccountBalance;
import com.flybird.nestwise.dto.banking.AuthType;
import com.flybird.nestwise.dto.banking.BankBalance;
import com.flybird.nestwise.dto.banking.BankBalanceResponseDto;
import com.flybird.nestwise.dto.banking.BankTransactionDto;
import com.flybird.nestwise.dto.banking.ExchangeRateDto;
import com.flybird.nestwise.dto.banking.LoginRequestDto;
import com.flybird.nestwise.dto.banking.LoginStatusResponseDto;
import com.flybird.nestwise.repositories.AccountRepository;
import com.flybird.nestwise.repositories.BankRepository;
import com.flybird.nestwise.repositories.ExchangeRateRepository;
import com.flybird.nestwise.repositories.UserRepository;
import com.flybird.nestwise.utils.CurrencyConversionUtil;
import com.flybird.nestwise.utils.MappingUtil;
import com.flybird.nestwise.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.flybird.nestwise.utils.MappingUtil.CURRENCY_MAPPING;
import static com.flybird.nestwise.utils.StringUtil.maskIBAN;

@Service
@RequiredArgsConstructor
public class AccountingServiceImpl implements AccountingService {
    private final Map<String, BankService> bankServices;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final BankRepository bankRepository;
    private final ExchangeRateRepository exchangeRateRepository;
    private final MappingUtil mappingUtil;

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
                                    .iban(maskIBAN(entry.getKey()))
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
        var exchangeRates = bankServices.get(bankId).getCurrentExchangeRates();

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
        var username = SecurityUtil.getUsername();
        var user = userRepository.findByUsername(username)
                .orElseThrow(RuntimeException::new);
        var userId = user.getId();

        var accountMap = accountRepository.findByUserId(userId).stream()
                .collect(Collectors.groupingBy(a -> a.getBank().getCode()));

        var banks = bankServices.entrySet().stream()
                .filter(b -> bankIds.isEmpty() || bankIds.contains(b.getKey()))
                .map(bankService -> toBankBalance(currency, bankService, accountMap.getOrDefault(bankService.getKey(), List.of())))
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

    private BankBalance toBankBalance(String currency, Map.Entry<String, BankService> bankService, List<Account> accounts) {
        var exchangeRates = bankService.getValue().getCurrentExchangeRates();

        var cardBalances = accounts.stream()
                .map(account -> AccountBalance.builder()
                        .iban(maskIBAN(account.getIban()))
                        .balance(toCurrency(currency, account, exchangeRates))
                        .build())
                .collect(Collectors.toList());

        var totalBalance = cardBalances.stream()
                .map(AccountBalance::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return BankBalance.builder()
                .bankId(bankService.getKey())
                .balance(totalBalance)
                .accounts(cardBalances)
                .build();
    }

    private static BigDecimal toCurrency(String currency, Account account, Map<Pair<Integer, Integer>, ExchangeRateDto> exchangeRates) {
        Function<Account, BigDecimal> balanceFunc = (account1) -> BigDecimal.valueOf(account1.getBalance() - account1.getCreditLimit()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        Function<Account, Integer> currencyCodeFunc = Account::getCurrencyCode;

        return CurrencyConversionUtil.toCurrency(currency, account, balanceFunc, currencyCodeFunc, exchangeRates);
    }

    @Override
    public void syncAccounts(Set<String> bankIds) {
        bankServices.entrySet().stream()
                .filter(b -> bankIds.isEmpty() || bankIds.contains(b.getKey()))
                .forEach(this::updateBankAccounts);
    }

    @Override
    public List<ExchangeRateDto> getExchangeRatesHistory(String bankId, String currency, LocalDate from, LocalDate to) {
        var bank = bankRepository.findByCode(bankId).orElseThrow(RuntimeException::new);
        var currencyCodeFrom = mappingUtil.toCurrencyCode("UAH");
        var currencyCodeTo = mappingUtil.toCurrencyCode(currency);

        return exchangeRateRepository.getExchangeRates(bank.getId(), currencyCodeFrom, currencyCodeTo, from, to).stream()
                .map(mappingUtil::toDto)
                .toList();
    }

    private List<Account> updateBankAccounts(Map.Entry<String, BankService> bankService) {
        var username = SecurityUtil.getUsername();
        var user = userRepository.findByUsername(username)
                .orElseThrow(RuntimeException::new);
        var userId = user.getId();
        var apiAccounts = bankService.getValue().getAccounts(userId);

        return updateAccountsInDB(userId, apiAccounts);
    }

    private List<Account> updateAccountsInDB(Long userId, List<Account> apiAccounts) {
        var dbAccounts = accountRepository.findByUserId(userId);

        var dbAccountMap = dbAccounts.stream()
                .collect(Collectors.toMap(Account::getBankAccountId, Function.identity()));

        var apiAccountMap = apiAccounts.stream()
                .collect(Collectors.toMap(Account::getBankAccountId, Function.identity()));

        var bankAccountIds = new HashSet<>(apiAccountMap.keySet());

        var accountsToInsert = new ArrayList<Account>();
        var accountsToUpdate = new ArrayList<Account>();

        for (var bankAccountId : bankAccountIds) {
            var dbAccount = dbAccountMap.get(bankAccountId);
            var apiAccount = apiAccountMap.get(bankAccountId);

            if (dbAccount == null) {
                accountsToInsert.add(apiAccount);
            } else if (!apiAccount.equals(dbAccount)) {
                dbAccount.setBalance(apiAccount.getBalance());
                dbAccount.setCreditLimit(apiAccount.getCreditLimit());
                dbAccount.setLastTransactionDate(apiAccount.getLastTransactionDate());
                dbAccount.setIsActive(apiAccount.getIsActive());
                accountsToUpdate.add(dbAccount);
            }
        }

        var newAccounts = accountRepository.saveAll(accountsToInsert);
        var updatedAccounts = accountRepository.saveAll(accountsToUpdate);

        return Stream.concat(newAccounts.stream(), updatedAccounts.stream()).toList();
    }
}
