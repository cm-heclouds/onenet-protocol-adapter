package com.github.cm.heclouds.adapter.handler;

import com.github.cm.heclouds.adapter.api.ConfigUtils;
import com.github.cm.heclouds.adapter.core.entity.Device;
import com.github.cm.heclouds.adapter.core.entity.Response;
import com.github.cm.heclouds.adapter.core.logging.ILogger;
import com.google.gson.JsonObject;

import java.util.List;

import static com.github.cm.heclouds.adapter.core.logging.LoggerFormat.Action.LOGOUT;
import static com.github.cm.heclouds.adapter.core.logging.LoggerFormat.Action.PLATFORM_DOWN_LINK;

/**
 * {@link DownLinkRequestHandler}的默认实现
 */
public final class DefaultDeviceDownLinkHandler implements DownLinkRequestHandler {

    private final ILogger logger = ConfigUtils.getLogger();

    @Override
    public void onDeviceNotifiedLogout(Device device, Response response) {
        logger.logDevInfo(ConfigUtils.getName(), LOGOUT, device.getProductId(), device.getDeviceName(), "logout notified, response: " + response);
    }

    @Override
    public void onSetPropertyRequest(Device device, String id, String version, JsonObject params) {
        logger.logDevInfo(ConfigUtils.getName(), PLATFORM_DOWN_LINK, device.getProductId(), device.getDeviceName(), "received set property request: " +
                "id=" + id + ", version=" + version + ", params=" + params);
    }

    @Override
    public void onGetPropertyRequest(Device device, String id, String version, List<String> params) {
        logger.logDevInfo(ConfigUtils.getName(), PLATFORM_DOWN_LINK, device.getProductId(), device.getDeviceName(), "received get property request:" +
                "id=" + id + ", version=" + version + ", params=" + params);
    }

    @Override
    public void onInvokeServiceRequest(Device device, String identifier, String id, String version, JsonObject params) {
        logger.logDevInfo(ConfigUtils.getName(), PLATFORM_DOWN_LINK, device.getProductId(), device.getDeviceName(), "received invoke service request:" +
                "id=" + id + ", version=" + version + ", params=" + params);
    }
}