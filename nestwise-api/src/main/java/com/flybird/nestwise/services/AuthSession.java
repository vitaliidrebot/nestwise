package com.flybird.nestwise.services;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthSession {
    private String authToken;
    private String otpChallengeId;
    private boolean isAuthCompleted;
}