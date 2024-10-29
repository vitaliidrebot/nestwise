package com.flybird.nestwise.services;

public interface BankAccountService {
    double calculateTotalBalance(String accountId, long from, long to);
}
