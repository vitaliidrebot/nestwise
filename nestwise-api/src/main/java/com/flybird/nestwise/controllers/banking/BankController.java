package com.flybird.nestwise.controllers.banking;

import com.flybird.nestwise.dto.banking.AuthType;
import com.flybird.nestwise.dto.banking.BankBalanceResponseDto;
import com.flybird.nestwise.dto.banking.ExchangeRateDto;
import com.flybird.nestwise.dto.banking.LoginRequestDto;
import com.flybird.nestwise.dto.banking.LoginStatusResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Tag(name = "Bank", description = "Bank management endpoints")
public interface BankController {

    @Operation(summary = "Login to a bank", tags = "Bank", description = "Login to a bank using predefined auth type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged in"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to access the resource"),
    })
    ResponseEntity<LoginStatusResponseDto> bankLogin(
            @Parameter(description = "Bank ID", example = "monobank") String bankId,
            @Parameter(description = "Auth type", example = "TOKEN") AuthType type,
            @Valid LoginRequestDto requestDto
    );

    @Operation(summary = "Calculate balance change", tags = "Bank", description = "Calculate the balance change over a specified period")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully calculated balance change"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to access the resource"),
    })
    ResponseEntity<BankBalanceResponseDto> calculateBalanceChange(
            @Parameter(description = "Start date", example = "20241001") Long from,
            @Parameter(description = "End date", example = "20241101") Long to,
            @Parameter(description = "ISO 4217 currency code", example = "UAH") String currency,
            @Parameter(description = "Comma-separated list of bank IDs", example = "monobank,kredobank") Set<String> bankIds
    );

    @Operation(summary = "Sync bank accounts", tags = "Bank", description = "Sync account statements with bank")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated account statements"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to access the resource"),
    })
    ResponseEntity<Void> syncAccounts(
            @Parameter(description = "Comma-separated list of bank IDs", example = "monobank,kredobank") Set<String> bankIds
    );

    @Operation(summary = "Get current balance", tags = "Bank", description = "Get the current balance of the bank accounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved current balance"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to access the resource"),
    })
    ResponseEntity<BankBalanceResponseDto> getCurrentBalance(
            @Parameter(description = "ISO 4217 currency code", example = "UAH") String currency,
            @Parameter(description = "Comma-separated list of bank IDs", example = "monobank,kredobank") Set<String> bankIds
    );

    @Operation(summary = "Get exchange rates history", tags = "Bank", description = "Get the exchange rates history for a specified bank, currency and period")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved exchange rates history"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to access the resource"),
    })
    ResponseEntity<List<ExchangeRateDto>> getExchangeRatesHistory(
            @Parameter(description = "Bank ID", example = "privatbank") String bankId,
            @Parameter(description = "ISO 4217 currency code", example = "UAH") String currency,
            @Parameter(description = "Start date", example = "20241001") LocalDate from,
            @Parameter(description = "End date", example = "20241101") LocalDate to
    );
}