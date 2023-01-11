package com.github.cm.heclouds.adapter.utils;

import com.github.cm.heclouds.adapter.exceptions.IllegalConfigException;

import java.net.InetSocketAddress;

/**
 * InetSocketAddress工具类
 */
public class InetSocketAddressUtils {

    private InetSocketAddressUtils() {
    }

    /**
     * 获取ConnectionHost
     *
     * @param connectionHost connectionHost
     * @return InetSocketAddress
     */
    public static InetSocketAddress getConnectionHost(String connectionHost) {
        String hostname;
        Integer port;
        try {
            hostname = connectionHost.substring(0, connectionHost.lastIndexOf(":"));
            port = Integer.valueOf(connectionHost.substring(connectionHost.lastIndexOf(":") + 1));
        } catch (Exception e) {
            throw new IllegalConfigException("illegal connection host:" + connectionHost);
        }
        return new InetSocketAddress(hostname, port);
    }


}
