package com.flybird.nestwise.controllers.banking;

import com.flybird.nestwise.dto.banking.AuthType;
import com.flybird.nestwise.dto.banking.LoginRequestDto;
import com.flybird.nestwise.dto.banking.LoginStatusResponseDto;
import com.flybird.nestwise.security.Authenticated;
import com.flybird.nestwise.services.banking.AccountingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}