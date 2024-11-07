package com.flybird.nestwise.clients.banks.kredobank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@AllArgsConstructor
public class LoginRequest {
    private String login;
    private String password;
}