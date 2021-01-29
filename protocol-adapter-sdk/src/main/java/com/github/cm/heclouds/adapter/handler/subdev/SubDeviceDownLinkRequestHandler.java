package com.github.cm.heclouds.adapter.handler.subdev;

import com.github.cm.heclouds.adapter.core.entity.Device;
import com.google.gson.JsonObject;

/**
 * 子设备平台下行请求处理接口
 * <p>
 * 此接口用于接收并处理平台接入机子设备下行请求数据
 */
public interface SubDeviceDownLinkRequestHandler {

    /**
     * 收到平台下发子设备属性设置
     *
     * @param device  目标网关设备
     * @param id      平台下发子设备属性消息id
     * @param version 平台下发子设备属性版本
     * @param params  平台下发子设备属性参数
     */
    void onSetSubDevicePropertyRequest(Device device, String id, String version, JsonObject params);

    /**
     * 收到平台获取子设备属性请求
     *
     * @param device  目标网关设备
     * @param id      平台下发获取设备属性消息id
     * @param version 平台下发获取设备属性版本
     * @param params  平台下发获取设备属性列表
     */
    void onGetSubDevicePropertyRequest(Device device, String id, String version, JsonObject params);

    /**
     * 收到平台调用子设备服务请求
     *
     * @param device  目标网关设备
     * @param id      平台调用子设备服务消息id
     * @param version 平台调用子设备服务版本
     * @param params  平台调用子设备服务请求数据
     */
    void onInvokeSubDeviceServiceRequest(Device device, String id, String version, JsonObject params);

    /**
     * 收到平台改变子设备拓扑关系请求
     *
     * @param device  目标网关设备
     * @param id      平台调用子设备服务消息id
     * @param version 平台调用子设备服务版本
     * @param params  平台调用子设备服务请求数据
     */
    void onSubDeviceTopoChangeRequest(Device device, String id, String version, JsonObject params);
}
