package com.github.cm.heclouds.adapter.core.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 物模型ID生成工具
 */
public class IdUtils {

    private static final AtomicLong ID = new AtomicLong(1);

    private static final Long MAX_ID = 9999999999999L;

    private IdUtils() {
    }

    public static String generateId() {
        long id = ID.getAndIncrement();
        // 如果超过最大值，重置
        if (id > MAX_ID) {
            // 加锁
            synchronized (IdUtils.class) {
                long l = ID.get();
                // 如果未重置，重置
                if (l > MAX_ID) {
                    ID.set(1);
                    return String.valueOf(1);
                }
                // 如果已重置，重新获取
                return String.valueOf(ID.getAndIncrement());
            }
        }
        return String.valueOf(id);
    }
}
