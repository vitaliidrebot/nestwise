package com.flybird.nestwise.clients.banks.monobank.dto;

import lombok.Data;

import java.util.List;

@Data
public class ClientInfoResponse {
    private String clientId;
    private String name;
    private String webHookUrl;
    private String permissions;
    private List<Account> accounts;

    @Data
    public static class Account {
        private String id;
        private String sendId;
        private int currencyCode;
        private String cashbackType;
        private long balance;
        private long creditLimit;
        private List<String> maskedPan;
        private String type;
        private String iban;
    }
}