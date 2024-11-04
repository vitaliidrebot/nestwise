package com.flybird.nestwise.services;

import com.flybird.nestwise.config.settings.KredobankSettings;
import com.flybird.nestwise.config.settings.MonobankSettings;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class SessionServiceImpl implements SessionService {
    private static final Map<String, AuthSession> SESSION_CACHE = new HashMap<>();

    private final MonobankSettings monobankSettings;
    private final KredobankSettings kredobankSettings;

    @PostConstruct
    private void init() {
        SESSION_CACHE.put("monobank", new AuthSession(monobankSettings.getToken(), null, true));
        SESSION_CACHE.put("kredobank", new AuthSession(kredobankSettings.getToken(), null, true));
    }

    @Override
    public String getAuthToken(String bankId) {
        var authSession = getSession(bankId);
        if (authSession.isAuthCompleted()) {
            return authSession.getAuthToken();
        } else {
            throw new RuntimeException("Unauthorized");
        }
    }

    @Override
    public AuthSession getSession(String bankId) {
        return SESSION_CACHE.get(bankId);
    }

    @Override
    public AuthSession putSession(String bankId, AuthSession session) {
        return SESSION_CACHE.put(bankId, session);
    }
}
