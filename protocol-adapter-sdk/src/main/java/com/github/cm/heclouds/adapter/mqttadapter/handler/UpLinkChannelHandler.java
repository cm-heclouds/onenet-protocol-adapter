package com.github.cm.heclouds.adapter.mqttadapter.handler;

import com.github.cm.heclouds.adapter.api.ConfigUtils;
import com.github.cm.heclouds.adapter.config.IDeviceConfig;
import com.github.cm.heclouds.adapter.entity.DeviceSession;
import com.github.cm.heclouds.adapter.entity.ProxySession;
import com.github.cm.heclouds.adapter.mqttadapter.DeviceSessionManager;
import com.github.cm.heclouds.adapter.mqttadapter.ProxySessionManager;
import com.github.cm.heclouds.adapter.mqttadapter.codec.ProtocolMessageUtil;
import com.github.cm.heclouds.adapter.core.entity.Device;
import com.github.cm.heclouds.adapter.core.logging.ILogger;
import io.netty.handler.codec.mqtt.MqttMessage;

import static com.github.cm.heclouds.adapter.core.logging.LoggerFormat.Action.*;


/**
 * 用于上行消息给平台
 */
public final class UpLinkChannelHandler {

    public static final UpLinkChannelHandler INSTANCE = new UpLinkChannelHandler();

    private final ILogger logger = ConfigUtils.getLogger();
    private final IDeviceConfig deviceConfig = ConfigUtils.getDeviceConfig();

    private UpLinkChannelHandler() {
    }

    /**
     * 发布物模型数据到平台
     *
     * @param device     设备
     * @param data       物模型数据
     * @param uploadType 上传类型
     */
    public void doThingPublish(Device device, byte[] data, UploadType uploadType) {
        String productId = device.getProductId();
        String deviceName = device.getDeviceName();
        DeviceSession deviceSession = DeviceSessionManager.getDeviceSession(productId, deviceName);
        if (null == deviceSession || !deviceSession.isLogin()) {
            logger.logDevWarn(ConfigUtils.getName(), GW_UP_LINK, device.getProductId(), deviceName, "device not login");
            return;
        }
        MqttMessage mqttMessage;
        switch (uploadType) {
            case PROPERTY:
                mqttMessage = ProtocolMessageUtil.createMqttPropertyUploadMsg(device, data);
                break;
            case EVENT:
                mqttMessage = ProtocolMessageUtil.createMqttEventUploadMsg(device, data);
                break;
            case DESIRED_GET:
                mqttMessage = ProtocolMessageUtil.createMqttDesiredGetMsg(device, data);
                break;
            case DESIRED_DELETE:
                mqttMessage = ProtocolMessageUtil.createMqttDesiredDeleteMsg(device, data);
                break;
            default:
                logger.logDevWarn(ConfigUtils.getName(), DEV_UP_LINK, device.getProductId(), device.getDeviceName(), "unknown upload type:" + uploadType);
                return;
        }
        deviceSession.getProxySession().getChannel().writeAndFlush(mqttMessage);
    }

    /**
     * 登录代理设备
     *
     * @param device 设备
     */
    public void doDeviceOnline(Device device) {
        String productId = device.getProductId();
        String deviceName = device.getDeviceName();
        DeviceSession deviceSession = DeviceSessionManager.getDeviceSession(productId, deviceName);
        if (deviceSession == null) {
            // 创建DeviceSession
            deviceSession = DeviceSessionManager.createDevSession(
                    productId,
                    deviceName);
            DeviceSessionManager.putDeviceSession(deviceSession);
        }
        ProxySession proxySession = deviceSession.getProxySession();
        if (proxySession == null) {
            proxySession = ProxySessionManager.chooseProxySession();
            if (proxySession == null) {
                logger.logDevWarn(ConfigUtils.getName(), LOGIN, productId, deviceName, "no available proxy session");
                return;
            }
            deviceSession.setProxySession(proxySession);
            proxySession.putDeviceSession(deviceSession);
        }
        if (null == proxySession.getChannel() || !proxySession.getChannel().isActive()) {
            logger.logDevWarn(ConfigUtils.getName(), LOGIN, productId, deviceName, "device session existing but proxy connection now is unavailable");
            return;
        }

        MqttMessage mqttMessage = ProtocolMessageUtil.createMqttLoginMsg(device, deviceConfig.getOriginalIdentity(productId, deviceName));
        if (mqttMessage != null) {
            proxySession.getChannel().writeAndFlush(mqttMessage);
        }
    }

    /**
     * 主动登出代理设备
     *
     * @param device 设备
     */
    public void doDeviceOffline(Device device) {
        String productId = device.getProductId();
        String deviceName = device.getDeviceName();
        DeviceSession deviceSession = DeviceSessionManager.getDeviceSession(productId, deviceName);
        if (null == deviceSession || !deviceSession.isLogin()) {
            logger.logDevWarn(ConfigUtils.getName(), LOGOUT, device.getProductId(), deviceName, "offline request canceled due to device not login");
            return;
        }
        deviceSession.setLogin(false);
        MqttMessage mqttMessage = ProtocolMessageUtil.createMqttLogoutMsg(device);
        deviceSession.getProxySession().getChannel().writeAndFlush(mqttMessage);
    }

    /**
     * 回复物模型命令到平台
     *
     * @param device 设备
     * @param data   物模型命令回复数据
     */
    public void doReplyThingRequest(Device device, byte[] data) {
        String productId = device.getProductId();
        String deviceName = device.getDeviceName();
        DeviceSession deviceSession = DeviceSessionManager.getDeviceSession(productId, deviceName);
        if (null == deviceSession || !deviceSession.isLogin()) {
            logger.logDevWarn(ConfigUtils.getName(), GW_UP_LINK, device.getProductId(), deviceName, "device not login");
            return;
        }
        MqttMessage mqttMessage = ProtocolMessageUtil.createMqttThingReplyMsg(device, data);
        deviceSession.getProxySession().getChannel().writeAndFlush(mqttMessage);
    }

    public enum UploadType {
        /**
         * 上传设备属性
         */
        PROPERTY,
        /**
         * 上报设备事件
         */
        EVENT,
        /**
         * 获取设备属性期望值
         */
        DESIRED_GET,
        /**
         * 删除设备属性期望值
         */
        DESIRED_DELETE
    }
}
