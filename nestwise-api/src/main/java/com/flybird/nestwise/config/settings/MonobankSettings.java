package com.flybird.nestwise.config.settings;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.flybird.nestwise.utils.AppConstants.UNRESOLVED_ENV_VAR_PATTERN;

@Data
@ConfigurationProperties("nestwise.bank.monobank")
@RequiredArgsConstructor
public class MonobankSettings {
    private final String url;
    @NotNull
    @Pattern(regexp = UNRESOLVED_ENV_VAR_PATTERN, message="Environment variable is missing")
    private final String token;
}
