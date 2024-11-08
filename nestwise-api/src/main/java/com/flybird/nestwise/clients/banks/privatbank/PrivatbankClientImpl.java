package com.flybird.nestwise.clients.banks.privatbank;

import com.flybird.nestwise.clients.banks.privatbank.dto.PrivatbankExchangeRateResponse;
import com.flybird.nestwise.config.settings.PrivatbankSettings;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static com.flybird.nestwise.utils.AppConstants.PRIVATBANK_EXCHANGE_RATES_PATH;

@Component
@RequiredArgsConstructor
public class PrivatbankClientImpl implements PrivatbankClient {
    // TODO: Check thread-safety
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy").withZone(ZoneId.systemDefault());

    private final RestClient restClient;
    private final PrivatbankSettings privatbankSettings;

    @Override
    public PrivatbankExchangeRateResponse getHistoricalExchangeRates(LocalDate date) {
        var url = UriComponentsBuilder.fromUriString(privatbankSettings.getUrl())
                .path(PRIVATBANK_EXCHANGE_RATES_PATH)
                .queryParam("date", DATE_FORMATTER.format(date))
                .build()
                .toUri();

        return restClient.get()
                .uri(url)
                .retrieve()
                .toEntity(PrivatbankExchangeRateResponse.class)
                .getBody();
    }
}
