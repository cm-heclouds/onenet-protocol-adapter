package com.github.cm.heclouds.adapter.utils;

import com.github.cm.heclouds.adapter.api.ConfigUtils;
import com.github.cm.heclouds.adapter.core.entity.Device;
import com.github.cm.heclouds.adapter.core.logging.ILogger;
import com.github.cm.heclouds.adapter.exceptions.InvalidMqttTopicException;
import com.github.cm.heclouds.adapter.exceptions.UnsupportedMqttMessageTypeException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.*;
import javafx.util.Pair;

import static com.github.cm.heclouds.adapter.core.logging.LoggerFormat.Action.LOGIN;


/**
 * 泛协议接入SDK内部数据工具类
 */
public final class ProtocolMessageUtils {

    private static final ILogger LOGGER = ConfigUtils.getLogger();

    /**
     * 创建MQTT发布消息
     *
     * @param topic MQTT发布主题
     * @param data  MQTT发布payload
     * @return MQTT发布消息
     */
    public static MqttMessage createMqttPublishMsg(String topic, byte[] data) {
        return createPublishMessage(topic, data);
    }

    /**
     * 创建MQTT登陆消息
     *
     * @param device 设备
     * @return MQTT登陆消息
     */
    public static MqttMessage createMqttLoginMsg(Device device, String originalIdentity) {
        String productId = device.getProductId();
        String deviceName = device.getDeviceName();
        String deviceKey = device.getKey();
        String topic = TopicUtils.createLoginTopic(productId, device.getDeviceName());
        String sasToken = SasTokenGenerator.deviceSasToken(productId, deviceName, deviceKey);
        if (sasToken == null) {
            LOGGER.logDevWarn(ConfigUtils.getName(), LOGIN, productId, deviceName, "gen device token failed, deviceKey=" + deviceKey);
            return null;
        }
        return createPublishMessage(topic, genDeviceOnlineJsonPayload(sasToken, originalIdentity).getBytes());
    }

    /**
     * 创建MQTT登陆消息
     *
     * @param device 设备
     * @return MQTT登陆消息
     */
    public static MqttMessage createMqttLogoutMsg(Device device) {
        String topic = TopicUtils.createLogoutTopic(device.getProductId(), device.getDeviceName());
        return createPublishMessage(topic, null);
    }

    /**
     * 创建MQTT设备属性上传消息
     *
     * @param device 设备
     * @param data   物模型数据
     * @return MQTT消息
     */
    public static MqttMessage createMqttPropertyUploadMsg(Device device, byte[] data) {
        String topic = TopicUtils.createMqttPropertyUploadTopic(device.getProductId(), device.getDeviceName());
        return createPublishMessage(topic, data);
    }

    /**
     * 创建MQTT设备事件上传消息
     *
     * @param device 设备
     * @param data   物模型数据
     * @return MQTT消息
     */
    public static MqttMessage createMqttEventUploadMsg(Device device, byte[] data) {
        String topic = TopicUtils.createMqttEventUploadTopic(device.getProductId(), device.getDeviceName());
        return createPublishMessage(topic, data);
    }

    /**
     * 创建MQTT获取设备属性期望值消息
     *
     * @param device 设备
     * @param data   物模型数据回复
     * @return MQTT获取设备属性期望值消息
     */
    public static MqttMessage createMqttDesiredGetMsg(Device device, byte[] data) {
        String topic = TopicUtils.createDesiredGetMsgTopic(device.getProductId(), device.getDeviceName());
        return createPublishMessage(topic, data);
    }

    /**
     * 创建MQTT清空设备属性期望值消息
     *
     * @param device 设备
     * @param data   物模型数据回复
     * @return MQTT清空设备属性期望值消息
     */
    public static MqttMessage createMqttDesiredDeleteMsg(Device device, byte[] data) {
        String topic = TopicUtils.createDesiredDeleteMsgTopic(device.getProductId(), device.getDeviceName());
        return createPublishMessage(topic, data);
    }

    /**
     * 创建回复设置属性请求的MQTT物模型消息
     *
     * @param device 设备
     * @param data   物模型数据回复
     * @return MQTT物模型数据回复消息
     */
    public static MqttMessage createMqttPropertySetRequestReplyMsg(Device device, byte[] data) {
        String topic = TopicUtils.createMqttReplyPropertySetRequestTopic(device.getProductId(), device.getDeviceName());
        return createPublishMessage(topic, data);
    }

    /**
     * 创建回复设备服务调用请求的MQTT物模型消息
     *
     * @param device     设备
     * @param data       物模型数据回复
     * @param identifier 服务标识
     * @return MQTT物模型数据回复消息
     */
    public static MqttMessage createMqttServiceInvokeRequestReplyMsg(Device device, byte[] data, String identifier) {
        String topic = TopicUtils.createMqttReplyServiceInvokeRequestTopic(device.getProductId(), device.getDeviceName(), identifier);
        return createPublishMessage(topic, data);
    }

    /**
     * 创建回复获取属性请求的MQTT物模型消息
     *
     * @param device 设备
     * @param data   物模型数据回复
     * @return MQTT物模型数据回复消息
     */
    public static MqttMessage createMqttPropertyGetRequestReplyMsg(Device device, byte[] data) {
        String topic = TopicUtils.createMqttReplyPropertyGetRequestTopic(device.getProductId(), device.getDeviceName());
        return createPublishMessage(topic, data);
    }

    /**
     * 创建MQTT设备批量上传数据消息
     *
     * @param device 设备
     * @param data   物模型数据回复
     * @return MQTT设备设备批量上传数据消息
     */
    public static MqttMessage createMqttDevicePackDataMsg(Device device, byte[] data) {
        String topic = TopicUtils.createDevicePackDataTopic(device.getProductId(), device.getDeviceName());
        return createPublishMessage(topic, data);
    }

    /**
     * 创建MQTT设备上传历史数据消息
     *
     * @param device 设备
     * @param data   物模型数据回复
     * @return MQTT设备上传历史数据消息
     */
    public static MqttMessage createMqttDeviceHistoryDataMsg(Device device, byte[] data) {
        String topic = TopicUtils.createDeviceHistoryDataTopic(device.getProductId(), device.getDeviceName());
        return createPublishMessage(topic, data);
    }

    /**
     * 创建MQTT子设备设备登录消息
     *
     * @param device 子设备
     * @param data   物模型数据回复
     * @return MQTT子设备设备登录消息
     */
    public static MqttMessage createMqttSubDeviceLoginMsg(Device device, byte[] data) {
        String topic = TopicUtils.createSubDeviceLoginTopic(device.getProductId(), device.getDeviceName());
        return createPublishMessage(topic, data);
    }

    /**
     * 创建MQTT子设备设备登出消息
     *
     * @param device 设备
     * @param data   物模型数据回复
     * @return MQTT子设备设备登出消息
     */
    public static MqttMessage createMqttSubDeviceLogoutMsg(Device device, byte[] data) {
        String topic = TopicUtils.createSubDeviceLogoutTopic(device.getProductId(), device.getDeviceName());
        return createPublishMessage(topic, data);
    }

    /**
     * 创建回复子设备设置属性请求的MQTT物模型消息
     *
     * @param device 设备
     * @param data   物模型数据回复
     * @return MQTT物模型数据回复消息
     */
    public static MqttMessage createMqttSubDevicePropertySetRequestReplyMsg(Device device, byte[] data) {
        String topic = TopicUtils.createMqttSubDeviceReplyPropertySetRequestTopic(device.getProductId(), device.getDeviceName());
        return createPublishMessage(topic, data);
    }

    /**
     * 创建子设备回复获取属性请求的MQTT物模型消息
     *
     * @param device 设备
     * @param data   物模型数据回复
     * @return MQTT物模型数据回复消息
     */
    public static MqttMessage createMqttSubDevicePropertyGetRequestReplyMsg(Device device, byte[] data) {
        String topic = TopicUtils.createMqttSubDeviceReplyPropertyGetRequestTopic(device.getProductId(), device.getDeviceName());
        return createPublishMessage(topic, data);
    }

    /**
     * 创建回复子设备设备服务调用请求的MQTT物模型消息
     *
     * @param device 设备
     * @param data   物模型数据回复
     * @return MQTT物模型数据回复消息
     */
    public static MqttMessage createMqttSubDeviceServiceInvokeRequestReplyMsg(Device device, byte[] data) {
        String topic = TopicUtils.createMqttSubDeviceReplyServiceInvokeRequestTopic(device.getProductId(), device.getDeviceName());
        return createPublishMessage(topic, data);
    }

    /**
     * 创建MQTT子设备新增拓扑关系消息
     *
     * @param device 设备
     * @param data   物模型数据回复
     * @return MQTT子设备新增拓扑消息消息
     */
    public static MqttMessage createMqttSubDeviceTopoAddMsg(Device device, byte[] data) {
        String topic = TopicUtils.createSubDeviceTopoAddTopic(device.getProductId(), device.getDeviceName());
        return createPublishMessage(topic, data);
    }

    /**
     * 创建MQTT子设备删除拓扑关系消息
     *
     * @param device 设备
     * @param data   物模型数据回复
     * @return MQTT子设备删除拓扑消息消息
     */
    public static MqttMessage createMqttSubDeviceTopoDeleteMsg(Device device, byte[] data) {
        String topic = TopicUtils.createSubDeviceTopoDeleteTopic(device.getProductId(), device.getDeviceName());
        return createPublishMessage(topic, data);
    }

    /**
     * 创建MQTT子设备获取拓扑关系消息
     *
     * @param device 设备
     * @param data   物模型数据回复
     * @return MQTT子设备获取拓扑消息消息
     */
    public static MqttMessage createMqttSubDeviceTopoGetMsg(Device device, byte[] data) {
        String topic = TopicUtils.createSubDeviceTopoGetTopic(device.getProductId(), device.getDeviceName());
        return createPublishMessage(topic, data);
    }

    /**
     * 创建MQTT网关拓扑关系同步结果响应消息
     *
     * @param device 设备
     * @param data   物模型数据回复
     * @return MQTT网关拓扑关系同步结果响应消息
     */
    public static MqttMessage createMqttSubDeviceTopoGetResultMsg(Device device, byte[] data) {
        String topic = TopicUtils.createSubDeviceTopoGetResultTopic(device.getProductId(), device.getDeviceName());
        return createPublishMessage(topic, data);
    }

    /**
     * 创建MQTT子设备拓扑关系改变回复消息
     *
     * @param device 设备
     * @param data   物模型数据回复
     * @return MQTT子设备拓扑关系改变回复消息
     */
    public static MqttMessage createMqttSubDeviceTopoChangeReplyMsg(Device device, byte[] data) {
        String topic = TopicUtils.createSubDeviceTopoChangeReplyTopic(device.getProductId(), device.getDeviceName());
        return createPublishMessage(topic, data);
    }

    public static MqttPublishMessage validateMqttMessage(MqttMessage mqttMessage) {
        if (MqttMessageType.PUBLISH != mqttMessage.fixedHeader().messageType()) {
            throw new UnsupportedMqttMessageTypeException("only publish message could be decoded to DeviceMessage");
        }
        MqttPublishMessage publishMessage = (MqttPublishMessage) mqttMessage;
        String[] tokens = TopicUtils.splitTopic(publishMessage.variableHeader().topicName());
        int minTopicLevel = 4;
        if (tokens.length < minTopicLevel) {
            throw new InvalidMqttTopicException("downlink topic level less than 4");
        }
        return publishMessage;
    }

    /**
     * @param tokens 分割后的topic tokens
     * @return 设备
     */
    public static Pair<String, String> extractDeviceInfoFromTopic(String[] tokens) {
        return new Pair<>(tokens[1], tokens[2]);
    }

    /**
     * @param topic   上行publish消息的mqtt topic
     * @param payload 上行publish消息的payload
     * @return MqttMessage
     */
    private static MqttMessage createPublishMessage(String topic, byte[] payload) {
        MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.AT_MOST_ONCE, false, 0);
        MqttPublishVariableHeader variableHeader = new MqttPublishVariableHeader(topic, 0);
        payload = payload == null ? new byte[0] : payload;
        ByteBuf byteBuf = Unpooled.wrappedBuffer(payload);
        return new MqttPublishMessage(fixedHeader, variableHeader, byteBuf);
    }

    /**
     * 生成设备登录用的Payload
     *
     * @param sasToken sasToken
     * @return 设备登录用的Payload
     */
    private static String genDeviceOnlineJsonPayload(String sasToken, String originalIdentity) {
        return "{\"Authorization\": \""
                + sasToken
                + "\"," +
                "\"OriginalIdentity\": \""
                + originalIdentity
                + "\"}";
    }
}
