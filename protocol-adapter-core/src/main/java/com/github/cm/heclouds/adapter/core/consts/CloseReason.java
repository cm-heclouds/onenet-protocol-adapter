package com.github.cm.heclouds.adapter.core.consts;

/**
 * 设备连接断开原因枚举类
 */
public enum CloseReason {
    /**
     * 由设备主动或者网络异常断开连接
     */
    CLOSE_BY_DEVICE("CloseByDevice"),
    /**
     * 由设备主动断开连接，且调用了OpenApi的设备登出API
     */
    CLOSE_BY_DEVICE_OFFLINE("CloseByDeviceOffline"),
    /**
     * 由设备主动断开连接
     */
    CLOSE_BY_DEVICE_ACTIVELY("CloseByDeviceActively"),
    /**
     * 由协议站主动断开连接
     */
    CLOSE_BY_PROTOCOL_HUB("CloseByProtocolHub"),
    /**
     * 由SDK主动断开连接
     */
    CLOSE_BY_SDK("CloseBySDK"),
    /**
     * 由平台接入机主动断开连接
     */
    CLOSE_BY_ONENET("CloseByOneNET"),
    /**
     * 因超时断开连接
     */
    CLOSE_BY_TIMEOUT("CloseByTimeout"),
    /**
     * 因控制连接断开断开连接
     */
    CLOSE_DUE_TO_CONTROL_CONNECTION_LOST("CloseDueToControlConnectionLost"),
    /**
     * 因代理连接断开断开连接
     */
    CLOSE_DUE_TO_PROXY_CONNECTION_LOST("CloseDueToProxyConnectionLost"),
    /**
     * 因未知异常断开连接
     */
    CLOSE_DUE_TO_EXCEPTION("CloseDueToException"),
    /**
     * 因未知异常断开连接
     */
    CLOSE_DUE_TO_UNKNOWN_EXCEPTION("CloseDueToUnknownException");


    private final String value;

    CloseReason(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
