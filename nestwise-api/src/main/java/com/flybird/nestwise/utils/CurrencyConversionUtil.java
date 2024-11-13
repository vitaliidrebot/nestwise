package com.flybird.nestwise.utils;

import com.flybird.nestwise.dto.banking.ExchangeRateDto;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import static com.flybird.nestwise.utils.MappingUtil.CURRENCY_MAPPING;

public class CurrencyConversionUtil {
    public static <T> BigDecimal toCurrency(String currency, T account, Function<T, BigDecimal> balanceFunc, ToIntFunction<T> currencyCodeFunc, Map<Pair<Integer, Integer>, ExchangeRateDto> exchangeRates) {
        var currencyCode = CURRENCY_MAPPING.get(currency);
        var balance = balanceFunc.apply(account);
        var accountCurrencyCode = currencyCodeFunc.applyAsInt(account);

        if (!Objects.equals(currencyCode, accountCurrencyCode)) {
            return balance.multiply(exchangeRates.get(Pair.of(accountCurrencyCode, currencyCode)).getSellRate());
        }

        return balance;
    }
}
