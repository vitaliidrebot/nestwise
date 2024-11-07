package com.flybird.nestwise.clients.banks.kredobank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseWithToken {
    private LoginResponse loginResponse;
    private String authToken;
}