package com.github.cm.heclouds.adapter.handler;

import com.github.cm.heclouds.adapter.core.entity.Device;
import com.github.cm.heclouds.adapter.core.entity.Response;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * 平台下行请求处理接口
 * <p>
 * 此接口用于接收并处理平台接入机下行请求数据
 */
public interface DownLinkRequestHandler {

    /**
     * 收到平台主动登出设备
     *
     * @param device   设备
     * @param response 响应
     */
    void onDeviceNotifiedLogout(Device device, Response response);

    /**
     * 收到平台下发设置设备属性请求
     *
     * @param device  目标设备
     * @param id      平台下发设备属性消息id
     * @param version 平台下发设备属性版本
     * @param params  平台下发设备属性参数
     */
    void onSetPropertyRequest(Device device, String id, String version, JsonObject params);

    /**
     * 收到平台下发获取设备属性请求
     *
     * @param device  目标设备
     * @param id      平台下发获取设备属性消息id
     * @param version 平台下发获取设备属性版本
     * @param params  平台下发获取设备属性列表
     */
    void onGetPropertyRequest(Device device, String id, String version, List<String> params);

    /**
     * 收到平台下发调用设备服务请求
     *
     * @param device     目标设备
     * @param identifier 服务标识符
     * @param id         平台调用设备服务消息id
     * @param version    平台调用设备服务版本
     * @param params     平台调用设备服务请求数据
     */
    void onInvokeServiceRequest(Device device, String identifier, String id, String version, JsonObject params);
}
