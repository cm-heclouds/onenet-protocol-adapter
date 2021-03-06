package com.github.cm.heclouds.adapter.mqttadapter;

import com.github.cm.heclouds.adapter.ProtocolAdapterService;
import com.github.cm.heclouds.adapter.api.ConfigUtils;
import com.github.cm.heclouds.adapter.config.Config;
import com.github.cm.heclouds.adapter.entity.ConnectionType;
import com.github.cm.heclouds.adapter.entity.ControlSession;
import com.github.cm.heclouds.adapter.utils.ConnectSessionNettyUtils;
import com.github.cm.heclouds.adapter.core.logging.ILogger;
import io.netty.channel.Channel;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.github.cm.heclouds.adapter.core.logging.LoggerFormat.Action.DISCONNECT;
import static com.github.cm.heclouds.adapter.core.logging.LoggerFormat.Action.RUNTIME;


/**
 * 控制连接Session管理类，相同的泛协议接入服务实例(服务ID+实例名称)仅建立一条控制连接
 */
public final class ControlSessionManager {

    public static Config config = null;
    public static ILogger logger = null;

    private static ControlSession controlSession = null;
    private static boolean isCtrlReconnect = false;

    private static AtomicLong ctrlReconnectInterval;
    private static AtomicInteger ctrlReconnectCount;
    private static int backoffReachTimes;
    private static int backoffExp;

    private static volatile boolean isConnected;

    private ControlSessionManager() {
    }

    public static ControlSession getSession() {
        return controlSession;
    }

    /**
     * 连接断开情况处理
     */
    public static void handleConnectionLost() {
        ControlSessionManager.isConnected = false;
        logger.logCtrlConnWarn(ConfigUtils.getName(), DISCONNECT, null, null, null);
        reconnectControlSession();
    }

    public static boolean isConnected() {
        return isConnected;
    }

    /**
     * 判断控制连接是否存活
     *
     * @return 是否存活
     */
    static boolean isControlSessionActive() {
        return ControlSessionManager.controlSession != null &&
                ControlSessionManager.isConnected &&
                ControlSessionManager.controlSession.getChannel().isActive();
    }

    /**
     * 初始化控制连接
     *
     * @param config  泛协议接入服务实例配置
     * @param channel 控制连接channel
     */
    public static void initControlSession(Config config, Channel channel) {
        if (ControlSessionManager.config != null) {
            throw new IllegalStateException("duplicated initiation of control session");
        }
        ControlSessionManager.config = config;
        logger = ConfigUtils.getLogger();
        isCtrlReconnect = config.getCtrlReconnect();
        ctrlReconnectCount = new AtomicInteger(0);
        ctrlReconnectInterval = new AtomicLong(config.getCtrlReconnectInterval());
        backoffReachTimes = config.getBackoffReachTimes();
        backoffExp = config.getBackoffExp();
        controlSession = ControlSession.newBuilder()
                .instanceName(config.getInstanceName())
                .serviceId(config.getServiceId())
                .channel(channel)
                .build();
        isConnected = true;
        ConnectSessionNettyUtils.setConnectionType(channel, ConnectionType.CONTROL_CONNECTION);
    }

    /**
     * 重新建立控制连接
     */
    private static void reconnectControlSession() {
        if (isCtrlReconnect) {
            if (ctrlReconnectCount.incrementAndGet() == backoffReachTimes + 1) {
                ctrlReconnectCount.set(1);
                ctrlReconnectInterval.set(ctrlReconnectInterval.get() * backoffExp);
                logger.logInnerWarn(ConfigUtils.getName(), RUNTIME, "ctrl reconnect failed after retry 2 times, increased ctrlReconnectInterval time to " + ctrlReconnectInterval.get() + "  seconds  ");
            }
            try {
                long reconnectInterval = ctrlReconnectInterval.get();
                logger.logInnerWarn(ConfigUtils.getName(), RUNTIME, "prepare to reconnect ctrl after " + reconnectInterval + "s");
                TimeUnit.SECONDS.sleep(reconnectInterval);
                Channel channel = ProtocolAdapterService.initControlConnection(config, false);
                isConnected = true;
                controlSession.setChannel(channel);
                ctrlReconnectCount.set(0);
                ctrlReconnectInterval = new AtomicLong(config.getCtrlReconnectInterval());
                ConnectSessionNettyUtils.setConnectionType(channel, ConnectionType.CONTROL_CONNECTION);
                logger.logInnerInfo(ConfigUtils.getName(), RUNTIME, "ctrl reconnected");
            } catch (Exception e) {
                logger.logInnerError(ConfigUtils.getName(), RUNTIME, "ctrl reconnect failed", e);
                reconnectControlSession();
            }
        }
    }
}
