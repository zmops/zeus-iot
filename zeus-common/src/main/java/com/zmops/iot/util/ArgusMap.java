package com.zmops.iot.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * HashMap Builder
 * 
 * @author yefei
 * @param <K>
 * @param <V>
 */
public abstract class ArgusMap<K, V> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static <K, V> HashMapBuilder<K, V> builder() {
        return new HashMapBuilder<K, V>();
    }

    public static <K, V> LinkedHashMapBuilder<K, V> linkedHashMapBuilder() {
        return new LinkedHashMapBuilder<K, V>();
    }

    public static class HashMapBuilder<K, V> {

        private Map<K, V> t;

        public HashMapBuilder(){
            this.t = new HashMap<>();
        }

        public HashMapBuilder<K, V> put(K key, V value) {
            this.t.put(key, value);
            return this;
        }

        public Map<K, V> build() {
            return this.t;
        }
    }

    public static class LinkedHashMapBuilder<K, V> {

        private Map<K, V> t;

        public LinkedHashMapBuilder(){
            this.t = new LinkedHashMap<>();
        }

        public LinkedHashMapBuilder<K, V> put(K key, V value) {
            this.t.put(key, value);
            return this;
        }

        public Map<K, V> build() {
            return this.t;
        }
    }
}
