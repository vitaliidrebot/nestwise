package com.flybird.nestwise.utils;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static String getUsername() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }
}