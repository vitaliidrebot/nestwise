package com.flybird.nestwise.controllers;

import com.flybird.nestwise.services.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BankAccountController {
    private final BankAccountService bankAccountService;

    /**
     * Endpoint to calculate the total balance for a given accountId within a specified date range.
     * @param accountId the ID of the bank account
     * @param from the start of the date range (Unix time in seconds)
     * @param to the end of the date range (Unix time in seconds)
     * @return the total balance
     */
    @GetMapping("/bank-account/{accountId}/total-balance")
    public double calculateTotalBalance(
            @PathVariable String accountId,
            @RequestParam long from,
            @RequestParam long to) {
        return bankAccountService.calculateTotalBalance(accountId, from, to);
    }
}
