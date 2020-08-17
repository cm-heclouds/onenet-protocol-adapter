package com.github.cm.heclouds.adapter.config;

/**
 * 泛协议接入服务配置接口类
 */
public interface IAdapterConfig {
    /**
     * 泛协议接入SDK名称，默认为SDK
     *
     * @return 泛协议接入SDK协议站TCP名称
     */
    String getName();

    /**
     * 平台接入机连接地址，默认实现中可返回null或空值
     *
     * @return connection host 平台接入机连接地址
     */
    String getConnectionHost();

    /**
     * 泛协议接入服务ID，此方法不可返回null或空值
     *
     * @return 泛协议接入服务ID
     */
    String getServiceId();

    /**
     * 泛协议接入服务实例ID，此方法不可返回null或空值
     *
     * @return 泛协议接入服务实例ID
     */
    String getInstanceName();

    /**
     * 泛协议接入服务实例Key，此方法不可返回null或空值
     *
     * @return 泛协议接入服务实例Key
     */
    String getInstanceKey();

    /**
     * 和平台接入机之间是否使用加密传输，此方法可返回null或空值，默认为true
     *
     * @return 是否支持TLS加密
     */
    Boolean tlsSupport();

    /**
     * 和平台接入机之间的控制连接断开后是否重连，此方法可返回null或空值，默认为false
     *
     * @return 是否支持TLS加密
     */
    Boolean ctrlReconnect();

    /**
     * 和平台接入机之间的控制连接异常断开后的初始重连等待时间（默认值：30，单位：秒）
     * 重连失败后等待时间会呈指数级逐渐增加，若重连成功，则等待时间重置为初始重连等待时间
     * 当ctrReconnect为ture时生效
     *
     * @return 控制连接异常断开后的初始重连等待时间，单位秒
     */
    Long getCtrlReconnectInterval();
}

