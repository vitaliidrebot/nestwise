package com.flybird.nestwise.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flybird.nestwise.clients.banks.kredobank.dto.KredobankExchangeRateResponse;
import com.flybird.nestwise.clients.banks.kredobank.dto.KredobankTransactionResponse;
import com.flybird.nestwise.clients.banks.kredobank.dto.LoginResponse;
import com.flybird.nestwise.clients.banks.monobank.dto.MonobankExchangeRateResponse;
import com.flybird.nestwise.clients.banks.monobank.dto.MonobankTransactionResponse;
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

import static java.util.Objects.nonNull;

@RequiredArgsConstructor
@Component
public class MappingUtil {
    public static final Map<String, Integer> CURRENCY_MAPPING = Map.of(
            "UAH", 980,
            "USD", 840
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
        domain.setCreatedBy(userId);
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
}
