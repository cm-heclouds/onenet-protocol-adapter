package com.github.cm.heclouds.adapter.api;

import com.github.cm.heclouds.adapter.core.entity.Device;
import com.google.gson.JsonObject;

/**
 * 物模型属性设置下发监听接口
 * <p>
 * 此接口非必须实现，可使用物模型代码生成工具自动生成的
 * {@link DeviceCommandListener +pid}代码
 * <p>
 * 注意，自动生成代码会拆分下发的属性设置
 */
public interface DeviceCommandListener {

    void onCommandReceived(Device device, String id, String version, JsonObject params);

}
