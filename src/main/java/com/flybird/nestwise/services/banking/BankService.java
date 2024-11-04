package com.flybird.nestwise.services.banking;

import com.flybird.nestwise.dto.banking.AuthType;
import com.flybird.nestwise.dto.banking.LoginRequestDto;
import com.flybird.nestwise.dto.banking.LoginStatusResponseDto;

public interface BankService {
    LoginStatusResponseDto bankLogin(String bankId, AuthType type, LoginRequestDto requestDto);
}
