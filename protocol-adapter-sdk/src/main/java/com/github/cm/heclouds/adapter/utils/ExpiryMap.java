package com.github.cm.heclouds.adapter.utils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 带过期时间的Map
 */
public class ExpiryMap<K, V> implements Map<K, V> {

    /*
    对外调用的方法：remove 、values、entryset、put、putifabsent
     */
    private final ConcurrentHashMap<K, V> workMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<K, Long> innerExpiryMap = new ConcurrentHashMap<>();

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

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
        lock.writeLock().lock();
        try {
            innerExpiryMap.keySet().forEach(key -> {
                if (innerExpiryMap.get(key) < System.currentTimeMillis()) {
                    innerExpiryMap.remove(key);
                    workMap.remove(key);
                }
            });
        } finally {
            lock.writeLock().unlock();
        }
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
        lock.writeLock().lock();
        try {
            innerExpiryMap.put(key, System.currentTimeMillis() + expiryTime);
            return workMap.put(key, value);
        } finally {
            lock.writeLock().unlock();
        }
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
        lock.writeLock().lock();
        try {
            innerExpiryMap.put(key, System.currentTimeMillis() + expiry);
            return workMap.put(key, value);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public V putIfAbsent(K key, V value) {
        lock.writeLock().lock();
        try {
            if (!containsKey(key)) {
                innerExpiryMap.put(key, System.currentTimeMillis() + expiryTime);
                return workMap.put(key, value);
            } else {
                if (containsKey(key)) {
                    return workMap.get(key);
                }
                return null;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public V putIfAbsent(K key, V value, long expiry) {
        lock.writeLock().lock();
        try {
            if (!containsKey(key)) {
//                innerExpiryMap.put(key, System.currentTimeMillis() + expiry);
                innerExpiryMap.put(key, System.currentTimeMillis() + expiryTime);
                return workMap.put(key, value);
            } else {
                if (containsKey(key)) {
                    return workMap.get(key);
                }
                return null;
            }
        }finally {
            lock.writeLock().unlock();
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
    @Deprecated
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

        lock.readLock().lock();
        try {
            Collection<V> values = workMap.values();
            return values.contains(value);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public V get(Object key) {
        lock.writeLock().lock();
        try {
            if (containsKey(key)) {
                return workMap.get(key);
            }
            return null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public V remove(Object key) {

        lock.writeLock().lock();
        try {
            boolean containKey = containsKey(key);
            if (key != null && innerExpiryMap.containsKey(key)){
                innerExpiryMap.remove(key);
            }
            if (containKey) {
                return workMap.remove(key);
            } else {
                return null;
            }
        }finally {
            lock.writeLock().unlock();
        }

    }

    @Deprecated
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new RuntimeException("此方法已废弃！");
    }

    @Override
    public void clear() {
        lock.writeLock().lock();
        try {
            innerExpiryMap.clear();
            workMap.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Set<K> keySet() {
        try {
            removeInValidKeys();
            lock.readLock().lock();
            return workMap.keySet();
        }finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Collection<V> values() {
        try {
            removeInValidKeys();
            lock.readLock().lock();
            return workMap.values();
        }finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        try {
            removeInValidKeys();
            lock.readLock().lock();
            return workMap.entrySet();
        }finally {
            lock.readLock().unlock();
        }
    }
}