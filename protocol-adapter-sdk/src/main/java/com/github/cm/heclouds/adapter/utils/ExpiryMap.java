package com.github.cm.heclouds.adapter.utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 带过期时间的Map
 */
public class ExpiryMap<K, V> implements Map<K, V> {

    private static final ConcurrentHashMap WORK_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Object, Long> EXPIRY_MAP = new ConcurrentHashMap<>();

    // 定时清除失效key
    static {
        //间隔时间，单位：秒
        int interval = 5 * 60;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                removeInValidKeys();
            }
            //每隔5分钟启动一次
        }, interval * 1000, interval * 1000);
    }

    /**
     * 过期时间，单位ms
     */
    private long expiryTime = 60 * 1000;

    public ExpiryMap() {
    }

    public ExpiryMap(long expiryTime) {
        super();
        this.expiryTime = expiryTime;
    }

    private static void removeInValidKeys() {
        EXPIRY_MAP.keySet().forEach(key -> {
            if (EXPIRY_MAP.get(key) < System.currentTimeMillis()) {
                EXPIRY_MAP.remove(key);
                WORK_MAP.remove(key);
            }
        });
    }


    /**
     * put方法，默认超时时间为expiryTime
     *
     * @param key   key
     * @param value value
     * @return value
     */
    @Override
    public V put(K key, V value) {
        EXPIRY_MAP.put(key, System.currentTimeMillis() + expiryTime);
        return (V) WORK_MAP.put(key, value);
    }

    /**
     * put方法，需要设置key 的有效期！单位为：毫秒
     *
     * @param key
     * @param value
     * @param expiry key的有效期，单位：毫秒
     * @return
     */
    public V put(K key, V value, long expiry) {
        EXPIRY_MAP.put(key, System.currentTimeMillis() + expiry);
        return (V) WORK_MAP.put(key, value);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        if (!containsKey(key)) {
            EXPIRY_MAP.put(key, System.currentTimeMillis() + expiryTime);
            return put(key, value);
        } else {
            return get(key);
        }
    }

    public V putIfAbsent(K key, V value, long expiry) {
        if (!containsKey(key)) {
            EXPIRY_MAP.put(key, System.currentTimeMillis() + expiry);
            return put(key, value);
        } else {
            return get(key);
        }
    }

    @Override
    public int size() {
        return keySet().size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key != null) {
            if (EXPIRY_MAP.containsKey(key)) {
                if (EXPIRY_MAP.get(key) > System.currentTimeMillis()) {
                    return true;
                } else {
                    EXPIRY_MAP.remove(key);
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        Collection<Object> values = WORK_MAP.values();
        return values.contains(value);
    }

    @Override
    public V get(Object key) {
        if (containsKey(key)) {
            return (V) WORK_MAP.get(key);
        }
        return null;
    }

    @Override
    public V remove(Object key) {
        boolean containKey = containsKey(key);
        EXPIRY_MAP.remove(key);
        if (containKey) {
            return (V) WORK_MAP.remove(key);
        } else {
            return null;
        }

    }

    @Deprecated
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new RuntimeException("此方法已废弃！");
    }

    @Override
    public void clear() {
        EXPIRY_MAP.clear();
        WORK_MAP.clear();
    }

    @Override
    public Set<K> keySet() {
        removeInValidKeys();
        return WORK_MAP.keySet();
    }

    @Override
    public Collection<V> values() {
        removeInValidKeys();
        return WORK_MAP.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        removeInValidKeys();
        return WORK_MAP.entrySet();
    }
}