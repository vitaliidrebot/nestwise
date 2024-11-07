package com.flybird.nestwise.config.settings;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "nestwise.bank.kredobank")
public class KredobankSettings {
    private String url;
    private String username;
    private String password;
    private String token;
}