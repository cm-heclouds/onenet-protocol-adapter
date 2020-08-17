package com.github.cm.heclouds.adapter.utils;

import com.github.cm.heclouds.adapter.entity.ConnectionType;
import com.github.cm.heclouds.adapter.entity.ProxySession;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

/**
 * 连接Session Netty Channel线程绑定工具类
 */
public final class ConnectSessionNettyUtils {

    private static final String CONNECTION_TYPE = "connectionType";
    private static final String PROXY_SESSION = "proxySession";

    private static final AttributeKey<ProxySession> ATTR_KEY_PROXY_SESSION = AttributeKey.valueOf(PROXY_SESSION);
    private static final AttributeKey<ConnectionType> ATTR_KEY_CONNECTION_TYPE = AttributeKey.valueOf(CONNECTION_TYPE);

    private ConnectSessionNettyUtils() {
    }

    public static void setProxySession(Channel channel, ProxySession proxySession) {
        channel.attr(ConnectSessionNettyUtils.ATTR_KEY_PROXY_SESSION).set(proxySession);
    }

    public static ProxySession proxySession(Channel channel) {
        ProxySession proxySession = null;
        if (null != channel.attr(ConnectSessionNettyUtils.ATTR_KEY_PROXY_SESSION).get()) {
            proxySession = channel.attr(ConnectSessionNettyUtils.ATTR_KEY_PROXY_SESSION).get();
        }
        return proxySession;
    }

    public static void setConnectionType(Channel channel, ConnectionType connectionType) {
        channel.attr(ConnectSessionNettyUtils.ATTR_KEY_CONNECTION_TYPE).set(connectionType);
    }

    public static ConnectionType connectionType(Channel channel) {
        ConnectionType connectionType = null;
        if (null != channel.attr(ConnectSessionNettyUtils.ATTR_KEY_CONNECTION_TYPE).get()) {
            connectionType = channel.attr(ConnectSessionNettyUtils.ATTR_KEY_CONNECTION_TYPE).get();
        }
        return connectionType;
    }
}
