package com.flybird.nestwise.services.banking;

import com.flybird.nestwise.clients.banks.kredobank.KredobankClient;
import com.flybird.nestwise.clients.banks.kredobank.dto.LoginResponseWithToken;
import com.flybird.nestwise.dto.banking.AuthType;
import com.flybird.nestwise.dto.banking.LoginRequestDto;
import com.flybird.nestwise.dto.banking.LoginStatusResponseDto;
import com.flybird.nestwise.services.AuthSession;
import com.flybird.nestwise.services.SessionService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.flybird.nestwise.dto.banking.AuthType.CREDENTIALS;
import static com.flybird.nestwise.dto.banking.AuthType.OTP;
import static java.util.Objects.isNull;

@Service("kredobank")
@AllArgsConstructor
public class KredobankServiceImpl implements BankService {
    private static final List<AuthType> BANK_LOGIN_TYPES = List.of(CREDENTIALS, OTP);

    private final SessionService sessionService;
    private final KredobankClient kredobankClient;

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
}
