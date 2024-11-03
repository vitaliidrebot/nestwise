package com.flybird.nestwise.dto.banking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class LoginStatusResponseDto {
    private boolean isAuthCompleted;
    private AuthType authType;
}