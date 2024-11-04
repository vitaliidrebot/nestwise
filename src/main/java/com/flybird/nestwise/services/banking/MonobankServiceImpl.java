package com.flybird.nestwise.services.banking;

import com.flybird.nestwise.clients.banks.monobank.MonobankClient;
import com.flybird.nestwise.clients.banks.monobank.dto.ClientInfoResponse;
import com.flybird.nestwise.dto.banking.AccountBalance;
import com.flybird.nestwise.dto.banking.AuthType;
import com.flybird.nestwise.dto.banking.BankTransactionDto;
import com.flybird.nestwise.dto.banking.ExchangeRateDto;
import com.flybird.nestwise.dto.banking.LoginRequestDto;
import com.flybird.nestwise.dto.banking.LoginStatusResponseDto;
import com.flybird.nestwise.services.SessionService;
import com.flybird.nestwise.utils.CurrencyConversionUtil;
import com.flybird.nestwise.utils.MappingUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.flybird.nestwise.dto.banking.AuthType.TOKEN;
import static com.flybird.nestwise.utils.MappingUtil.CURRENCY_MAPPING;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toMap;

@Service("monobank")
@AllArgsConstructor
public class MonobankServiceImpl implements BankService {
    private static final List<AuthType> BANK_LOGIN_TYPES = List.of(TOKEN);

    private final SessionService sessionService;
    private final MonobankClient monobankClient;
    private final MappingUtil mappingUtil;

    @Override
    public LoginStatusResponseDto bankLogin(String bankId, AuthType type, LoginRequestDto requestDto) {
        if (isNull(type)) {
            type = BANK_LOGIN_TYPES.getFirst();
        }

        return switch (type) {
            case TOKEN -> loginWithToken(bankId);
            default -> throw new RuntimeException("Login type '" + type + "' not supported");
        };
    }

    @Override
    public Map<Pair<Integer, Integer>, ExchangeRateDto> getExchangeRates() {
        var exchangeRates = monobankClient.getExchangeRates();
        var uahCode = CURRENCY_MAPPING.get("UAH");

        return exchangeRates.stream()
                .filter(rate -> CURRENCY_MAPPING.containsValue(rate.getCurrencyCodeA()) && rate.getCurrencyCodeB().equals(uahCode))
                .map(mappingUtil::toDto)
                .map(rate -> List.of(rate, mappingUtil.toInvertedExchangeRateDto(rate)))
                .flatMap(Collection::stream)
                .collect(toMap(rate -> Pair.of(rate.getCurrencyCodeFrom(), rate.getCurrencyCodeTo()), Function.identity()));
    }

    @Override
    public Map<String, List<BankTransactionDto>> getTransactions(long from, long to) {
        var authToken = sessionService.getAuthToken("monobank");
        var clientInfo = monobankClient.getClientInfo(authToken);

        return clientInfo.getAccounts().stream()
                // TODO: temporary ignore other cards to avoid rate limiting
                .filter(account -> !account.getType().equals("madeInUkraine") && !account.getType().equals("eAid"))
                .collect(toMap(ClientInfoResponse.Account::getId, account -> getAccountTransactions(account.getId(), from, to, authToken)));
    }

    @Override
    public List<AccountBalance> getAccounts(String currency) {
        var authToken = sessionService.getAuthToken("monobank");
        var exchangeRates = getExchangeRates();

        return monobankClient.getClientInfo(authToken).getAccounts().stream()
                .map(account -> AccountBalance.builder()
                        .accountId(account.getId())
                        .balance(toCurrency(currency, account, exchangeRates))
                        .build())
                .collect(Collectors.toList());
    }

    private List<BankTransactionDto> getAccountTransactions(String accountId, long from, long to, String authToken) {
        return monobankClient.getAccountStatement(accountId, from, to, authToken).stream()
                .map(mappingUtil::toDto)
                .toList();
    }

    private static BigDecimal toCurrency(String currency, ClientInfoResponse.Account account, Map<Pair<Integer, Integer>, ExchangeRateDto> exchangeRates) {
        Function<ClientInfoResponse.Account, BigDecimal> balanceFunc = (account1) -> BigDecimal.valueOf(account1.getBalance() - account1.getCreditLimit()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        Function<ClientInfoResponse.Account, Integer> currencyCodeFunc = ClientInfoResponse.Account::getCurrencyCode;

        return CurrencyConversionUtil.toCurrency(currency, account, balanceFunc, currencyCodeFunc, exchangeRates);
    }

    private LoginStatusResponseDto loginWithToken(String bankId) {
        var session = sessionService.getSession(bankId);

        return new LoginStatusResponseDto(Objects.nonNull(session), TOKEN);
    }
}
