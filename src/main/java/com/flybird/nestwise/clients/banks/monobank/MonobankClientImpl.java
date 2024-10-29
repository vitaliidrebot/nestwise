package com.flybird.nestwise.clients.banks.monobank;

import com.flybird.nestwise.clients.banks.monobank.dto.AccountStatementResponse;
import com.flybird.nestwise.config.settings.MonobankSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static com.flybird.nestwise.utils.AppConstants.MONOBANK_ACCOUNT_STATEMENT_PATH;

@Component
@RequiredArgsConstructor
public class MonobankClientImpl extends MonobankClient {
    private final RestClient restClient;
    private final MonobankSettings monobankSettings;

    @Override
    public List<AccountStatementResponse> getAccountStatement(String account, long from, long to) {
        var url = UriComponentsBuilder.fromUriString(monobankSettings.getUrl())
                .path(MONOBANK_ACCOUNT_STATEMENT_PATH)
                .build(account, from, to);

        return restClient.get()
                .uri(url)
                .header("X-Token", monobankSettings.getToken())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<AccountStatementResponse>>() {})
                .getBody();
    }
}
