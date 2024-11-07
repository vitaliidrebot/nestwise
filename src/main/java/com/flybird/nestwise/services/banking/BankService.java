package com.flybird.nestwise.services.banking;

import com.flybird.nestwise.domain.Account;
import com.flybird.nestwise.dto.banking.AuthType;
import com.flybird.nestwise.dto.banking.BankTransactionDto;
import com.flybird.nestwise.dto.banking.ExchangeRateDto;
import com.flybird.nestwise.dto.banking.LoginRequestDto;
import com.flybird.nestwise.dto.banking.LoginStatusResponseDto;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public interface BankService {
    LoginStatusResponseDto bankLogin(String bankId, AuthType type, LoginRequestDto requestDto);

    Map<Pair<Integer, Integer>, ExchangeRateDto> getCurrentExchangeRates();

    Map<String, List<BankTransactionDto>> getTransactions(long from, long to);

    List<Account> getAccounts(Long userId);
}
