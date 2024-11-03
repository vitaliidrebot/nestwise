package com.flybird.nestwise.clients.banks.kredobank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompleteOtpChallengeRequest {
    private String authValue;
    private Challenge challenge;

    @Data
    @AllArgsConstructor
    public static class Challenge {
        private String challengeId;
    }
}