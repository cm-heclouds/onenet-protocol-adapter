package com.github.cm.heclouds.adapter.protocolhub.tcp.session;

import com.github.cm.heclouds.adapter.core.logging.ILogger;
import com.github.cm.heclouds.adapter.protocolhub.tcp.config.TcpProtocolHubConfigUtils;
import io.netty.channel.Channel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.github.cm.heclouds.adapter.core.logging.LoggerFormat.Action.DISCONNECT;
import static com.github.cm.heclouds.adapter.core.logging.LoggerFormat.Action.RUNTIME;

/**
 * 设备Session管理
 */
public final class TcpDeviceSessionManager {

    private static final ILogger LOGGER = TcpProtocolHubConfigUtils.getLogger();

    /**
     * 设备连接Session池
     * 设备名称与设备连接Session的映射
     */
    private static final ConcurrentMap<String, TcpDeviceSession> DEVICE_SESSION_POOL = new ConcurrentHashMap<>();

    private TcpDeviceSessionManager() {
    }

    /**
     * 创建设备连接Session
     *
     * @param productId  产品ID
     * @param deviceName 设备名称
     * @param channel    连接channel
     * @return 设备连接Session
     */
    public static TcpDeviceSession createDeviceSession(String productId, String deviceName, Channel channel) {
        return TcpDeviceSession.newBuilder()
                .productId(productId)
                .deviceName(deviceName)
                .channel(channel)
                .build();
    }

    /**
     * 放入设备连接Session
     *
     * @param deviceSession 设备连接Session
     */
    public static void putDeviceSession(TcpDeviceSession deviceSession) {
        DEVICE_SESSION_POOL.put(genKey(deviceSession), deviceSession);
    }

    /**
     * 获取设备连接Session
     *
     * @param productId  产品ID
     * @param deviceName 设备名称
     * @return 设备连接Session
     */
    public static TcpDeviceSession getDeviceSession(String productId, String deviceName) {
        return DEVICE_SESSION_POOL.get(productId + "-" + deviceName);
    }

    /**
     * 连接断开情况处理
     */
    public static void handleConnectionLost(TcpDeviceSession deviceSession) {
        if (deviceSession != null) {
            Channel channel = deviceSession.getChannel();
            String reason = TcpDeviceSessionNettyUtils.deviceCloseReason(channel);
            // 移除DevSessionManager中的DeviceSession
            TcpDeviceSessionManager.removeDeviceSession(deviceSession);
            LOGGER.logDevInfo(TcpProtocolHubConfigUtils.getName(), DISCONNECT, deviceSession.getProductId(), deviceSession.getDeviceName(), reason);
        } else {
            LOGGER.logInnerWarn(TcpProtocolHubConfigUtils.getName(), RUNTIME, "device connection lost without logging in");
        }
    }

    /**
     * 移除设备连接Session
     *
     * @param deviceSession 连接Session
     */
    private static void removeDeviceSession(TcpDeviceSession deviceSession) {
        DEVICE_SESSION_POOL.remove(genKey(deviceSession));
    }

    /**
     * 生成DevSession池的key，规则为产品id+设备名称
     *
     * @param deviceSession 设备Session
     * @return key
     */
    private static String genKey(TcpDeviceSession deviceSession) {
        return deviceSession.getProductId() + "-" + deviceSession.getDeviceName();
    }

}
