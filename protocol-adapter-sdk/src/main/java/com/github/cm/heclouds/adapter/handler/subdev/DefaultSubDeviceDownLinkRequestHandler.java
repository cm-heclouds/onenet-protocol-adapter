package com.github.cm.heclouds.adapter.handler.subdev;

import com.github.cm.heclouds.adapter.api.ConfigUtils;
import com.github.cm.heclouds.adapter.core.entity.Device;
import com.github.cm.heclouds.adapter.core.logging.ILogger;
import com.google.gson.JsonObject;

import static com.github.cm.heclouds.adapter.core.logging.LoggerFormat.Action.PLATFORM_DOWN_LINK;

/**
 * {@link SubDeviceDownLinkRequestHandler} 的默认实现
 */
public class DefaultSubDeviceDownLinkRequestHandler implements SubDeviceDownLinkRequestHandler {

    private final ILogger logger = ConfigUtils.getLogger();

    @Override
    public void onSetSubDevicePropertyRequest(Device device, String id, String version, JsonObject params) {
        logger.logDevInfo(ConfigUtils.getName(), PLATFORM_DOWN_LINK, device.getProductId(), device.getDeviceName(), "received set sub device property request: " +
                "id=" + id + ", version=" + version + ", params=" + params);
    }

    @Override
    public void onGetSubDevicePropertyRequest(Device device, String id, String version, JsonObject params) {
        logger.logDevInfo(ConfigUtils.getName(), PLATFORM_DOWN_LINK, device.getProductId(), device.getDeviceName(), "received get sub device property request: " +
                "id=" + id + ", version=" + version + ", params=" + params);
    }

    @Override
    public void onInvokeSubDeviceServiceRequest(Device device, String id, String version, JsonObject params) {
        logger.logDevInfo(ConfigUtils.getName(), PLATFORM_DOWN_LINK, device.getProductId(), device.getDeviceName(), "received invoke sub device service request: " +
                "id=" + id + ", version=" + version + ", params=" + params);
    }

    @Override
    public void onSubDeviceTopoChangeRequest(Device device, String id, String version, JsonObject params) {
        logger.logDevInfo(ConfigUtils.getName(), PLATFORM_DOWN_LINK, device.getProductId(), device.getDeviceName(), "received sub device topo change request: " +
                "id=" + id + ", version=" + version + ", params=" + params);
    }
}
