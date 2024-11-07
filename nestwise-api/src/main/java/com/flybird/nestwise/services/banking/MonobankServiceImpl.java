package com.flybird.nestwise.services.banking;

import com.flybird.nestwise.clients.banks.monobank.MonobankClient;
import com.flybird.nestwise.clients.banks.monobank.dto.ClientInfoResponse;
import com.flybird.nestwise.domain.Account;
import com.flybird.nestwise.domain.Bank;
import com.flybird.nestwise.dto.banking.AuthType;
import com.flybird.nestwise.dto.banking.BankTransactionDto;
import com.flybird.nestwise.dto.banking.ExchangeRateDto;
import com.flybird.nestwise.dto.banking.LoginRequestDto;
import com.flybird.nestwise.dto.banking.LoginStatusResponseDto;
import com.flybird.nestwise.repositories.BankRepository;
import com.flybird.nestwise.repositories.UserRepository;
import com.flybird.nestwise.services.SessionService;
import com.flybird.nestwise.utils.MappingUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static com.flybird.nestwise.dto.banking.AuthType.TOKEN;
import static com.flybird.nestwise.utils.MappingUtil.CURRENCY_MAPPING;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toMap;

@Service("monobank")
@RequiredArgsConstructor
public class MonobankServiceImpl implements BankService {
    private static final List<AuthType> BANK_LOGIN_TYPES = List.of(TOKEN);

    private final SessionService sessionService;
    private final MonobankClient monobankClient;
    private final UserRepository userRepository;
    private final BankRepository bankRepository;
    private final MappingUtil mappingUtil;
    private Bank bank;

    @PostConstruct
    private void init() {
        bank = bankRepository.findByCode("monobank").orElseThrow(RuntimeException::new);
    }

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
    public Map<Pair<Integer, Integer>, ExchangeRateDto> getCurrentExchangeRates() {
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
    public List<Account> getAccounts(Long userId) {
        var authToken = sessionService.getAuthToken("monobank");

        return monobankClient.getClientInfo(authToken).getAccounts().stream()
                .map(account -> mappingUtil.toDomain(account,
                        bank.getId(), bankRepository::getReferenceById,
                        userId, userRepository::getReferenceById))
                .toList();
    }

    private List<BankTransactionDto> getAccountTransactions(String accountId, long from, long to, String authToken) {
        return monobankClient.getAccountStatement(accountId, from, to, authToken).stream()
                .map(mappingUtil::toDto)
                .toList();
    }

    private LoginStatusResponseDto loginWithToken(String bankId) {
        var session = sessionService.getSession(bankId);

        return new LoginStatusResponseDto(Objects.nonNull(session), TOKEN);
    }
}
