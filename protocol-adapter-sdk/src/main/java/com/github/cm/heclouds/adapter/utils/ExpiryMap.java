package com.github.cm.heclouds.adapter.utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 带过期时间的Map
 */
public class ExpiryMap<K, V> implements Map<K, V> {

    private final ConcurrentHashMap<K, V> workMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<K, Long> innerExpiryMap = new ConcurrentHashMap<>();


    /**
     * 过期时间，单位ms
     */
    private long expiryTime = 60 * 1000;

    public ExpiryMap() {
        // 定时清除失效key
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

    public ExpiryMap(long expiryTime) {
        this();
        this.expiryTime = expiryTime;
    }

    private void removeInValidKeys() {
        innerExpiryMap.keySet().forEach(key -> {
            if (innerExpiryMap.get(key) < System.currentTimeMillis()) {
                innerExpiryMap.remove(key);
                workMap.remove(key);
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
        innerExpiryMap.put(key, System.currentTimeMillis() + expiryTime);
        return workMap.put(key, value);
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
        innerExpiryMap.put(key, System.currentTimeMillis() + expiry);
        return workMap.put(key, value);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        if (!containsKey(key)) {
            innerExpiryMap.put(key, System.currentTimeMillis() + expiryTime);
            return put(key, value);
        } else {
            return get(key);
        }
    }

    public V putIfAbsent(K key, V value, long expiry) {
        if (!containsKey(key)) {
            innerExpiryMap.put(key, System.currentTimeMillis() + expiry);
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
            if (innerExpiryMap.containsKey(key)) {
                if (innerExpiryMap.get(key) > System.currentTimeMillis()) {
                    return true;
                } else {
                    innerExpiryMap.remove(key);
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        Collection<V> values = workMap.values();
        return values.contains(value);
    }

    @Override
    public V get(Object key) {
        if (containsKey(key)) {
            return workMap.get(key);
        }
        return null;
    }

    @Override
    public V remove(Object key) {
        boolean containKey = containsKey(key);
        innerExpiryMap.remove(key);
        if (containKey) {
            return workMap.remove(key);
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
        innerExpiryMap.clear();
        workMap.clear();
    }

    @Override
    public Set<K> keySet() {
        removeInValidKeys();
        return workMap.keySet();
    }

    @Override
    public Collection<V> values() {
        removeInValidKeys();
        return workMap.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        removeInValidKeys();
        return workMap.entrySet();
    }
}
