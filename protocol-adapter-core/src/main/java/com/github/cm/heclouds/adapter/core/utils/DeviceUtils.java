package com.github.cm.heclouds.adapter.core.utils;

import com.github.cm.heclouds.adapter.core.consts.CloseReason;
import com.github.cm.heclouds.adapter.core.entity.Device;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Netty相关工具类
 */
public class DeviceUtils {

    private DeviceUtils() {
    }

    /**
     * 设备连接断开原因Map
     */
    private static final Map<Device, CloseReason> DEVICE_CLOSE_REASON_MAP = new ConcurrentHashMap<>();

    /**
     * 查询设备连接断开原因
     *
     * @param device 设备
     * @return 设备连接断开原因
     */
    public static CloseReason getDeviceCloseReason(Device device) {
        return DEVICE_CLOSE_REASON_MAP.getOrDefault(device, CloseReason.CLOSE_BY_DEVICE);
    }

    /**
     * 设置设备连接断开原因
     *
     * @param device      设备
     * @param closeReason 设备连接断开原因
     */
    public static void setDeviceCloseReason(Device device, CloseReason closeReason) {
        DEVICE_CLOSE_REASON_MAP.put(device, closeReason);
    }

    /**
     * 移除设备连接断开原因
     *
     * @param device 设备
     */
    public static void removeDeviceCloseReason(Device device) {
        DEVICE_CLOSE_REASON_MAP.remove(device);
    }
}
