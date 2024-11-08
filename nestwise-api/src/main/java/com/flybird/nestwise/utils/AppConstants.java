package com.flybird.nestwise.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppConstants {
    public static final String UNRESOLVED_ENV_VAR_PATTERN = "^(?!(\\$\\{.+\\})$).+$";

    public static final String MONOBANK_CURRENCY_PATH = "/bank/currency";
    public static final String MONOBANK_CLIENT_INFO_PATH = "/personal/client-info";
    public static final String MONOBANK_ACCOUNT_STATEMENT_PATH = "/personal/statement/{account}/{from}/{to}";

    public static final String KREDOBANK_AUTH_CREDENTIALS_PATH = "/v1/individual/light/auth/login/login-password";
    public static final String KREDOBANK_AUTH_INITIATE_OTP_CHALLENGE_PATH = "/v1/individual/light/auth/login/otp_sms/challenge";
    public static final String KREDOBANK_AUTH_COMPLETE_OTP_CHALLENGE_PATH = "/v1/individual/light/auth/login/otp_sms/response";
    public static final String KREDOBANK_AUTH_SESSION_PATH = "/v1/individual/light/auth/session";
    public static final String KREDOBANK_ACCOUNT_INFO_PATH = "/v2/individual/light/contract/card/all";
    public static final String KREDOBANK_ACCOUNT_HISTORY_PATH = "/v1/individual/light/contract/card/{accountId}/history/all/between";
    public static final String KREDOBANK_CURRENCY_PATH = "/v1/individual/light/currencies/bank";

    public static final String PRIVATBANK_EXCHANGE_RATES_PATH = "/exchange_rates";
}
