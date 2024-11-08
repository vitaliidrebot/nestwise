package com.flybird.nestwise.config.settings;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("nestwise.bank.privatbank")
@RequiredArgsConstructor
public class PrivatbankSettings {
    private final String url;
}
