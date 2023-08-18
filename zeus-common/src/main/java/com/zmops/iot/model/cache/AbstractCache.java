package com.zmops.iot.model.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 封装了缓存的读写操作
 *
 * @author yefei
 **/
public abstract class AbstractCache<K, V> {

    private final Map<K, V> cache;

    private final ReadWriteLock lock;

    protected AbstractCache() {
        this.cache = new HashMap<>();
        this.lock = new ReentrantReadWriteLock();
    }


    private volatile Map<K, V> view;


    public boolean containsKey(K k) {
        return read(()
                ->
                cache.containsKey(k)
        );
    }


    private <X> X read(Supplier<X> supplier) {
        Lock readLock = this.lock.readLock();
        readLock.lock();
        try {
            return supplier.get();
        } finally {
            readLock.unlock();
        }
    }

    public V get(K key) {
        return read(()
                ->
                cache.get(key)
        );
    }

    public Map<K, V> getAll() {
        return read(() -> view);
    }

    public void update(Map<K, V> values) {
        Lock writeLock = this.lock.writeLock();
        writeLock.lock();
        try {
            cache.clear();
            cache.putAll(values);
            view = new ConcurrentHashMap<>(cache);
        } finally {
            writeLock.unlock();
        }
    }


    public void update(Collection<V> values, Function<V, K> keyMapper) {
        this.update(values.stream().collect(Collectors.toMap(keyMapper, o -> o, (a, b) -> b)));
    }


}
