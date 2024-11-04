package com.flybird.nestwise.services.banking;

import com.flybird.nestwise.dto.banking.AccountBalance;
import com.flybird.nestwise.dto.banking.AuthType;
import com.flybird.nestwise.dto.banking.BankTransactionDto;
import com.flybird.nestwise.dto.banking.ExchangeRateDto;
import com.flybird.nestwise.dto.banking.LoginRequestDto;
import com.flybird.nestwise.dto.banking.LoginStatusResponseDto;

import java.util.List;
import java.util.Map;

public interface BankService {
    LoginStatusResponseDto bankLogin(String bankId, AuthType type, LoginRequestDto requestDto);

    Map<Integer, ExchangeRateDto> getExchangeRates();

    Map<String, List<BankTransactionDto>> getTransactions(long from, long to);

    List<AccountBalance> getAccounts(String currency);
}
