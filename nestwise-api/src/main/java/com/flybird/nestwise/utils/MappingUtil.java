package com.flybird.nestwise.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flybird.nestwise.clients.banks.kredobank.dto.CardInfoResponse;
import com.flybird.nestwise.clients.banks.kredobank.dto.KredobankExchangeRateResponse;
import com.flybird.nestwise.clients.banks.kredobank.dto.KredobankTransactionResponse;
import com.flybird.nestwise.clients.banks.kredobank.dto.LoginResponse;
import com.flybird.nestwise.clients.banks.monobank.dto.ClientInfoResponse;
import com.flybird.nestwise.clients.banks.monobank.dto.MonobankExchangeRateResponse;
import com.flybird.nestwise.clients.banks.monobank.dto.MonobankTransactionResponse;
import com.flybird.nestwise.clients.banks.privatbank.dto.PrivatbankExchangeRateResponse;
import com.flybird.nestwise.domain.Account;
import com.flybird.nestwise.domain.Bank;
import com.flybird.nestwise.domain.Card;
import com.flybird.nestwise.domain.Currency;
import com.flybird.nestwise.domain.ExchangeRate;
import com.flybird.nestwise.domain.Goal;
import com.flybird.nestwise.domain.User;
import com.flybird.nestwise.dto.GoalRequestDto;
import com.flybird.nestwise.dto.GoalResponseDto;
import com.flybird.nestwise.dto.banking.BankTransactionDto;
import com.flybird.nestwise.dto.banking.ExchangeRateDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import java.util.function.Function;

import static com.flybird.nestwise.utils.StringUtil.maskCreditCard;
import static com.flybird.nestwise.utils.StringUtil.maskIBAN;
import static java.util.Objects.nonNull;

@RequiredArgsConstructor
@Component
public class MappingUtil {
    public static final Map<String, Integer> CURRENCY_MAPPING = Map.of(
            "UAH", 980,
            "USD", 840,
            "EUR", 978,
            "PLN", 985
    );

    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    public Integer toCurrencyCode(String currency) {
        return CURRENCY_MAPPING.get(currency);
    }

    public Goal toDomain(GoalRequestDto requestDto, Long userId, Function<Long, Goal> goalExtractor, Function<Long, User> userExtractor) {
        var domain = modelMapper.map(requestDto, Goal.class);

        var parentId = requestDto.getParentId();
        if (nonNull(parentId)) {
            domain.setParent(goalExtractor.apply(parentId));
        }
        domain.setUserId(userId);
        domain.setUser(userExtractor.apply(userId));
        domain.setCreatedAt(Instant.now());

        return domain;
    }

    public GoalResponseDto toDto(Goal goal) {
        return modelMapper.map(goal, GoalResponseDto.class);
    }

    public LoginResponse toDto(InputStream inputStream) {
        try {
            return objectMapper.readValue(inputStream, LoginResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public BankTransactionDto toDto(MonobankTransactionResponse transaction) {
        return BankTransactionDto.builder()
                .id(transaction.getId())
                .description(transaction.getDescription())
                .amount(BigDecimal.valueOf(transaction.getAmount()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP))
                .currencyCode(transaction.getCurrencyCode())
                .transactionTime(Instant.ofEpochSecond(transaction.getTime()))
                .build();
    }

    public BankTransactionDto toDto(KredobankTransactionResponse transaction) {
        return BankTransactionDto.builder()
                .id(transaction.getId())
                .description(transaction.getDescription())
                .amount(BigDecimal.valueOf(transaction.getAmountInCents()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP))
                .currencyCode(CURRENCY_MAPPING.get(transaction.getCurrency()))
                .transactionTime(Instant.ofEpochMilli(transaction.getOperationDate()))
                .build();
    }

    public ExchangeRateDto toDto(MonobankExchangeRateResponse rate) {
        return ExchangeRateDto.builder()
                .currencyCodeFrom(rate.getCurrencyCodeA())
                .currencyCodeTo(rate.getCurrencyCodeB())
                .buyRate(rate.getRateBuy())
                .sellRate(rate.getRateSell())
                .date(Instant.ofEpochSecond(rate.getDate())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate())
                .build();
    }

    public ExchangeRateDto toDto(KredobankExchangeRateResponse rate) {
        return ExchangeRateDto.builder()
                .currencyCodeFrom(toCurrencyCode(rate.getCurrency()))
                .currencyCodeTo(toCurrencyCode("UAH"))
                .buyRate(rate.getBuyRate())
                .sellRate(rate.getSellRate())
                .date(LocalDate.parse(rate.getEndDate()))
                .build();
    }

    public ExchangeRateDto toInvertedExchangeRateDto(ExchangeRateDto rate) {
        return ExchangeRateDto.builder()
                .currencyCodeFrom(rate.getCurrencyCodeTo())
                .currencyCodeTo(rate.getCurrencyCodeFrom())
                .buyRate(BigDecimal.ONE.divide(rate.getSellRate(), 6, RoundingMode.HALF_UP))
                .sellRate(BigDecimal.ONE.divide(rate.getBuyRate(), 6, RoundingMode.HALF_UP))
                .date(rate.getDate())
                .build();
    }

    public Account toDomain(ClientInfoResponse.Account apiAccount, Long bankId, Function<Long, Bank> bankExtractor, Long userId, Function<Long, User> userExtractor) {
        var account = Account.builder()
                .bankAccountId(apiAccount.getId())
                .title(apiAccount.getType())
                .currencyCode(apiAccount.getCurrencyCode())
                .balance(apiAccount.getBalance())
                .creditLimit(apiAccount.getCreditLimit())
                .iban(maskIBAN(apiAccount.getIban()))
                .isActive(true)
                .user(userExtractor.apply(userId))
                .bank(bankExtractor.apply(bankId))
                .build();

        var cards = apiAccount.getMaskedPan().stream()
                .map(cardNumber -> Card.builder()
                        .title(apiAccount.getType())
                        .description(null)
                        .cardNumber(maskCreditCard(cardNumber))
                        .build()
                )
                .toList();

        account.addCards(cards);

        return account;
    }

    public Account toDomain(CardInfoResponse.Contract apiAccount, Long bankId, Function<Long, Bank> bankExtractor, Long userId, Function<Long, User> userExtractor) {
        var account = Account.builder()
                .bankAccountId(apiAccount.getId())
                .title(apiAccount.getProductTitle())
                .currencyCode(CURRENCY_MAPPING.get(apiAccount.getMainAccountCurrency()))
                .balance(apiAccount.getBalance())
                .creditLimit(apiAccount.getCreditLimit())
                .iban(maskIBAN(apiAccount.getIban()))
                .isActive(true)
                .user(userExtractor.apply(userId))
                .bank(bankExtractor.apply(bankId))
                .build();

        var cards = apiAccount.getCardsList().stream()
                .map(card -> {
                    var productTitle = getCardAttribute(card, "productTitle");
                    var description = getCardAttribute(card, "card_contract_desc");

                    return Card.builder()
                            .title(productTitle)
                            .description(description)
                            .cardNumber(maskCreditCard(card.getCardNumberMask()))
                            .build();
                })
                .toList();

        account.addCards(cards);

        return account;
    }

    private static String getCardAttribute(CardInfoResponse.Contract.Card card, String attributeName) {
        return card.getAttributes().stream()
                .filter(f -> f.getKey().equals(attributeName))
                .findFirst()
                .map(CardInfoResponse.Contract.Card.Attribute::getValue)
                .orElse(null);
    }

    public ExchangeRateDto toDto(ExchangeRate exchangeRate) {
        return modelMapper.map(exchangeRate, ExchangeRateDto.class);
    }

    public ExchangeRate toDomain(PrivatbankExchangeRateResponse.ExchangeRate exchangeRate, Long bankId, Function<Long, Bank> bankExtractor, Function<Integer, Currency> currencyExtractor, LocalDate date) {
        return ExchangeRate.builder()
                .bank(bankExtractor.apply(bankId))
                .currencyFrom(currencyExtractor.apply(CURRENCY_MAPPING.get(exchangeRate.getBaseCurrency())))
                .currencyTo(currencyExtractor.apply(CURRENCY_MAPPING.get(exchangeRate.getCurrency())))
                .date(date)
                .buyRate(exchangeRate.getPurchaseRate())
                .sellRate(exchangeRate.getSaleRate())
                .build();
    }

    public ExchangeRate toDomain(KredobankExchangeRateResponse exchangeRate, Long bankId, Function<Long, Bank> bankExtractor, Function<Integer, Currency> currencyExtractor, LocalDate date) {
        return ExchangeRate.builder()
                .bank(bankExtractor.apply(bankId))
                .currencyFrom(currencyExtractor.apply(CURRENCY_MAPPING.get("UAH")))
                .currencyTo(currencyExtractor.apply(CURRENCY_MAPPING.get(exchangeRate.getCurrency())))
                .date(date)
                .buyRate(exchangeRate.getBuyRate())
                .sellRate(exchangeRate.getSellRate())
                .build();
    }
}
