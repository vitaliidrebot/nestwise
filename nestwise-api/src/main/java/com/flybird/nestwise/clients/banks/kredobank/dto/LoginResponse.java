package com.flybird.nestwise.clients.banks.kredobank.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class LoginResponse {
    private long expireDate;
    private long creationDate;
    private long updateDate;
    private List<Role> roles;
    private Map<String, String> userInfo;
    private String level;
    private long levelExpireDate;
    private List<Attribute> sessionAttributes;
    private List<Attribute> profileAttributes;
    private boolean isAuthCompleted;
    private List<String> nextAuthTypes;
    private boolean requestPasswordChange;
    private int daysToPasswordExpiration;

    @Data
    public static class Role {
        private String code;
    }

    @Data
    public static class Attribute {
        private String key;
        private String value;
    }
}