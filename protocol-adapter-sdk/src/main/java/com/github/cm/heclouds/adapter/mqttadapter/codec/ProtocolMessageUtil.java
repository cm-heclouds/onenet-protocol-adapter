package com.github.cm.heclouds.adapter.mqttadapter.codec;

import com.github.cm.heclouds.adapter.api.ConfigUtils;
import com.github.cm.heclouds.adapter.exceptions.InvalidMqttTopicException;
import com.github.cm.heclouds.adapter.exceptions.UnsupportedMqttMessageTypeException;
import com.github.cm.heclouds.adapter.utils.SasTokenGenerator;
import com.github.cm.heclouds.adapter.core.entity.Device;
import com.github.cm.heclouds.adapter.core.logging.ILogger;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.*;
import javafx.util.Pair;

import static com.github.cm.heclouds.adapter.core.logging.LoggerFormat.Action.LOGIN;


/**
 * 泛协议接入SDK内部数据工具类
 */
public final class ProtocolMessageUtil {

    private static final ILogger LOGGER = ConfigUtils.getLogger();

    /**
     * 创建MQTT登陆消息
     *
     * @param device 设备
     * @return MQTT登陆消息
     */
    public static MqttMessage createMqttLoginMsg(Device device, String originalIdentity) {
        String productId = device.getProductId();
        String deviceName = device.getDeviceName();
        String deviceKey = device.getDeviceKey();
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
     * 创建MQTT物模型数据回复消息
     *
     * @param device 设备
     * @param data   物模型数据回复
     * @return MQTT物模型数据回复消息
     */
    public static MqttMessage createMqttThingReplyMsg(Device device, byte[] data) {
        String topic = TopicUtils.createThingRespondTopic(device.getProductId(), device.getDeviceName());
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
