package com.flybird.nestwise.controllers.banking;

import com.flybird.nestwise.dto.banking.BankBalanceResponseDto;
import com.flybird.nestwise.dto.banking.AuthType;
import com.flybird.nestwise.dto.banking.LoginRequestDto;
import com.flybird.nestwise.dto.banking.LoginStatusResponseDto;
import com.flybird.nestwise.security.Authenticated;
import com.flybird.nestwise.services.banking.AccountingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.Objects.isNull;

@RestController
@RequestMapping("/banks")
@RequiredArgsConstructor
public class BankControllerImpl implements BankController {
    private final AccountingService accountingService;

    @Override
    @Authenticated
    @PostMapping("/{bankId}/login")
    public ResponseEntity<LoginStatusResponseDto> bankLogin(@PathVariable String bankId,
                                                            @RequestParam(required = false) AuthType type,
                                                            @RequestBody(required = false) LoginRequestDto requestDto) {
        return ResponseEntity.ok(accountingService.bankLogin(bankId, type, requestDto));
    }

    @Override
    @Authenticated
    @GetMapping("/balance-change")
    public ResponseEntity<BankBalanceResponseDto> calculateBalanceChange(@RequestParam Long from,
                                                                         @RequestParam Long to,
                                                                         @RequestParam(required = false, defaultValue = "UAH") String currency,
                                                                         @RequestParam(required = false) Set<String> bankIds) {
        return ResponseEntity.ok(accountingService.calculateBalanceChange(from, to, currency, isNull(bankIds) ? emptySet() : bankIds));
    }

    @Override
    @Authenticated
    @GetMapping("/balance")
    public ResponseEntity<BankBalanceResponseDto> getCurrentBalance(@RequestParam(required = false, defaultValue = "UAH") String currency,
                                                                    @RequestParam(required = false) Set<String> bankIds) {
        return ResponseEntity.ok(accountingService.calculateCurrentBalance(currency, isNull(bankIds) ? emptySet() : bankIds));
    }
}