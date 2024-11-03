package com.flybird.nestwise.services.banking;

import com.flybird.nestwise.dto.banking.AuthType;
import com.flybird.nestwise.dto.banking.LoginRequestDto;
import com.flybird.nestwise.dto.banking.LoginStatusResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccountingServiceImpl implements AccountingService {
    private final Map<String, BankService> bankServices;

    @Override
    public LoginStatusResponseDto bankLogin(String bankId, AuthType authType, LoginRequestDto requestDto) {
        return bankServices.get(bankId).bankLogin(bankId, authType, requestDto);
    }
}
