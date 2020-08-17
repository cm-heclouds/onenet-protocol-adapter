package com.github.cm.heclouds.adapter.security;

/**
 * 鉴权接口
 */
public interface IAuthenticator {

    /**
     * 检查是否有效
     *
     * @param productId  产品ID
     * @param deviceName 设备名称
     * @return 是否有效
     */
    boolean checkValid(String productId, String deviceName);

}
