package com.github.cm.heclouds.adapter.mqttadapter.handler;

import com.github.cm.heclouds.adapter.api.ConfigUtils;
import com.github.cm.heclouds.adapter.config.IDeviceConfig;
import com.github.cm.heclouds.adapter.core.entity.*;
import com.github.cm.heclouds.adapter.core.logging.ILogger;
import com.github.cm.heclouds.adapter.core.utils.DeviceUtils;
import com.github.cm.heclouds.adapter.core.utils.GsonUtils;
import com.github.cm.heclouds.adapter.core.utils.IdUtils;
import com.github.cm.heclouds.adapter.entity.DevicePromise;
import com.github.cm.heclouds.adapter.entity.response.GetTopoResult;
import com.github.cm.heclouds.adapter.entity.sdk.DeviceSession;
import com.github.cm.heclouds.adapter.entity.sdk.MessageType;
import com.github.cm.heclouds.adapter.entity.sdk.ProxySession;
import com.github.cm.heclouds.adapter.mqttadapter.DeviceSessionManager;
import com.github.cm.heclouds.adapter.mqttadapter.ProxySessionManager;
import com.github.cm.heclouds.adapter.mqttadapter.mqtt.PromiseCanceller;
import com.github.cm.heclouds.adapter.utils.ProtocolMessageUtils;
import com.google.gson.JsonObject;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.util.internal.StringUtil;

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
     * 主动登出代理设备
     *
     * @param device 设备
     * @return 返回结果
     */
    public CallableFuture<DeviceResult> doDeviceOffline(Device device) {
        String productId = device.getProductId();
        String deviceName = device.getDeviceName();
        DeviceSession deviceSession = DeviceSessionManager.getDeviceSession(productId, deviceName);
        if (null == deviceSession || !deviceSession.isLogin()) {
            logger.logDevWarn(ConfigUtils.getName(), LOGOUT, device.getProductId(), deviceName, "offline request canceled due to device not login");
            DevicePromise<DeviceResult> promise = new DevicePromise<>();
            promise.trySuccess(new DeviceResult(ReturnCode.SUCCESS));
            return promise;
        }
        MqttMessage mqttMessage = ProtocolMessageUtils.createMqttLogoutMsg(device);
        Channel channel = deviceSession.getProxySession().getChannel();
        DevicePromise<DeviceResult> promise = new DevicePromise<>("", mqttMessage, productId, deviceName, MessageType.LOGOUT_REQUEST, channel.eventLoop());
        channel.writeAndFlush(promise).addListener(new PromiseCanceller<>(promise));
        return promise;
    }

    /**
     * 上传OneJSON数据
     *
     * @param device     设备
     * @param request    请求数据
     * @param uploadType 上传数据类型
     * @return 返回结果
     */
    public CallableFuture<DeviceResult> doUpload(Device device, Request request, UploadType uploadType) {
        return doThingPublish(device, request, uploadType);
    }

    /**
     * 上传OneJSON数据
     *
     * @param device 设备
     * @return 返回结果
     */
    public CallableFuture<GetTopoResult> doGetTopo(Device device) {
        OneJSONRequest request = new OneJSONRequest();
        String productId = device.getProductId();
        String deviceName = device.getDeviceName();
        String packetId = request.getId();
        DeviceSession deviceSession = DeviceSessionManager.getDeviceSession(productId, deviceName);
        if (null == deviceSession || !deviceSession.isLogin()) {
            logger.logDevWarn(ConfigUtils.getName(), GW_UP_LINK, device.getProductId(), deviceName, "device not login");
            DevicePromise<GetTopoResult> promise = new DevicePromise<>();
            promise.trySuccess(new GetTopoResult(packetId, ReturnCode.DEVICE_NOT_ONLINE));
            return promise;
        }
        DevicePromise<GetTopoResult> promise = new DevicePromise<>(packetId, productId, deviceName);
        promise.setMessageType(MessageType.GET_TOPO_REQUEST);
        byte[] data = request.encode();
        MqttMessage mqttMessage = ProtocolMessageUtils.createMqttSubDeviceTopoGetMsg(device, data);
        promise.setMqttMessage(mqttMessage);
        Channel channel = deviceSession.getProxySession().getChannel();
        channel.writeAndFlush(promise).addListener(new PromiseCanceller<>(promise));
        return promise;
    }


    /**
     * 响应OneJSON数据
     *
     * @param device     设备
     * @param response   响应
     * @param identifier 服务标识
     * @param replyType  响应数据类型
     * @return 消息ID
     */
    public String doReply(Device device, Response response, String identifier, ReplyType replyType) {
        doReplyThingRequest(device, response, replyType, identifier);
        return response.getId();
    }

    /**
     * 发布数据到平台
     *
     * @param device  设备
     * @param topic   topic
     * @param request 请求数据
     * @return 返回结果
     */
    public CallableFuture<DeviceResult> doPublish(Device device, String topic, String request) {
        String productId = device.getProductId();
        String deviceName = device.getDeviceName();
        String id;
        JsonObject jsonObject;
        try {
            jsonObject = GsonUtils.GSON.fromJson(request, JsonObject.class);
            id = jsonObject.get("id").getAsString();
        } catch (Exception e) {
            logger.logDevWarn(ConfigUtils.getName(), GW_UP_LINK, device.getProductId(), deviceName, "unexpected request:" + request);
            DevicePromise<DeviceResult> promise = new DevicePromise<>();
            promise.trySuccess(new DeviceResult(ReturnCode.ILLEGAL_DATA));
            return promise;
        }
        if (StringUtil.isNullOrEmpty(id)) {
            id = IdUtils.generateId();
            jsonObject.addProperty("id", id);
        }
        DeviceSession deviceSession = DeviceSessionManager.getDeviceSession(productId, deviceName);
        if (null == deviceSession || !deviceSession.isLogin()) {
            logger.logDevWarn(ConfigUtils.getName(), GW_UP_LINK, device.getProductId(), deviceName, "device not login");
            DevicePromise<DeviceResult> promise = new DevicePromise<>();
            promise.trySuccess(new DeviceResult(id, ReturnCode.DEVICE_NOT_ONLINE));
            return promise;
        }
        MqttMessage mqttMessage = ProtocolMessageUtils.createMqttPublishMsg(topic, jsonObject.toString().getBytes());
        ProxySession proxySession = deviceSession.getProxySession();
        Channel channel = proxySession.getChannel();
        DevicePromise<DeviceResult> promise = new DevicePromise<>(id, mqttMessage, productId, deviceName, MessageType.COMMON_REQUEST, channel.eventLoop());
        channel.writeAndFlush(promise).addListener(new PromiseCanceller<>(promise));
        return promise;
    }

    /**
     * 发布物模型数据到平台
     *
     * @param device     设备
     * @param request    物模型请求
     * @param uploadType 上传类型
     * @return 返回结果
     */
    public CallableFuture<DeviceResult> doThingPublish(Device device, Request request, UploadType uploadType) {
        String productId = device.getProductId();
        String deviceName = device.getDeviceName();
        String packetId = request.getId();
        DeviceSession deviceSession = DeviceSessionManager.getDeviceSession(productId, deviceName);

        if (null == deviceSession || !deviceSession.isLogin()) {
            logger.logDevWarn(ConfigUtils.getName(), GW_UP_LINK, device.getProductId(), deviceName, "device not login");
            DevicePromise<DeviceResult> promise = new DevicePromise<>();
            promise.trySuccess(new DeviceResult(packetId, ReturnCode.DEVICE_NOT_ONLINE));
            return promise;
        }
        MqttMessage mqttMessage;
        DevicePromise<DeviceResult> promise = new DevicePromise<>(packetId, productId, deviceName);
        promise.setMessageType(MessageType.UPLOAD_REQUEST);
        byte[] data = request.encode();
        switch (uploadType) {
            case PROPERTY:
                mqttMessage = ProtocolMessageUtils.createMqttPropertyUploadMsg(device, data);
                break;
            case EVENT:
                mqttMessage = ProtocolMessageUtils.createMqttEventUploadMsg(device, data);
                break;
            case DESIRED_GET:
                mqttMessage = ProtocolMessageUtils.createMqttDesiredGetMsg(device, data);
                break;
            case DESIRED_DELETE:
                mqttMessage = ProtocolMessageUtils.createMqttDesiredDeleteMsg(device, data);
                break;
            case PACK_DATA:
                mqttMessage = ProtocolMessageUtils.createMqttDevicePackDataMsg(device, data);
                break;
            case HISTORY_DATA:
                mqttMessage = ProtocolMessageUtils.createMqttDeviceHistoryDataMsg(device, data);
                break;
            case SUB_LOGIN:
                mqttMessage = ProtocolMessageUtils.createMqttSubDeviceLoginMsg(device, data);
                break;
            case SUB_LOGOUT:
                mqttMessage = ProtocolMessageUtils.createMqttSubDeviceLogoutMsg(device, data);
                break;
            case SUB_DEV_TOPO_ADD:
                mqttMessage = ProtocolMessageUtils.createMqttSubDeviceTopoAddMsg(device, data);
                break;
            case SUB_DEV_TOPO_GET:
                mqttMessage = ProtocolMessageUtils.createMqttSubDeviceTopoGetMsg(device, data);
                break;
            case SUB_DEV_TOPO_DELETE:
                mqttMessage = ProtocolMessageUtils.createMqttSubDeviceTopoDeleteMsg(device, data);
                break;
            default:
                logger.logDevWarn(ConfigUtils.getName(), DEV_UP_LINK, device.getProductId(), device.getDeviceName(), "unknown upload type:" + uploadType);
                promise = new DevicePromise<>();
                promise.trySuccess(new DeviceResult(packetId, ReturnCode.DEVICE_NOT_ONLINE));
                return promise;
        }
        promise.setMqttMessage(mqttMessage);
        Channel channel = deviceSession.getProxySession().getChannel();
        channel.writeAndFlush(promise).addListener(new PromiseCanceller<>(promise));
        return promise;
    }

    /**
     * 登录代理设备
     *
     * @param device 设备
     * @return 返回结果
     */
    public CallableFuture<DeviceResult> doDeviceOnline(Device device) {
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
                DevicePromise<DeviceResult> promise = new DevicePromise<>();
                promise.trySuccess(new DeviceResult(ReturnCode.SDK_INTERVAL_ERROR));
                return promise;
            }
            deviceSession.setProxySession(proxySession);
            proxySession.putDeviceSession(deviceSession);
        }
        if (null == proxySession.getChannel() || !proxySession.getChannel().isActive()) {
            logger.logDevWarn(ConfigUtils.getName(), LOGIN, productId, deviceName, "device session existing but proxy connection now is unavailable");
            DevicePromise<DeviceResult> promise = new DevicePromise<>();
            promise.trySuccess(new DeviceResult(ReturnCode.SDK_INTERVAL_ERROR));
            return promise;
        }

        MqttMessage mqttMessage = ProtocolMessageUtils.createMqttLoginMsg(device, deviceConfig.getOriginalIdentity(productId, deviceName));
        if (mqttMessage != null) {
            DeviceUtils.removeDeviceCloseReason(device);
            Channel channel = proxySession.getChannel();
            DevicePromise<DeviceResult> promise = new DevicePromise<>("", mqttMessage, productId, deviceName, MessageType.LOGIN_REQUEST, channel.eventLoop());
            channel.writeAndFlush(promise).addListener(new PromiseCanceller<>(promise));
            return promise;
        }
        DevicePromise<DeviceResult> promise = new DevicePromise<>();
        promise.trySuccess(new DeviceResult(ReturnCode.SDK_INTERVAL_ERROR));
        return promise;
    }

    /**
     * 回复物模型请求到平台
     *
     * @param device    设备
     * @param response  回复物模型请求数据
     * @param replyType 响应类型
     */
    private void doReplyThingRequest(Device device, Response response, ReplyType replyType, String identifier) {
        String productId = device.getProductId();
        String deviceName = device.getDeviceName();
        DeviceSession deviceSession = DeviceSessionManager.getDeviceSession(productId, deviceName);
        if (null == deviceSession || !deviceSession.isLogin()) {
            logger.logDevWarn(ConfigUtils.getName(), GW_UP_LINK, device.getProductId(), deviceName, "device not login");
            return;
        }
        byte[] data = response.encode();
        MqttMessage mqttMessage;
        switch (replyType) {
            case PROPERTY_SET:
                mqttMessage = ProtocolMessageUtils.createMqttPropertySetRequestReplyMsg(device, data);
                break;
            case PROPERTY_GET:
                mqttMessage = ProtocolMessageUtils.createMqttPropertyGetRequestReplyMsg(device, data);
                break;
            case SERVICE_INVOKE:
                mqttMessage = ProtocolMessageUtils.createMqttServiceInvokeRequestReplyMsg(device, data, identifier);
                break;
            case SUB_DEV_TOPO_GET_RESULT:
                mqttMessage = ProtocolMessageUtils.createMqttSubDeviceTopoGetResultMsg(device, data);
                break;
            case SUB_DEV_TOPO_CHANGE_REPLY:
                mqttMessage = ProtocolMessageUtils.createMqttSubDeviceTopoChangeReplyMsg(device, data);
                break;
            case SUB_PROPERTY_SET:
                mqttMessage = ProtocolMessageUtils.createMqttSubDevicePropertySetRequestReplyMsg(device, data);
                break;
            case SUB_PROPERTY_GET:
                mqttMessage = ProtocolMessageUtils.createMqttSubDevicePropertyGetRequestReplyMsg(device, data);
                break;
            case SUB_SERVICE_INVOKE:
                mqttMessage = ProtocolMessageUtils.createMqttSubDeviceServiceInvokeRequestReplyMsg(device, data);
                break;
            default:
                logger.logDevWarn(ConfigUtils.getName(), GW_UP_LINK, device.getProductId(), deviceName, "unknown thing request reply type: " + replyType);
                return;
        }
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
        DESIRED_DELETE,
        /**
         * 设备批量上传数据
         */
        PACK_DATA,
        /**
         * 设备上传历史数据
         */
        HISTORY_DATA,
        /**
         * 子设备登录
         */
        SUB_LOGIN,
        /**
         * 子设备登出
         */
        SUB_LOGOUT,
        /**
         * 添加拓扑关系
         */
        SUB_DEV_TOPO_ADD,

        /**
         * 获取拓扑关系
         */
        SUB_DEV_TOPO_GET,

        /**
         * 删除拓扑关系
         */
        SUB_DEV_TOPO_DELETE
    }

    public enum ReplyType {
        /**
         * 回复设置设备属性请求
         */
        PROPERTY_SET,
        /**
         * 回复获取设备属性请求
         */
        PROPERTY_GET,
        /**
         * 回复请求
         */
        SERVICE_INVOKE,
        /**
         * 网关同步结果响应
         */
        SUB_DEV_TOPO_GET_RESULT,
        /**
         * 网关设备回复topo变更
         */
        SUB_DEV_TOPO_CHANGE_REPLY,
        /**
         * 回复设置子设备属性请求
         */
        SUB_PROPERTY_SET,
        /**
         * 回复子设备属性获取
         */
        SUB_PROPERTY_GET,
        /**
         * 回复子设备服务调用
         */
        SUB_SERVICE_INVOKE
    }
}
