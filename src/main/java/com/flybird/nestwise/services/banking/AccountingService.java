package com.flybird.nestwise.services.banking;

import com.flybird.nestwise.dto.banking.AuthType;
import com.flybird.nestwise.dto.banking.BankBalanceResponseDto;
import com.flybird.nestwise.dto.banking.ExchangeRateDto;
import com.flybird.nestwise.dto.banking.LoginRequestDto;
import com.flybird.nestwise.dto.banking.LoginStatusResponseDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface AccountingService {
    LoginStatusResponseDto bankLogin(String bankId, AuthType type, LoginRequestDto requestDto);

    BankBalanceResponseDto calculateBalanceChange(long from, long to, String currency, Set<String> bankIds);

    BankBalanceResponseDto calculateCurrentBalance(String currency, Set<String> bankIds);

    void syncAccounts(Set<String> bankIds);

    List<ExchangeRateDto> getExchangeRatesHistory(String bankId, String currency, LocalDate from, LocalDate to);
}
