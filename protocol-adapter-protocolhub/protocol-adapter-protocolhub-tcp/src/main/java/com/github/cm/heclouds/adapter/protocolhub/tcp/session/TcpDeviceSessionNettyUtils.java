package com.github.cm.heclouds.adapter.protocolhub.tcp.session;

import com.github.cm.heclouds.adapter.core.entity.Device;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * 设备连接Session Netty Channel线程绑定工具类
 */
public final class TcpDeviceSessionNettyUtils {

    private static final String DEVICE = "device";
    private static final String DEVICE_SESSION = "deviceSession";
    private static final String DEVICE_PROTOCOL_TYPE = "deviceProtocolType";
    private static final String DEVICE_REMOTE_ADDRESS = "deviceRemoteAddress";

    private static final AttributeKey<Device> ATTR_KEY_DEVICE = AttributeKey.valueOf(DEVICE);
    private static final AttributeKey<TcpDeviceSession> ATTR_KEY_DEVICE_SESSION = AttributeKey.valueOf(DEVICE_SESSION);
    private static final AttributeKey<String> ATTR_KEY_PROTOCOL_TYPE = AttributeKey.valueOf(DEVICE_PROTOCOL_TYPE);
    private static final AttributeKey<String> ATTR_KEY_REMOTE_ADDRESS = AttributeKey.valueOf(DEVICE_REMOTE_ADDRESS);

    private TcpDeviceSessionNettyUtils() {
    }

    public static void setDevice(Channel channel, Device device) {
        channel.attr(TcpDeviceSessionNettyUtils.ATTR_KEY_DEVICE).set(device);
    }

    public static Device device(Channel channel) {
        Device device = null;
        if (null != channel.attr(TcpDeviceSessionNettyUtils.ATTR_KEY_DEVICE).get()) {
            device = channel.attr(TcpDeviceSessionNettyUtils.ATTR_KEY_DEVICE).get();
        }
        return device;
    }

    public static void setDeviceSession(Channel channel, TcpDeviceSession tcpDeviceSession) {
        channel.attr(TcpDeviceSessionNettyUtils.ATTR_KEY_DEVICE_SESSION).set(tcpDeviceSession);
    }

    public static TcpDeviceSession deviceSession(Channel channel) {
        TcpDeviceSession tcpDeviceSession = null;
        if (null != channel.attr(TcpDeviceSessionNettyUtils.ATTR_KEY_DEVICE_SESSION).get()) {
            tcpDeviceSession = channel.attr(TcpDeviceSessionNettyUtils.ATTR_KEY_DEVICE_SESSION).get();
        }
        return tcpDeviceSession;
    }

    public static void setDeviceRemoteAddress(Channel channel, String remoteAddress) {
        channel.attr(TcpDeviceSessionNettyUtils.ATTR_KEY_REMOTE_ADDRESS).set(remoteAddress);
    }

    public static String deviceRemoteAddress(Channel channel) {
        String remoteAddress = null;
        if (null != channel.attr(TcpDeviceSessionNettyUtils.ATTR_KEY_REMOTE_ADDRESS).get()) {
            remoteAddress = channel.attr(TcpDeviceSessionNettyUtils.ATTR_KEY_REMOTE_ADDRESS).get();
        }
        return remoteAddress;
    }

    public static void setDeviceProtocolType(Channel channel, String protocolType) {
        channel.attr(TcpDeviceSessionNettyUtils.ATTR_KEY_PROTOCOL_TYPE).set(protocolType);
    }

    public static String deviceProtocolType(Channel channel) {
        String protocolType = null;
        if (null != channel.attr(TcpDeviceSessionNettyUtils.ATTR_KEY_PROTOCOL_TYPE).get()) {
            protocolType = channel.attr(TcpDeviceSessionNettyUtils.ATTR_KEY_PROTOCOL_TYPE).get();
        }
        return protocolType;
    }
}
