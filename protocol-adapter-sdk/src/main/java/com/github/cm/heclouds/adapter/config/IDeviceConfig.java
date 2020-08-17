package com.github.cm.heclouds.adapter.config;


import com.github.cm.heclouds.adapter.core.entity.Device;

/**
 * 设备配置接口类，默认不使用
 */
public interface IDeviceConfig {

    /**
     * 获取平台设备相关信息
     *
     * @param originalIdentity 每个设备的唯一识别字符串
     * @return 平台设备实体相关信息，可能为null
     */
    Device getDeviceEntity(String originalIdentity);

    /**
     * 获取设备唯一识别字符串
     *
     * @param productId  平台产品id
     * @param deviceName 设备名称
     * @return 设备唯一识别字符串
     */
    String getOriginalIdentity(String productId, String deviceName);

}
