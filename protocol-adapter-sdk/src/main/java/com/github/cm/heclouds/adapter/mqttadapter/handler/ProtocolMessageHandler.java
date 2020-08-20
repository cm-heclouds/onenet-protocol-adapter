package com.github.cm.heclouds.adapter.mqttadapter.handler;

import com.github.cm.heclouds.adapter.api.ConfigUtils;
import com.github.cm.heclouds.adapter.config.Config;
import com.github.cm.heclouds.adapter.core.consts.CloseReason;
import com.github.cm.heclouds.adapter.core.entity.Device;
import com.github.cm.heclouds.adapter.core.entity.OneJSONRequest;
import com.github.cm.heclouds.adapter.core.entity.Response;
import com.github.cm.heclouds.adapter.core.logging.ILogger;
import com.github.cm.heclouds.adapter.core.utils.DeviceUtils;
import com.github.cm.heclouds.adapter.custom.DeviceDownLinkHandler;
import com.github.cm.heclouds.adapter.entity.ConnectionType;
import com.github.cm.heclouds.adapter.entity.DeviceSession;
import com.github.cm.heclouds.adapter.entity.MessageType;
import com.github.cm.heclouds.adapter.entity.ProxySession;
import com.github.cm.heclouds.adapter.exceptions.InvalidMqttTopicException;
import com.github.cm.heclouds.adapter.exceptions.UnknownMessageTypeException;
import com.github.cm.heclouds.adapter.mqttadapter.ControlSessionManager;
import com.github.cm.heclouds.adapter.mqttadapter.DeviceSessionManager;
import com.github.cm.heclouds.adapter.mqttadapter.ProxySessionManager;
import com.github.cm.heclouds.adapter.mqttadapter.codec.ProtocolMessageUtil;
import com.github.cm.heclouds.adapter.mqttadapter.codec.TopicUtils;
import com.github.cm.heclouds.adapter.mqttadapter.mqtt.MqttSubscription;
import com.github.cm.heclouds.adapter.utils.ConnectSessionNettyUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

import static com.github.cm.heclouds.adapter.core.logging.LoggerFormat.Action.*;
import static io.netty.channel.ChannelFutureListener.CLOSE_ON_FAILURE;


/**
 * 内部消息处理Handler
 */
@ChannelHandler.Sharable
public final class ProtocolMessageHandler extends SimpleChannelInboundHandler<MqttMessage> {

    public static final ProtocolMessageHandler INSTANCE = new ProtocolMessageHandler(ConfigUtils.getConfig());

    private final ILogger logger;

    private static final String SUBSCRIBE_FORMAT = "$gw-proxy/%s/%s/#";

    private final DeviceDownLinkHandler deviceDownLinkHandler;

    private ProtocolMessageHandler(Config config) {
        this.logger = ConfigUtils.getLogger();
        this.deviceDownLinkHandler = config.getDeviceDownLinkHandler();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.logInnerError(ConfigUtils.getName(), RUNTIME, "exceptionCaught cause:", cause);
        ctx.close().addListener(CLOSE_ON_FAILURE);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MqttMessage mqttMessage) {
        ConnectionType connectionType = ConnectSessionNettyUtils.connectionType(channelHandlerContext.channel());
        if (connectionType == ConnectionType.PROXY_CONNECTION) {
            ProxySession proxySession = ConnectSessionNettyUtils.proxySession(channelHandlerContext.channel());
            if (proxySession != null) {
                try {
                    MqttPublishMessage publishMessage = ProtocolMessageUtil.validateMqttMessage(mqttMessage);
                    Pair<String, String> pair = ProtocolMessageUtil.extractDeviceInfoFromTopic(TopicUtils.splitTopic(publishMessage.variableHeader().topicName()));
                    DeviceSession deviceSession = proxySession.getDeviceSession(pair.getKey(), pair.getValue());
                    if (null != deviceSession) {
                        dispatchProxyConnMessage(deviceSession, publishMessage);
                    }
                } catch (Exception e) {
                    logger.logPxyConnWarn(ConfigUtils.getName(), GW_DOWN_LINK, "catch exception:" + e, proxySession.getProxyId());
                }
            }
        } else {
            logger.logInnerWarn(ConfigUtils.getName(), RUNTIME, "unexpected connection type:" + connectionType);
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        ConnectionType connectionType = ConnectSessionNettyUtils.connectionType(ctx.channel());
        switch (connectionType) {
            case CONTROL_CONNECTION:
                ControlSessionManager.handleConnectionLost();
                break;
            case PROXY_CONNECTION:
                ProxySession proxySession = ConnectSessionNettyUtils.proxySession(ctx.channel());
                if (proxySession != null) {
                    ProxySessionManager.handleConnectionLost(proxySession);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 平台下行代理连接消息分发回调
     *
     * @param deviceSession  设备session
     * @param publishMessage MQTT接入机发布消息
     */
    private void dispatchProxyConnMessage(DeviceSession deviceSession, MqttPublishMessage publishMessage) {
        String productId = deviceSession.getProductId();
        String deviceName = deviceSession.getDeviceName();
        boolean login = deviceSession.isLogin();
        String[] tokens = TopicUtils.splitTopic(publishMessage.variableHeader().topicName());
        String event = tokens[3];
        MessageType messageType;
        switch (event) {
            case TopicUtils.LOGIN:
                if (!TopicUtils.validateDownLinkLoginTopic(tokens)) {
                    throw new InvalidMqttTopicException("invalid downlink login topic");
                }
                messageType = MessageType.LOGIN_RESPONSE;
                break;
            case TopicUtils.LOGOUT:
                if (!TopicUtils.validateDownLinkLogoutTopic(tokens)) {
                    throw new InvalidMqttTopicException("invalid downlink logout topic");
                }
                messageType = TopicUtils.getDownLinkLogoutMessageType(tokens);
                break;
            case TopicUtils.THING:
                if (!TopicUtils.validateDownLinkThingTopic(tokens)) {
                    throw new InvalidMqttTopicException("invalid downlink thing topic");
                }
                messageType = TopicUtils.getDownLinkThingMessageType(tokens);
                break;
            default:
                throw new UnknownMessageTypeException("unknown downlink message type:" + event);
        }

        byte[] data = new byte[publishMessage.payload().readableBytes()];
        publishMessage.payload().readBytes(data);
        Device device = Device.newBuilder()
                .productId(productId)
                .deviceName(deviceName)
                .build();
        switch (messageType) {
            case LOGIN_RESPONSE:
                Response response = Response.decode(data);
                if (response.getCode() == 200) {
                    deviceSession.setLogin(true);
                    //登录前先订阅设备的topic
                    List<MqttSubscription> subscriptionList = new ArrayList<>();
                    subscriptionList.add(new MqttSubscription(MqttQoS.AT_MOST_ONCE,
                            String.format(SUBSCRIBE_FORMAT, productId, deviceName)));
                    deviceSession.getProxySession().getMqttClient().subscribe(subscriptionList)
                            .addListener(future ->
                                    deviceDownLinkHandler.onDeviceLoginResponse(device, response));
                } else {
                    if (!ProxySessionManager.isProxiedDevicesReachedLimit(deviceSession, response)) {
                        deviceSession.getProxySession().setDevicesReachedLimit(false);
                        DeviceUtils.setDeviceCloseReason(device, CloseReason.CLOSE_BY_ONENET);
                        deviceDownLinkHandler.onDeviceLoginResponse(device, response);
                    }
                }
                break;
            case LOGOUT_RESPONSE: {
                DeviceUtils.setDeviceCloseReason(device, CloseReason.CLOSE_BY_DEVICE_OFFLINE);
                DeviceSessionManager.handleDeviceOffline(deviceSession);
                deviceDownLinkHandler.onDeviceLogoutResponse(device, Response.decode(data));
                break;
            }
            case LOGOUT_NOTIFY_RESPONSE: {
                if (!login) {
                    logger.logDevWarn(ConfigUtils.getName(), GW_DOWN_LINK, productId, deviceName, "device is received logout_notify message while offline");
                    return;
                }
                Response decode = Response.decode(data);
                DeviceUtils.setDeviceCloseReason(device, CloseReason.CLOSE_BY_ONENET);
                DeviceSessionManager.handleDeviceOffline(deviceSession);
                deviceDownLinkHandler.onDeviceNotifiedLogout(device, decode);
                break;
            }
            case UPLOAD_PROPERTY_RESPONSE:
                if (!login) {
                    logger.logDevWarn(ConfigUtils.getName(), GW_DOWN_LINK, productId, deviceName, "device is received upload_property_response message while offline");
                    return;
                }
                deviceDownLinkHandler.onPropertyUploadResponse(device, Response.decode(data));
                break;
            case UPLOAD_EVENT_RESPONSE: {
                if (!login) {
                    logger.logDevWarn(ConfigUtils.getName(), GW_DOWN_LINK, productId, deviceName, "device is received upload_event_response message while offline");
                    return;
                }
                deviceDownLinkHandler.onEventUploadResponse(device, Response.decode(data));
                break;
            }
            case SET_THING_PROPERTY:
                if (!login) {
                    logger.logDevWarn(ConfigUtils.getName(), GW_DOWN_LINK, productId, deviceName, "device is received set_thing_property message while offline");
                    return;
                }
                deviceDownLinkHandler.onEventUploadResponse(device, Response.decode(data));
                OneJSONRequest request = OneJSONRequest.decode(data);
                deviceDownLinkHandler.onPropertySetRequest(device, request.getId(), request.getVersion(), request.getParams());
                break;
            case GET_DESIRED_RESPONSE:
                if (!login) {
                    logger.logDevWarn(ConfigUtils.getName(), GW_DOWN_LINK, productId, deviceName, "device is received get_desired_response message while offline");
                    return;
                }
                deviceDownLinkHandler.onEventUploadResponse(device, Response.decode(data));
                deviceDownLinkHandler.onDesiredGetResponse(device, Response.decode(data));
                break;
            case DELETE_DESIRED_RESPONSE: {
                if (!login) {
                    logger.logDevWarn(ConfigUtils.getName(), GW_DOWN_LINK, productId, deviceName, "device is received delete_desired_response message while offline");
                    return;
                }
                deviceDownLinkHandler.onEventUploadResponse(device, Response.decode(data));
                deviceDownLinkHandler.onDesiredDeleteResponse(device, Response.decode(data));
                break;
            }
            default:
                logger.logPxyConnWarn(ConfigUtils.getName(), PLATFORM_DOWN_LINK, "unrecognized downlink message type:" + messageType, null);
                break;
        }
    }
}
