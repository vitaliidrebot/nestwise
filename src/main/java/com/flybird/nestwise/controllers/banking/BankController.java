package com.flybird.nestwise.controllers.banking;

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
}