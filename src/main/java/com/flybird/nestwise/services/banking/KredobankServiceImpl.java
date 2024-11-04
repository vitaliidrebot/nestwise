package com.flybird.nestwise.services.banking;

import com.flybird.nestwise.clients.banks.kredobank.KredobankClient;
import com.flybird.nestwise.clients.banks.kredobank.dto.CardInfoResponse;
import com.flybird.nestwise.clients.banks.kredobank.dto.LoginResponseWithToken;
import com.flybird.nestwise.dto.banking.AccountBalance;
import com.flybird.nestwise.dto.banking.AuthType;
import com.flybird.nestwise.dto.banking.BankTransactionDto;
import com.flybird.nestwise.dto.banking.ExchangeRateDto;
import com.flybird.nestwise.dto.banking.LoginRequestDto;
import com.flybird.nestwise.dto.banking.LoginStatusResponseDto;
import com.flybird.nestwise.services.AuthSession;
import com.flybird.nestwise.services.SessionService;
import com.flybird.nestwise.utils.CurrencyConversionUtil;
import com.flybird.nestwise.utils.MappingUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.flybird.nestwise.dto.banking.AuthType.CREDENTIALS;
import static com.flybird.nestwise.dto.banking.AuthType.OTP;
import static com.flybird.nestwise.utils.MappingUtil.CURRENCY_MAPPING;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;

@Service("kredobank")
@AllArgsConstructor
public class KredobankServiceImpl implements BankService {
    private static final List<AuthType> BANK_LOGIN_TYPES = List.of(CREDENTIALS, OTP);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy").withZone(ZoneId.systemDefault());

    private final SessionService sessionService;
    private final KredobankClient kredobankClient;
    private final MappingUtil mappingUtil;

    @Override
    public LoginStatusResponseDto bankLogin(String bankId, AuthType type, LoginRequestDto requestDto) {
        if (isNull(type)) {
            type = BANK_LOGIN_TYPES.getFirst();
        }

        return switch (type) {
            case CREDENTIALS -> loginWithCredentials(bankId);
            case OTP -> loginWithOtp(bankId, requestDto.getOtp());
            default -> throw new RuntimeException("Login type '" + type + "' not supported");
        };
    }

    @Override
    public Map<Pair<Integer, Integer>, ExchangeRateDto> getExchangeRates() {
        var exchangeRates = kredobankClient.getExchangeRates(CURRENCY_MAPPING.keySet());

        return exchangeRates.stream()
                .map(mappingUtil::toDto)
                .filter(rate -> !Objects.equals(rate.getCurrencyCodeFrom(), rate.getCurrencyCodeTo()))
                .filter(rate -> nonNull(rate.getBuyRate()) && nonNull(rate.getSellRate()))
                .map(rate -> List.of(rate, mappingUtil.toInvertedExchangeRateDto(rate)))
                .flatMap(Collection::stream)
                .collect(toMap(rate -> Pair.of(rate.getCurrencyCodeFrom(), rate.getCurrencyCodeTo()), Function.identity()));
    }

    @Override
    public Map<String, List<BankTransactionDto>> getTransactions(long from, long to) {
        var authToken = sessionService.getAuthToken("kredobank");
        var cardInfo = kredobankClient.getCards(authToken);

        return cardInfo.getContracts().stream()
                .filter(CardInfoResponse.Contract::getIsActiveProduct)
                .filter(account -> isNull(account.getLastTransactionDate()) || LocalDate.parse(account.getLastTransactionDate(), DATE_FORMATTER)
                        .atStartOfDay()
                        .toInstant(ZoneOffset.UTC)
                        .isAfter(Instant.ofEpochSecond(from))
                )
                .collect(toMap(CardInfoResponse.Contract::getId, account -> getAccountTransactions(account.getId(), from, to, authToken)));
    }

    @Override
    public List<AccountBalance> getAccounts(String currency) {
        var authToken = sessionService.getAuthToken("kredobank");
        var exchangeRates = getExchangeRates();

        return kredobankClient.getCards(authToken).getContracts().stream()
                .filter(f -> CURRENCY_MAPPING.containsKey(f.getMainAccountCurrency()))
                .map(account -> AccountBalance.builder()
                        .accountId(account.getId())
                        .balance(toCurrency(currency, account, exchangeRates))
                        .build()
                )
                .collect(Collectors.toList());
    }

    private static BigDecimal toCurrency(String currency, CardInfoResponse.Contract account, Map<Pair<Integer, Integer>, ExchangeRateDto> exchangeRates) {
        Function<CardInfoResponse.Contract, BigDecimal> balanceFunc = (account1) -> BigDecimal.valueOf(account1.getBalance() - account1.getCreditLimit()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        Function<CardInfoResponse.Contract, Integer> currencyCodeFunc = (account1) -> CURRENCY_MAPPING.get(account1.getMainAccountCurrency());

        return CurrencyConversionUtil.toCurrency(currency, account, balanceFunc, currencyCodeFunc, exchangeRates);
    }

    private LoginStatusResponseDto loginWithCredentials(String bankId) {
        boolean isAuthCompleted = false;
        Optional<LoginResponseWithToken> authSessionOpt = kredobankClient.getAuthSession();

        AuthSession authSession;
        if (authSessionOpt.isEmpty()) {
            var loginResponse = kredobankClient.loginWithCredentials();
            var authToken = loginResponse.getAuthToken();
            isAuthCompleted = loginResponse.getLoginResponse().isAuthCompleted();
            String challengeId = null;
            if (!isAuthCompleted) {
                challengeId = kredobankClient.initiateOtpChallenge(authToken).getChallengeId();
            }
            authSession = new AuthSession(authToken, challengeId, isAuthCompleted);
        } else {
            authSession = new AuthSession(authSessionOpt.get().getAuthToken(), null, true);
        }

        sessionService.putSession(bankId, authSession);

        return new LoginStatusResponseDto(isAuthCompleted, OTP);
    }

    private LoginStatusResponseDto loginWithOtp(String bankId, String otp) {
        var session = Optional.ofNullable(sessionService.getSession(bankId)).orElseThrow(() -> new RuntimeException("Session not found"));
        var authSession = kredobankClient.completeOtpChallenge(otp, session.getOtpChallengeId(), session.getAuthToken());

        sessionService.putSession(bankId, new AuthSession(authSession.getAuthToken(), session.getOtpChallengeId(), true));

        return new LoginStatusResponseDto(true, OTP);
    }

    private List<BankTransactionDto> getAccountTransactions(String accountId, long from, long to, String authToken) {
        return kredobankClient.getCardHistory(accountId, from, to, authToken).stream()
                .map(mappingUtil::toDto)
                .toList();
    }
}
