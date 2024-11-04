package com.flybird.nestwise.controllers.banking;

import com.flybird.nestwise.dto.banking.BankBalanceResponseDto;
import com.flybird.nestwise.dto.banking.AuthType;
import com.flybird.nestwise.dto.banking.LoginRequestDto;
import com.flybird.nestwise.dto.banking.LoginStatusResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

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
            @Parameter(description = "Comma-separated list of bank IDs", example = "UAH") Set<String> bankIds
    );

    @Operation(summary = "Get current balance", tags = "Bank", description = "Get the current balance of the bank accounts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved current balance"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to access the resource"),
    })
    ResponseEntity<BankBalanceResponseDto> getCurrentBalance(
            @Parameter(description = "ISO 4217 currency code", example = "UAH") String currency,
            @Parameter(description = "Comma-separated list of bank IDs", example = "UAH") Set<String> bankIds
    );
}