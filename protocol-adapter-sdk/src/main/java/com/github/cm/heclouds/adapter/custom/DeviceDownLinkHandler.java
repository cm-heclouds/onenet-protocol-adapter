package com.github.cm.heclouds.adapter.custom;

import com.github.cm.heclouds.adapter.core.entity.Device;
import com.github.cm.heclouds.adapter.core.entity.Response;
import com.google.gson.JsonObject;

/**
 * 下行数据处理接口，用于接收并处理平台接入机下行数据
 */
public interface DeviceDownLinkHandler {

    /**
     * 设备登陆响应
     *
     * @param device   设备
     * @param response 响应
     */
    void onDeviceLoginResponse(Device device, Response response);

    /**
     * 设备主动登出响应
     *
     * @param device   设备
     * @param response 响应
     */
    void onDeviceLogoutResponse(Device device, Response response);

    /**
     * 平台主动登出设备
     *
     * @param device   设备
     * @param response 响应
     */
    void onDeviceNotifiedLogout(Device device, Response response);

    /**
     * 平台对于设备属性上传的响应
     *
     * @param device   设备
     * @param response 响应
     */
    void onPropertyUploadResponse(Device device, Response response);

    /**
     * 平台对于设备事件上报的响应
     *
     * @param device   设备
     * @param response 响应
     */
    void onEventUploadResponse(Device device, Response response);

    /**
     * 收到平台下发设备属性设置
     *
     * @param device  平台下行物模型数据
     * @param id      平台下发设备属性消息id
     * @param version 平台下发设备属性版本
     * @param params  平台下发设备属性参数
     */
    void onPropertySetRequest(Device device, String id, String version, JsonObject params);

    /**
     * 平台对于设备获取期望值响应
     *
     * @param device   设备
     * @param response 响应
     */
    void onDesiredGetResponse(Device device, Response response);

    /**
     * 平台对于设备删除期望值响应
     *
     * @param device   设备
     * @param response 响应
     */
    void onDesiredDeleteResponse(Device device, Response response);


}
