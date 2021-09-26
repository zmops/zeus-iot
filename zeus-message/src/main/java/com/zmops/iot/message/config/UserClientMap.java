package com.zmops.iot.message.config;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserClientMap {

    public static final ConcurrentHashMap<String, UUID> DB = new ConcurrentHashMap<>();

    public List<UUID> findAll() {
        return CollUtil.newArrayList(DB.values());
    }


    public Optional<UUID> findByUserId(String userId) {
        return Optional.ofNullable(DB.get(userId));
    }


    public void save(String userId, UUID sessionId) {
        DB.put(userId, sessionId);
    }


    public void deleteByUserId(String userId) {
        DB.remove(userId);
    }

}
