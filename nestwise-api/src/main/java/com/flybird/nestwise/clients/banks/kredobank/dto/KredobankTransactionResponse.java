package com.flybird.nestwise.clients.banks.kredobank.dto;

import lombok.Data;

@Data
public class KredobankTransactionResponse {
    private String id;
    private String externalId;
    private Source source;
    private long amountInCents;
    private String currency;
    private long localAmountInCents;
    private long operationDate;
    private long finalizationDate;
    private String description;
    private String cardId;

    @Data
    public static class Source {
        private String accountNumber;
        private String name;
        private String currency;
    }
}