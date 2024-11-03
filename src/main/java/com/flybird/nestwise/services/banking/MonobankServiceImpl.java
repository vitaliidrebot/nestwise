package com.flybird.nestwise.services.banking;

import com.flybird.nestwise.dto.banking.AuthType;
import com.flybird.nestwise.dto.banking.LoginRequestDto;
import com.flybird.nestwise.dto.banking.LoginStatusResponseDto;
import com.flybird.nestwise.services.SessionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.flybird.nestwise.dto.banking.AuthType.TOKEN;

@Service("monobank")
@AllArgsConstructor
public class MonobankServiceImpl implements BankService {
    private final SessionService sessionService;

    @Override
    public LoginStatusResponseDto bankLogin(String bankId, AuthType type, LoginRequestDto requestDto) {
        return loginWithToken(bankId);
    }

    private LoginStatusResponseDto loginWithToken(String bankId) {
        var session = sessionService.getSession(bankId);

        return new LoginStatusResponseDto(Objects.nonNull(session), TOKEN);
    }
}
