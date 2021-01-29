package com.github.cm.heclouds.adapter.api;

import com.github.cm.heclouds.adapter.core.entity.Device;
import com.google.gson.JsonObject;

/**
 * 物模型属性设置下发监听接口
 * <p>
 * 此接口非必须实现，可使用物模型代码生成工具自动生成的
 * {@link DevicePropertySetListener+pid}代码
 * <p>
 * 注意，自动生成代码会拆分下发的属性设置
 */
public interface DevicePropertySetListener {

    /**
     * 接收物模型属性设置下发请求
     *
     * @param device  设备
     * @param id      id
     * @param version version
     * @param params  参数
     */
    void onRequestReceived(Device device, String id, String version, JsonObject params);

}
