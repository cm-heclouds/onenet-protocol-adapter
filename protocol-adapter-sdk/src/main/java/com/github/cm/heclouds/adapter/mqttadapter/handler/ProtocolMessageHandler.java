package com.github.cm.heclouds.adapter.mqttadapter.handler;

import com.github.cm.heclouds.adapter.api.ConfigUtils;
import com.github.cm.heclouds.adapter.config.Config;
import com.github.cm.heclouds.adapter.core.consts.CloseReason;
import com.github.cm.heclouds.adapter.core.entity.*;
import com.github.cm.heclouds.adapter.core.logging.ILogger;
import com.github.cm.heclouds.adapter.core.utils.DeviceUtils;
import com.github.cm.heclouds.adapter.entity.DevicePromise;
import com.github.cm.heclouds.adapter.entity.request.GetDesiredRequest;
import com.github.cm.heclouds.adapter.entity.response.GetTopoResponse;
import com.github.cm.heclouds.adapter.entity.response.GetTopoResult;
import com.github.cm.heclouds.adapter.entity.sdk.ConnectionType;
import com.github.cm.heclouds.adapter.entity.sdk.DeviceSession;
import com.github.cm.heclouds.adapter.entity.sdk.MessageType;
import com.github.cm.heclouds.adapter.entity.sdk.ProxySession;
import com.github.cm.heclouds.adapter.exceptions.UnknownMessageTypeException;
import com.github.cm.heclouds.adapter.handler.DownLinkRequestHandler;
import com.github.cm.heclouds.adapter.handler.subdev.SubDeviceDownLinkRequestHandler;
import com.github.cm.heclouds.adapter.mqttadapter.ControlSessionManager;
import com.github.cm.heclouds.adapter.mqttadapter.DeviceSessionManager;
import com.github.cm.heclouds.adapter.mqttadapter.ProxySessionManager;
import com.github.cm.heclouds.adapter.mqttadapter.mqtt.MqttSubscription;
import com.github.cm.heclouds.adapter.mqttadapter.mqtt.promise.PromiseBreaker;
import com.github.cm.heclouds.adapter.utils.ConnectSessionNettyUtils;
import com.github.cm.heclouds.adapter.utils.ExpiryMap;
import com.github.cm.heclouds.adapter.utils.ProtocolMessageUtils;
import com.github.cm.heclouds.adapter.utils.TopicUtils;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.ReferenceCountUtil;
import javafx.util.Pair;

import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.cm.heclouds.adapter.core.logging.LoggerFormat.Action.GW_DOWN_LINK;
import static com.github.cm.heclouds.adapter.core.logging.LoggerFormat.Action.RUNTIME;
import static io.netty.channel.ChannelFutureListener.CLOSE_ON_FAILURE;


/**
 * 内部消息处理Handler
 */
public final class ProtocolMessageHandler extends ChannelDuplexHandler {

    private final ILogger logger;

    private static final String SUBSCRIBE_FORMAT = "$gw-proxy/%s/%s/#";

    /**
     * 设备上线Promise映射  pid-devName -> promise
     */
    private static final Map<String, AtomicReference<DevicePromise<DeviceResult>>> DEVICE_ONLINE_PROMISE_MAP = new ExpiryMap<>();
    /**
     * 设备下线Promise映射  pid-devName -> promise
     */
    private static final Map<String, AtomicReference<DevicePromise<DeviceResult>>> DEVICE_OFFLINE_PROMISE_MAP = new ExpiryMap<>();
    /**
     * 设备上行Promise映射 packedId -> promise
     */
    private static final Map<String, DevicePromise<DeviceResult>> DEVICE_UPLOAD_PROMISE_MAP = new ExpiryMap<>();
    /**
     * 获取网关子设备拓扑Promise映射 packedId -> promise
     */
    private static final Map<String, DevicePromise<GetTopoResult>> GET_TOPO_PROMISE_MAP = new ExpiryMap<>();

    private final DownLinkRequestHandler downLinkRequestHandler;

    private final SubDeviceDownLinkRequestHandler subDeviceDownLinkRequestHandler;

    public ProtocolMessageHandler(Config config) {
        this.logger = ConfigUtils.getLogger();
        this.downLinkRequestHandler = config.getDownLinkRequestHandler();
        this.subDeviceDownLinkRequestHandler = config.getSubDeviceDownLinkRequestHandler();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.logInnerError(ConfigUtils.getName(), RUNTIME, "exceptionCaught cause:", cause);
        ctx.close().addListener(CLOSE_ON_FAILURE);
    }


    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        MqttMessage mqttMessage = (MqttMessage) msg;
        try {
            ConnectionType connectionType = ConnectSessionNettyUtils.connectionType(channelHandlerContext.channel());
            if (connectionType == ConnectionType.PROXY_CONNECTION) {
                ProxySession proxySession = ConnectSessionNettyUtils.proxySession(channelHandlerContext.channel());
                if (proxySession != null) {
                    try {
                        MqttPublishMessage publishMessage = ProtocolMessageUtils.validateMqttMessage(mqttMessage);
                        Pair<String, String> pair = ProtocolMessageUtils.extractDeviceInfoFromTopic(TopicUtils.splitTopic(publishMessage.variableHeader().topicName()));
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
        } finally {
            ReferenceCountUtil.release(mqttMessage);
        }
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise channelPromise) throws Exception {
        if (msg instanceof DevicePromise<?>) {
            MqttMessage mqttMessage;
            if (((DevicePromise<?>) msg).getMessageType().equals(MessageType.GET_TOPO_REQUEST)) {
                DevicePromise<GetTopoResult> promise = (DevicePromise<GetTopoResult>) msg;
                mqttMessage = promise.getMqttMessage();
                GET_TOPO_PROMISE_MAP.put(promise.getPacketId(), promise);
                super.write(ctx, mqttMessage, channelPromise);
            } else {
                DevicePromise<DeviceResult> promise = (DevicePromise<DeviceResult>) msg;
                mqttMessage = promise.getMqttMessage();
                String productId = promise.getProductId();
                String deviceName = promise.getDeviceName();
                switch (((DevicePromise<?>) msg).getMessageType()) {
                    case LOGIN_REQUEST:
                        AtomicReference<DevicePromise<DeviceResult>> reference = new AtomicReference<>();
                        reference.set(promise);
                        AtomicReference<DevicePromise<DeviceResult>> previous = DEVICE_ONLINE_PROMISE_MAP.putIfAbsent(productId + "-" + deviceName, reference);
                        if (previous != null) {
                            promise.trySuccess(new DeviceResult(ReturnCode.DUP_LOGIN));
                            ReferenceCountUtil.release(mqttMessage);
                        } else {
                            super.write(ctx, mqttMessage, channelPromise);
                        }
                        break;
                    case LOGOUT_REQUEST:
                        reference = new AtomicReference<>();
                        reference.set(promise);
                        previous = DEVICE_OFFLINE_PROMISE_MAP.putIfAbsent(productId + "-" + deviceName, reference);
                        if (previous != null) {
                            promise.trySuccess(new DeviceResult(ReturnCode.SUCCESS));
                            ReferenceCountUtil.release(mqttMessage);
                        } else {
                            super.write(ctx, mqttMessage, channelPromise);
                        }
                        break;
                    case UPLOAD_REQUEST:
                    case COMMON_REQUEST:
                        DEVICE_UPLOAD_PROMISE_MAP.put(promise.getPacketId(), promise);
                        super.write(ctx, mqttMessage, channelPromise);
                        break;
                    default:
                        super.write(ctx, mqttMessage, channelPromise);
                        break;
                }
            }
        } else {
            super.write(ctx, msg, channelPromise);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        PromiseBreaker promiseBreaker = new PromiseBreaker(new ClosedChannelException());
        for (Map.Entry<String, AtomicReference<DevicePromise<DeviceResult>>> entry : DEVICE_ONLINE_PROMISE_MAP.entrySet()) {
            promiseBreaker.renege(entry.getValue().getAndSet(null));
        }
        for (Map.Entry<String, AtomicReference<DevicePromise<DeviceResult>>> entry : DEVICE_OFFLINE_PROMISE_MAP.entrySet()) {
            promiseBreaker.renege(entry.getValue().getAndSet(null));
        }
        promiseBreaker.renege(DEVICE_UPLOAD_PROMISE_MAP.values());
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
                messageType = MessageType.LOGIN_RESPONSE;
                break;
            case TopicUtils.LOGOUT:
                messageType = TopicUtils.getDownLinkLogoutMessageType(tokens);
                break;
            case TopicUtils.THING:
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
                AtomicReference<DevicePromise<DeviceResult>> reference = DEVICE_ONLINE_PROMISE_MAP.remove(productId + "-" + deviceName);
                if (reference == null) {
                    logger.logDevWarn(ConfigUtils.getName(), GW_DOWN_LINK, productId, deviceName, "device online failed: online result is null");
                    return;
                }
                DevicePromise<DeviceResult> promise = reference.getAndSet(null);
                Response response = Response.decode(data);
                if (response.getCode() == ReturnCode.SUCCESS.getCode()) {
                    deviceSession.setLogin(true);
                    //登录前先订阅设备的topic
                    List<MqttSubscription> subscriptionList = new ArrayList<>();
                    subscriptionList.add(new MqttSubscription(MqttQoS.AT_MOST_ONCE,
                            String.format(SUBSCRIBE_FORMAT, productId, deviceName)));
                    deviceSession.getProxySession().getMqttClient().subscribe(subscriptionList)
                            .addListener(future ->
                                    promise.trySuccess(new DeviceResult(response)));
                } else if (response.getCode() == ReturnCode.DUP_LOGIN.getCode()) {
                    logger.logDevWarn(ConfigUtils.getName(), GW_DOWN_LINK, productId, deviceName, "duplicated login");
                    downLinkRequestHandler.onDeviceNotifiedLogout(device, response);
                    deviceSession.setLogin(true);
                    response.setCode(ReturnCode.SUCCESS.getCode());
                    response.setMsg(ReturnCode.SUCCESS.getMsg());
                    promise.trySuccess(new DeviceResult(response));
                } else {
                    if (!ProxySessionManager.isProxiedDevicesReachedLimit(deviceSession, response)) {
                        deviceSession.getProxySession().setDevicesReachedLimit(false);
                        DeviceUtils.setDeviceCloseReason(device, CloseReason.CLOSE_BY_ONENET);
                        promise.trySuccess(new DeviceResult(response));
                    }
                }
                break;
            case LOGOUT_RESPONSE: {
                reference = DEVICE_OFFLINE_PROMISE_MAP.remove(productId + "-" + deviceName);
                if (reference == null) {
                    logger.logDevWarn(ConfigUtils.getName(), GW_DOWN_LINK, productId, deviceName, "device online failed: online result is null");
                    return;
                }
                promise = reference.getAndSet(null);
                response = Response.decode(data);
                DeviceUtils.setDeviceCloseReason(device, CloseReason.CLOSE_BY_DEVICE_OFFLINE);
                DeviceSessionManager.handleDeviceOffline(deviceSession);
                promise.trySuccess(new DeviceResult(response));
                break;
            }
            case LOGOUT_NOTIFY: {
                if (!login) {
                    logger.logDevWarn(ConfigUtils.getName(), GW_DOWN_LINK, productId, deviceName, "device is received logout_notify message while offline");
                    return;
                }
                response = Response.decode(data);
                DeviceUtils.setDeviceCloseReason(device, CloseReason.CLOSE_BY_ONENET);
                DeviceSessionManager.handleDeviceOffline(deviceSession);
                downLinkRequestHandler.onDeviceNotifiedLogout(device, response);
                break;
            }
            case SET_THING_PROPERTY_REQUEST:
                if (!login) {
                    logger.logDevWarn(ConfigUtils.getName(), GW_DOWN_LINK, productId, deviceName, "device is received set_thing_property message while offline");
                }
                OneJSONRequest request = OneJSONRequest.decode(data);
                downLinkRequestHandler.onSetPropertyRequest(device, request.getId(), request.getVersion(), request.getParams());
                break;
            case GET_PROPERTY_REQUEST:
                if (!login) {
                    logger.logDevWarn(ConfigUtils.getName(), GW_DOWN_LINK, productId, deviceName, "device is received get_thing_property message while offline");
                }
                GetDesiredRequest getPropertyRequest = GetDesiredRequest.decode(data);
                downLinkRequestHandler.onGetPropertyRequest(device, getPropertyRequest.getId(), getPropertyRequest.getVersion(), getPropertyRequest.getParams());
                break;
            case INVOKE_SERVICE_REQUEST:
                if (!login) {
                    logger.logDevWarn(ConfigUtils.getName(), GW_DOWN_LINK, productId, deviceName, "device is received invoke_thing_service message while offline");
                }
                request = OneJSONRequest.decode(data);
                downLinkRequestHandler.onInvokeServiceRequest(device, tokens[5], request.getId(), request.getVersion(), request.getParams());
                break;
            case SUB_DEV_TOPO_CHANGE: {
                if (!login) {
                    logger.logDevWarn(ConfigUtils.getName(), GW_DOWN_LINK, productId, deviceName, "device is received subdevice_login message while offline");
                }
                request = OneJSONRequest.decode(data);
                subDeviceDownLinkRequestHandler.onSubDeviceTopoChangeRequest(device, request.getId(), request.getVersion(), request.getParams());
                break;
            }
            case SUB_SET_THING_PROPERTY_REQUEST: {
                if (!login) {
                    logger.logDevWarn(ConfigUtils.getName(), GW_DOWN_LINK, productId, deviceName, "device is received subdevice_set_thing_property message while offline");
                }
                request = OneJSONRequest.decode(data);
                subDeviceDownLinkRequestHandler.onSetSubDevicePropertyRequest(device, request.getId(), request.getVersion(), request.getParams());
                break;
            }
            case SUB_GET_PROPERTY_REQUEST: {
                if (!login) {
                    logger.logDevWarn(ConfigUtils.getName(), GW_DOWN_LINK, productId, deviceName, "device is received subdevice_get_thing_property message while offline");
                }
                request = OneJSONRequest.decode(data);
                subDeviceDownLinkRequestHandler.onGetSubDevicePropertyRequest(device, request.getId(), request.getVersion(), request.getParams());
                break;
            }
            case SUB_INVOKE_SERVICE_REQUEST: {
                if (!login) {
                    logger.logDevWarn(ConfigUtils.getName(), GW_DOWN_LINK, productId, deviceName, "device is received subdevice_invoke_thing_service message while offline");
                }
                request = OneJSONRequest.decode(data);
                subDeviceDownLinkRequestHandler.onInvokeSubDeviceServiceRequest(device, request.getId(), request.getVersion(), request.getParams());
                break;
            }
            case SUB_DEV_TOPO_GET_RESPONSE:
                GetTopoResponse getTopoResponse = GetTopoResponse.decode(data);
                DevicePromise<GetTopoResult> getTopoResultDevicePromise = GET_TOPO_PROMISE_MAP.remove(getTopoResponse.getId());
                if (getTopoResultDevicePromise != null) {
                    getTopoResultDevicePromise.trySuccess(new GetTopoResult(getTopoResponse));
                } else {
                    logger.logDevWarn(ConfigUtils.getName(), GW_DOWN_LINK, productId, deviceName, String.format("device is received unexpected data, messageType=%s, data=%s", messageType, getTopoResponse));
                    return;
                }
                break;
            case UPLOAD_PROPERTY_RESPONSE:
            case UPLOAD_EVENT_RESPONSE:
            case GET_DESIRED_RESPONSE:
            case DELETE_DESIRED_RESPONSE:
            case UPLOAD_HISTORY_DATA_RESPONSE:
            case SUB_DEV_TOPO_ADD_RESPONSE:
            case SUB_DEV_TOPO_DELETE_RESPONSE:
            case SUB_LOGIN_RESPONSE:
            case SUB_LOGOUT_RESPONSE:
            default:
                response = Response.decode(data);
                promise = DEVICE_UPLOAD_PROMISE_MAP.remove(response.getId());
                if (promise != null) {
                    promise.trySuccess(new DeviceResult(response));
                } else {
                    logger.logDevWarn(ConfigUtils.getName(), GW_DOWN_LINK, productId, deviceName, String.format("device is received unexpected data, messageType=%s, data=%s", messageType, response));
                    return;
                }
                break;
        }
    }
}
