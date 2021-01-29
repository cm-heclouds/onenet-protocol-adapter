package com.github.cm.heclouds.adapter.api;

import com.github.cm.heclouds.adapter.core.entity.Device;
import com.google.gson.JsonObject;

/**
 * 物模型服务调用监听接口
 * <p>
 * 此接口非必须实现，可使用物模型代码生成工具自动生成的
 * {@link DeviceServiceInvokeListener+pid}代码
 */
public interface DeviceServiceInvokeListener {

    /**
     * 接收物模型服务调用请求
     *
     * @param device     设备
     * @param identifier 服务标识符
     * @param id         id
     * @param version    version
     * @param params     参数
     */
    void onRequestReceived(Device device, String identifier, String id, String version, JsonObject params);

}
