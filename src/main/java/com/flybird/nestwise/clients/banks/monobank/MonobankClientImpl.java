package com.flybird.nestwise.clients.banks.monobank;

import com.flybird.nestwise.clients.banks.monobank.dto.ClientInfoResponse;
import com.flybird.nestwise.clients.banks.monobank.dto.ExchangeRateResponse;
import com.flybird.nestwise.clients.banks.monobank.dto.MonobankTransactionResponse;
import com.flybird.nestwise.config.settings.MonobankSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static com.flybird.nestwise.utils.AppConstants.*;

@Component
@RequiredArgsConstructor
public class MonobankClientImpl implements MonobankClient {
    private final RestClient restClient;
    private final MonobankSettings monobankSettings;

    @Override
    public List<MonobankTransactionResponse> getAccountStatement(String account, long from, long to, String authToken) {
        var url = UriComponentsBuilder.fromUriString(monobankSettings.getUrl())
                .path(MONOBANK_ACCOUNT_STATEMENT_PATH)
                .build(account, from, to);

        return restClient.get()
                .uri(url)
                .header("X-Token", authToken)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<MonobankTransactionResponse>>() {})
                .getBody();
    }

    @Override
    public ClientInfoResponse getClientInfo(String authToken) {
        var url = UriComponentsBuilder.fromUriString(monobankSettings.getUrl())
                .path(MONOBANK_CLIENT_INFO_PATH)
                .build()
                .toUri();

        return restClient.get()
                .uri(url)
                .header("X-Token", authToken)
                .retrieve()
                .toEntity(ClientInfoResponse.class)
                .getBody();
    }

    @Override
    public List<ExchangeRateResponse> getExchangeRates() {
        var url = UriComponentsBuilder.fromUriString(monobankSettings.getUrl())
                .path(MONOBANK_CURRENCY_PATH)
                .build()
                .toUri();

        return restClient.get()
                .uri(url)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<ExchangeRateResponse>>() {})
                .getBody();
    }
}
