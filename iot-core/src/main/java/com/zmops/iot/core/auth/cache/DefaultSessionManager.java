package com.zmops.iot.core.auth.cache;

import com.zmops.iot.core.auth.model.LoginUser;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于内存的会话管理
 *
 * @author fengshuonan
 */
@Component
public class DefaultSessionManager implements SessionManager {

    private final Map<String, LoginUser> caches = new ConcurrentHashMap<>();

    @Override
    public void createSession(String token, LoginUser loginUser) {
        caches.put(SESSION_PREFIX + token, loginUser);
    }

    @Override
    public LoginUser getSession(String token) {
        return caches.get(SESSION_PREFIX + token);
    }

    @Override
    public void removeSession(String token) {
        caches.remove(SESSION_PREFIX + token);
    }

    @Override
    public boolean haveSession(String token) {
        return caches.containsKey(SESSION_PREFIX + token);
    }
}
