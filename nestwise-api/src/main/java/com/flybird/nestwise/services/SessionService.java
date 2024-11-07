package com.flybird.nestwise.services;

public interface SessionService {
    String getAuthToken(String bankId);

    AuthSession getSession(String bankId);

    AuthSession putSession(String bankId, AuthSession session);
}
