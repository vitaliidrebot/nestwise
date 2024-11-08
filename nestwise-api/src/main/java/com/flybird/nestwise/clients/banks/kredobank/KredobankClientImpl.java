package com.flybird.nestwise.clients.banks.kredobank;

import com.flybird.nestwise.clients.banks.kredobank.dto.CardInfoResponse;
import com.flybird.nestwise.clients.banks.kredobank.dto.CompleteOtpChallengeRequest;
import com.flybird.nestwise.clients.banks.kredobank.dto.InitiateOtpChallengeResponse;
import com.flybird.nestwise.clients.banks.kredobank.dto.KredobankExchangeRateResponse;
import com.flybird.nestwise.clients.banks.kredobank.dto.KredobankTransactionResponse;
import com.flybird.nestwise.clients.banks.kredobank.dto.LoginRequest;
import com.flybird.nestwise.clients.banks.kredobank.dto.LoginResponse;
import com.flybird.nestwise.clients.banks.kredobank.dto.LoginResponseWithToken;
import com.flybird.nestwise.config.settings.KredobankSettings;
import com.flybird.nestwise.utils.MappingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.flybird.nestwise.utils.AppConstants.KREDOBANK_ACCOUNT_HISTORY_PATH;
import static com.flybird.nestwise.utils.AppConstants.KREDOBANK_ACCOUNT_INFO_PATH;
import static com.flybird.nestwise.utils.AppConstants.KREDOBANK_AUTH_COMPLETE_OTP_CHALLENGE_PATH;
import static com.flybird.nestwise.utils.AppConstants.KREDOBANK_AUTH_CREDENTIALS_PATH;
import static com.flybird.nestwise.utils.AppConstants.KREDOBANK_AUTH_INITIATE_OTP_CHALLENGE_PATH;
import static com.flybird.nestwise.utils.AppConstants.KREDOBANK_AUTH_SESSION_PATH;
import static com.flybird.nestwise.utils.AppConstants.KREDOBANK_CURRENCY_PATH;
import static org.apache.tomcat.util.http.fileupload.FileUploadBase.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@RequiredArgsConstructor
public class KredobankClientImpl implements KredobankClient {
    // TODO: Check thread-safety
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneId.systemDefault());

    private final RestClient restClient;
    private final KredobankSettings kredobankSettings;
    private final MappingUtil mappingUtil;

    @Override
    public List<KredobankExchangeRateResponse> getExchangeRates(Set<String> currencies, LocalDate date) {
        var url = UriComponentsBuilder.fromUriString(kredobankSettings.getUrl())
                .path(KREDOBANK_CURRENCY_PATH)
                .queryParam("date", DATE_FORMATTER.format(date))
                .queryParam("currency", String.join(",", currencies))
                .build()
                .toUri();

        return restClient.get()
                .uri(url)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<KredobankExchangeRateResponse>>() {})
                .getBody();
    }

    @Override
    public LoginResponseWithToken loginWithCredentials() {
        var url = UriComponentsBuilder.fromUriString(kredobankSettings.getUrl())
                .path(KREDOBANK_AUTH_CREDENTIALS_PATH)
                .build()
                .toUri();

        var request = new LoginRequest(kredobankSettings.getUsername(), kredobankSettings.getPassword());

        var responseEntity = restClient.post()
                .uri(url)
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .body(request)
                .retrieve()
                .toEntity(LoginResponse.class);

        var authToken = responseEntity.getHeaders().get(AUTHORIZATION).getFirst();

        return new LoginResponseWithToken(responseEntity.getBody(), authToken);
    }

    @Override
    public InitiateOtpChallengeResponse initiateOtpChallenge(String authToken) {
        var url = UriComponentsBuilder.fromUriString(kredobankSettings.getUrl())
                .path(KREDOBANK_AUTH_INITIATE_OTP_CHALLENGE_PATH)
                .queryParam("userName", kredobankSettings.getUsername())
                .build()
                .toUri();

        return restClient.get()
                .uri(url)
                .header(AUTHORIZATION, authToken)
                .retrieve()
                .toEntity(InitiateOtpChallengeResponse.class)
                .getBody();
    }

    @Override
    public LoginResponseWithToken completeOtpChallenge(String otp, String challengeId, String authToken) {
        var url = UriComponentsBuilder.fromUriString(kredobankSettings.getUrl())
                .path(KREDOBANK_AUTH_COMPLETE_OTP_CHALLENGE_PATH)
                .build()
                .toUri();

        var request = new CompleteOtpChallengeRequest(otp, new CompleteOtpChallengeRequest.Challenge(challengeId));

        var responseEntity = restClient.post()
                .uri(url)
                .header(AUTHORIZATION, authToken)
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .body(request)
                .retrieve()
                .toEntity(LoginResponse.class);

        var respAuthToken = responseEntity.getHeaders().get(AUTHORIZATION).getFirst();

        return new LoginResponseWithToken(responseEntity.getBody(), respAuthToken);
    }

    @Override
    public CardInfoResponse getCards(String authToken) {
        var url = UriComponentsBuilder.fromUriString(kredobankSettings.getUrl())
                .path(KREDOBANK_ACCOUNT_INFO_PATH)
                .build()
                .toUri();

        return restClient.get()
                .uri(url)
                .header(AUTHORIZATION, authToken)
                .retrieve()
                .toEntity(CardInfoResponse.class)
                .getBody();
    }

    @Override
    public List<KredobankTransactionResponse> getCardHistory(String accountId, long from, long to, String authToken) {
        var url = UriComponentsBuilder.fromUriString(kredobankSettings.getUrl())
                .path(KREDOBANK_ACCOUNT_HISTORY_PATH)
                .queryParam("from", DATE_FORMATTER.format(Instant.ofEpochSecond(from)))
                .queryParam("to", DATE_FORMATTER.format(Instant.ofEpochSecond(to)))
                .queryParam("withCard", true)
                .build(accountId);

        return restClient.get()
                .uri(url)
                .header(AUTHORIZATION, authToken)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<KredobankTransactionResponse>>() {})
                .getBody();
    }

    @Override
    public Optional<LoginResponseWithToken> getAuthSession() {
        var url = UriComponentsBuilder.fromUriString(kredobankSettings.getUrl())
                .path(KREDOBANK_AUTH_SESSION_PATH)
                .build()
                .toUri();

        return restClient.get()
                .uri(url)
                .header(AUTHORIZATION, kredobankSettings.getToken())
                .exchange((_, response) -> {
                    if (response.getStatusCode().value() == 401) {
                        return Optional.empty();
                    }
                    else {
                        var respAuthToken = response.getHeaders().get(AUTHORIZATION).getFirst();

                        var loginResponse = mappingUtil.toDto(response.getBody());
                        return Optional.of(new LoginResponseWithToken(loginResponse, respAuthToken));
                    }
                });
    }
}