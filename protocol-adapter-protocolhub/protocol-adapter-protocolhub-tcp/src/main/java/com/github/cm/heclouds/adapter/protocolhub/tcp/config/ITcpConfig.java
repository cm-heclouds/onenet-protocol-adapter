package com.github.cm.heclouds.adapter.protocolhub.tcp.config;

/**
 * TCP协议站服务配置接口类
 */
public interface ITcpConfig {

    /**
     * 泛协议接入SDK协议站TCP名称，默认为TCPHub
     *
     * @return 泛协议接入SDK协议站TCP名称
     */
    String getName();

    /**
     * 泛协议接入SDK协议站TCP启动的地址，默认为本地地址
     *
     * @return 泛协议接入SDK协议站TCP启动的端口
     */
    String getHost();

    /**
     * 泛协议接入SDK协议站TCP启动的端口，默认为10086
     *
     * @return 泛协议接入SDK协议站TCP启动的端口
     */
    Integer getPort();
}

