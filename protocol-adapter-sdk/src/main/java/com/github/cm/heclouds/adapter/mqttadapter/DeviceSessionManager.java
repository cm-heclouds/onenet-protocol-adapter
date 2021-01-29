package com.github.cm.heclouds.adapter.mqttadapter;

import com.github.cm.heclouds.adapter.entity.sdk.DeviceSession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 设备Session管理
 */
public final class DeviceSessionManager {

    /**
     * 设备连接Session池
     * 设备名称与设备连接Session的映射
     */
    private static final ConcurrentMap<String, DeviceSession> DEVICE_SESSION_POOL = new ConcurrentHashMap<>();

    private DeviceSessionManager() {
    }

    /**
     * 创建设备连接Session
     *
     * @param productId  产品ID
     * @param deviceName 设备名称
     * @return 设备连接Session
     */
    public static DeviceSession createDevSession(String productId, String deviceName) {
        return DeviceSession.newBuilder()
                .productId(productId)
                .deviceName(deviceName)
                .build();
    }

    /**
     * 放入设备连接Session
     *
     * @param deviceSession 设备连接Session
     */
    public static void putDeviceSession(DeviceSession deviceSession) {
        DEVICE_SESSION_POOL.put(genKey(deviceSession), deviceSession);
    }

    /**
     * 获取设备连接Session
     *
     * @param productId  产品ID
     * @param deviceName 设备名称
     * @return 设备连接Session
     */
    public static DeviceSession getDeviceSession(String productId, String deviceName) {
        return DEVICE_SESSION_POOL.get(productId + "-" + deviceName);
    }

    /**
     * 连接断开情况处理
     */
    public static void handleDeviceOffline(DeviceSession deviceSession) {
        // 移除DevSessionManager中的DeviceSession
        deviceSession.setLogin(false);
        deviceSession.getProxySession().setDevicesReachedLimit(false);
        deviceSession.getProxySession().removeDeviceSession(deviceSession.getProductId(), deviceSession.getDeviceName());
        deviceSession.setProxySession(null);
        DeviceSessionManager.removeDeviceSession(deviceSession);
    }

    public static ConcurrentMap<String, DeviceSession> getDeviceSessionPool() {
        return DEVICE_SESSION_POOL;
    }

    /**
     * 移除设备连接Session
     *
     * @param deviceSession 连接Session
     */
    private static void removeDeviceSession(DeviceSession deviceSession) {
        DEVICE_SESSION_POOL.remove(genKey(deviceSession));
    }

    /**
     * 生成DevSession池的key，规则为产品id+设备名称
     *
     * @param devSession 设备Session
     * @return key
     */
    private static String genKey(DeviceSession devSession) {
        return devSession.getProductId() + "-" + devSession.getDeviceName();
    }
}
