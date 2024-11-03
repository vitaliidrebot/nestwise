package com.flybird.nestwise.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppConstants {
    public static final String UNRESOLVED_ENV_VAR_PATTERN = "^(?!(\\$\\{.+\\})$).+$";

    public static final String MONOBANK_CURRENCY_PATH = "/bank/currency";
    public static final String MONOBANK_CLIENT_INFO_PATH = "/personal/client-info";
    public static final String MONOBANK_ACCOUNT_STATEMENT_PATH = "/personal/statement/{account}/{from}/{to}";
}
