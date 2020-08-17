package com.github.cm.heclouds.adapter.mqttadapter;

import com.github.cm.heclouds.adapter.api.ConfigUtils;
import com.github.cm.heclouds.adapter.config.IDeviceConfig;
import com.github.cm.heclouds.adapter.config.impl.ConfigConsts;
import com.github.cm.heclouds.adapter.entity.ConnectionType;
import com.github.cm.heclouds.adapter.entity.DeviceSession;
import com.github.cm.heclouds.adapter.entity.ProxySession;
import com.github.cm.heclouds.adapter.utils.ConnectSessionNettyUtils;
import com.github.cm.heclouds.adapter.utils.SasTokenGenerator;
import com.github.cm.heclouds.adapter.core.entity.Device;
import com.github.cm.heclouds.adapter.core.entity.Response;
import com.github.cm.heclouds.adapter.core.logging.ILogger;
import com.github.cm.heclouds.adapter.mqttadapter.handler.UpLinkChannelHandler;
import com.github.cm.heclouds.adapter.mqttadapter.mqtt.promise.MqttConnectResult;
import io.netty.channel.Channel;
import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import static com.github.cm.heclouds.adapter.core.logging.LoggerFormat.Action.*;

/**
 * 代理连接Session管理类
 */
public final class ProxySessionManager {

    private static final ILogger LOGGER = ConfigUtils.getLogger();
    private static final IDeviceConfig DEVICE_CONFIG = ConfigUtils.getDeviceConfig();

    /**
     * 上一个代理连接创建时间
     */
    private static final AtomicLong LAST_PROXY_CONNECTION_CREATION_TIME = new AtomicLong(0);

    private static final UpLinkChannelHandler UP_LINK_CHANNEL_HANDLER = UpLinkChannelHandler.INSTANCE;

    /**
     * 代理连接Session池
     * 代理连接ID与代理连接Session的映射
     */
    private static final ConcurrentMap<String, ProxySession> PROXY_SESSION_POOL = new ConcurrentHashMap<>();

    public static ConcurrentMap<String, ProxySession> getProxySessionPool() {
        return PROXY_SESSION_POOL;
    }

    /**
     * 创建新的代理连接Session
     * <p>
     * 注意：仅当控制连接有效时才会建立
     *
     * @param proxyId    代理连接ID
     * @param channel    连接channel
     * @param mqttClient Mqtt Client
     * @return 新的代理连接Session
     */
    private static ProxySession createProxySession(String proxyId, Channel channel, MqttClient mqttClient) {
        ProxySession proxySession = ProxySession.newBuilder()
                .mqttClient(mqttClient)
                .proxyId(proxyId)
                .channel(channel)
                .proxyDevAssociation(new ConcurrentHashMap<>())
                .connected(true)
                .isDevicesReachedLimit(false)
                .build();
        ConnectSessionNettyUtils.setConnectionType(channel, ConnectionType.PROXY_CONNECTION);
        ConnectSessionNettyUtils.setProxySession(channel, proxySession);
        return proxySession;
    }

    /**
     * 选择代理连接Session，用于新设备选择合适的代理连接，默认为选择当前代理设备最少的代理连接
     *
     * @return 代理连接Session
     */
    public static ProxySession chooseProxySession() {
        if (ControlSessionManager.config == null) {
            LOGGER.logInnerWarn(ConfigUtils.getName(), RUNTIME, "choose proxy session failed as control session was not initialized");
            return null;
        }
        return chooseMinimumDeviceProxySession()
                .orElseGet(ProxySessionManager::initNewProxyConnection);
    }

    /**
     * 连接断开情况处理
     */
    public static void handleConnectionLost(ProxySession proxySession) {
        LOGGER.logPxyConnWarn(ConfigUtils.getName(), DISCONNECT, null, proxySession.getProxyId());
        Iterator<Map.Entry<Pair<String, String>, DeviceSession>> iterator = proxySession.getProxyDevAssociation().entrySet().iterator();
        // 所有代理设备下线
        while (iterator.hasNext()) {
            Map.Entry<Pair<String, String>, DeviceSession> entry = iterator.next();
            DeviceSession deviceSession = entry.getValue();
            deviceSession.setCloseReason("proxy connection is disconnected");
            DeviceSessionManager.handleDeviceOffline(deviceSession);
            // 主动通知设备下线
            ConfigUtils.getConfig().getDeviceDownLinkHandler()
                    .onDeviceNotifiedLogout(Device.newBuilder()
                            .productId(deviceSession.getProductId())
                            .deviceName(deviceSession.getDeviceName()).build(), new Response(null, 1061, "disconnected proxy connection"));
            iterator.remove();
        }
        removeProxySession(proxySession.getProxyId());
    }

    /**
     * 代理连接代理的设备数量是否超过限制
     *
     * @param deviceSession deviceSession
     * @param response      登录响应
     * @return 是否超过限制
     */
    public static boolean isProxiedDevicesReachedLimit(DeviceSession deviceSession, Response response) {
        ProxySession proxySession = deviceSession.getProxySession();
        if (response.getCode() == 0x8B) {
            proxySession.setDevicesReachedLimit(true);
            deviceSession.setProxySession(null);
            Device deviceEntity = DEVICE_CONFIG.getDeviceEntity(DEVICE_CONFIG.getOriginalIdentity(deviceSession.getProductId(), deviceSession.getDeviceName()));
            // 重新选择代理连接登录
            UP_LINK_CHANNEL_HANDLER.doDeviceOnline(deviceEntity);
            return true;
        }
        return false;
    }

    /**
     * 放入代理连接Session
     *
     * @param proxySession 代理连接Session
     */
    private static void putProxySession(ProxySession proxySession) {
        PROXY_SESSION_POOL.put(proxySession.getProxyId(), proxySession);
    }

    /**
     * 从Session池中移除
     *
     * @param proxyId 代理连接id
     */
    private static void removeProxySession(String proxyId) {
        PROXY_SESSION_POOL.remove(proxyId);
    }

    /**
     * 当前代理设备最少的代理连接
     *
     * @return 当前代理设备最少的代理连接
     */
    private static Optional<ProxySession> chooseMinimumDeviceProxySession() {
        return PROXY_SESSION_POOL.values().stream()
                .filter(proxySession -> !proxySession.isDevicesReachedLimit())
                .min(Comparator.comparingInt(ProxySession::size));
    }

    /**
     * 初始化代理连接，当控制连接不存在或者已经断开时不会进行初始化
     *
     * @return 代理连接Session
     */
    private static ProxySession initNewProxyConnection() {
        if (!ControlSessionManager.isControlSessionActive()) {
            LOGGER.logPxyConnWarn(ConfigUtils.getName(), INIT, "init new proxy connection failed as control session is inactive", null);
            return null;
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - LAST_PROXY_CONNECTION_CREATION_TIME.getAndSet(currentTimeMillis) < ConfigConsts.MAX_PROXY_CONNECTION_CREATION_INTERVAL_MS) {
            LOGGER.logPxyConnWarn(ConfigUtils.getName(), INIT, "init new proxy connection failed as proxy connections create too fast", null);
            return null;
        }
        String clientId = genClientId();
        if (PROXY_SESSION_POOL.containsKey(clientId)) {
            LOGGER.logPxyConnWarn(ConfigUtils.getName(), INIT, "existed proxy connection", clientId);
            return null;
        }
        MqttClient mqttClient = new MqttClient(ConfigUtils.getConfig());
        MqttConnectResult result;
        try {
            String serviceId = ControlSessionManager.config.getServiceId();
            String sasToken = SasTokenGenerator.adapterSasToken(ControlSessionManager.config);
            if (sasToken == null) {
                LOGGER.logPxyConnWarn(ConfigUtils.getName(), INIT, "init new proxy connection failed due to generate sasToken failed", clientId);
                return null;
            }
            result = mqttClient.connect(clientId, serviceId, sasToken);
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.logPxyConnWarn(ConfigUtils.getName(), INIT, "initialize mqtt client failed whiling choose proxy session due to " + e.getLocalizedMessage(), clientId);
            Thread.currentThread().interrupt();
            return null;
        }
        ProxySession proxySession = null;
        switch (result.returnCode()) {
            case CONNECTION_ACCEPTED:
                proxySession = createProxySession(clientId, mqttClient.getChannel(), mqttClient);
                putProxySession(proxySession);
                LOGGER.logPxyConnInfo(ConfigUtils.getName(), INIT, "init new proxy connection succeed", clientId);
                break;
            case CONNECTION_REFUSED_NOT_AUTHORIZED:
                LOGGER.logPxyConnWarn(ConfigUtils.getName(), INIT, "init new proxy connection failed due to proxy connections reached limit", clientId);
                break;
            case CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD:
            case CONNECTION_REFUSED_SERVER_UNAVAILABLE:
            default:
                LOGGER.logPxyConnWarn(ConfigUtils.getName(), INIT, "init new proxy connection failed due to " + result.returnCode().toString(), clientId);
                break;
        }
        return proxySession;
    }

    /**
     * 生成用于建立代理连接的clientID
     *
     * @return clientID
     */
    private static String genClientId() {
        if (ControlSessionManager.config == null) {
            throw new IllegalStateException("control session was not initialized");
        }
        return ControlSessionManager.config.getInstanceName() + "/" + UUID.randomUUID().toString();
    }
}
