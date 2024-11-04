package com.flybird.nestwise.clients.banks.kredobank;

import com.flybird.nestwise.clients.banks.kredobank.dto.CardInfoResponse;
import com.flybird.nestwise.clients.banks.kredobank.dto.KredobankExchangeRateResponse;
import com.flybird.nestwise.clients.banks.kredobank.dto.InitiateOtpChallengeResponse;
import com.flybird.nestwise.clients.banks.kredobank.dto.KredobankTransactionResponse;
import com.flybird.nestwise.clients.banks.kredobank.dto.LoginResponseWithToken;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface KredobankClient {
    List<KredobankExchangeRateResponse> getExchangeRates(Set<String> currencies);

    LoginResponseWithToken loginWithCredentials();

    InitiateOtpChallengeResponse initiateOtpChallenge(String authToken);

    LoginResponseWithToken completeOtpChallenge(String otp, String challengeId, String authToken);

    CardInfoResponse getCards(String authToken);

    List<KredobankTransactionResponse> getCardHistory(String accountId, long from, long to, String authToken);

    Optional<LoginResponseWithToken> getAuthSession();
}
