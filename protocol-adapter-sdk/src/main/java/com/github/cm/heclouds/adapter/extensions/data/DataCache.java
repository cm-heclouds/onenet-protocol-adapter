package com.github.cm.heclouds.adapter.extensions.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * data cache extension
 */
public class DataCache<T> {
    private static int MAX_QUEUE_SIZE = 8 * 1024;
    private Map<String, ConcurrentLinkedDeque<T>> deviceDataCache = new ConcurrentHashMap<>();

    public boolean cacheData(Long pid, String deviceName, T t) {
        ConcurrentLinkedDeque<T> deque = deviceDataCache.computeIfAbsent(pid + deviceName, s -> new ConcurrentLinkedDeque<>());
        if (deque.size() >= MAX_QUEUE_SIZE) {
            return false;
        }
        return deque.add(t);
    }

    public boolean cacheData(Long pid, String deviceName, T[] array) {
        if (array.length > MAX_QUEUE_SIZE) {
            return false;
        }
        ConcurrentLinkedDeque<T> deque = deviceDataCache.computeIfAbsent(pid + deviceName, s -> new ConcurrentLinkedDeque<>());
        if (deque.size() + array.length > MAX_QUEUE_SIZE) {
            return false;
        }
        return deque.addAll(Arrays.asList(array));
    }

    public T getData(Long pid, String deviceName) {
        ConcurrentLinkedDeque<T> deque = deviceDataCache.get(pid + deviceName);
        if (null == deque) {
            return null;
        }

        if (deque.size() > 0) {
            return deque.poll();
        }
        return null;
    }

    public ArrayList<T> getDataAll(Long pid, String deviceName) {
        ConcurrentLinkedDeque<T> deque = deviceDataCache.get(pid + deviceName);
        if (null == deque) {
            return new ArrayList<>();
        }

        if (deque.size() > 0) {
            ArrayList<T> tmp = new ArrayList<>(deque);
            deque.clear();
            return tmp;
        }
        return new ArrayList<>();
    }

    public ArrayList<T> getData(Long pid, String deviceName, int length) {
        ConcurrentLinkedDeque<T> deque = deviceDataCache.get(pid + deviceName);
        if (null == deque) {
            return null;
        }

        ArrayList<T> tmp = new ArrayList<>();
        for (int i = 0; i < length; ++i) {
            T t = deque.poll();
            if (null != t) {
                tmp.add(t);
            } else {
                break;
            }
        }
        return tmp;
    }


    public void removeData(Long pid, String deviceName) {
        ConcurrentLinkedDeque<T> deque = deviceDataCache.get(pid + deviceName);
        if (null != deque) {
            deque.poll();
        }
    }

    public void removeData(Long pid, String deviceName, int length) {
        ConcurrentLinkedDeque<T> deque = deviceDataCache.get(pid + deviceName);
        if (null != deque) {
            int size = Math.min(length, deque.size());
            for (int i = 0; i < size; ++i) {
                deque.poll();
            }
        }
    }

    public void removeDataAll(Long pid, String deviceName) {
        deviceDataCache.remove(pid + deviceName);
    }
}
