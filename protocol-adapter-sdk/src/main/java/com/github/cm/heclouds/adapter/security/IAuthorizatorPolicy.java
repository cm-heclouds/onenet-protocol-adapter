package com.github.cm.heclouds.adapter.security;

/**
 * 安全策略接口
 */
public interface IAuthorizatorPolicy {

    /**
     * @param productId  产品ID
     * @param deviceName 设备名称
     * @return 是否可写
     */
    boolean canWrite(String productId, String deviceName);

    /**
     * @param productId  产品ID
     * @param deviceName 设备名称
     * @return 是否可读
     */
    boolean canRead(String productId, String deviceName);

}
