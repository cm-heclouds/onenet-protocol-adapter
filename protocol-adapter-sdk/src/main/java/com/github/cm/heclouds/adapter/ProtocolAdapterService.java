package com.github.cm.heclouds.adapter;

import com.github.cm.heclouds.adapter.api.ConfigUtils;
import com.github.cm.heclouds.adapter.config.Config;
import com.github.cm.heclouds.adapter.core.logging.ILogger;
import com.github.cm.heclouds.adapter.entity.sdk.ConnectionType;
import com.github.cm.heclouds.adapter.exceptions.IllegalConfigException;
import com.github.cm.heclouds.adapter.extensions.metric.Metric;
import com.github.cm.heclouds.adapter.mqttadapter.ControlSessionManager;
import com.github.cm.heclouds.adapter.mqttadapter.MqttClient;
import com.github.cm.heclouds.adapter.mqttadapter.ProxySessionManager;
import com.github.cm.heclouds.adapter.mqttadapter.mqtt.promise.MqttConnectResult;
import com.github.cm.heclouds.adapter.utils.ConnectSessionNettyUtils;
import com.github.cm.heclouds.adapter.utils.SasTokenGenerator;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.util.internal.StringUtil;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static com.github.cm.heclouds.adapter.core.logging.LoggerFormat.Action.INIT;
import static com.github.cm.heclouds.adapter.core.logging.LoggerFormat.Action.LAUNCH;

/**
 * 泛协议接入SDK服务
 */
public final class ProtocolAdapterService {

    private static ILogger logger;
    private final Metric metric;
    private final Config config;
    private final String instanceName;
    private final String serviceId;

    public ProtocolAdapterService(Config config) {
        this.config = config;
        this.instanceName = config.getInstanceName();
        this.serviceId = config.getServiceId();
        this.metric = Metric.INSTANCE;
        logger = ConfigUtils.getLogger();
    }

    /**
     * 初始化控制连接
     *
     * @return 控制连接channel
     * @throws Exception exception
     */
    public static Channel initControlConnection(Config config, boolean isInit) throws Exception {
        // 实例启动时建立控制连接
        if (StringUtil.isNullOrEmpty(config.getInstanceName())) {
            throw new IllegalConfigException("config \"instanceName\" must be present");
        }
        if (isInit && ControlSessionManager.config != null) {
            throw new IllegalStateException("duplicated initiation of control session");
        }
        ControlSessionManager.config = config;
        ControlSessionManager.logger = ConfigUtils.getLogger();
        MqttClient mqttClient = new MqttClient(config);
        try{
            ConnectSessionNettyUtils.setConnectionType(mqttClient.getChannel(), ConnectionType.CONTROL_CONNECTION);
            String sasToken = SasTokenGenerator.adapterSasToken(config);
            String serviceId = config.getServiceId();
            String instanceName = config.getInstanceName();
            MqttConnectResult result = mqttClient.connect(instanceName, serviceId, sasToken);
            if (result.returnCode() != MqttConnectReturnCode.CONNECTION_ACCEPTED) {
                if (isInit) {
                    logger.logCtrlConnError(ConfigUtils.getName(), INIT, serviceId, instanceName, "failed, error: " + result.returnCode().toString(), null);
                    System.exit(0);
                } else {
                    mqttClient.getChannel().close();
                    throw new Exception("ctrl connect failed, error: " + result.returnCode().toString());
                }
            }
            return mqttClient.getChannel();
        }catch (Exception e){
            mqttClient.getChannel().close();
            throw e;
        }
    }

    /**
     * 启动泛协议接入SDK
     */
    public void start() {
        try {
            long beginTime = System.currentTimeMillis();
            logger.logInnerInfo(ConfigUtils.getName(), LAUNCH, serviceId, instanceName, "starting protocol adapter sdk");
            logger.logInnerInfo(ConfigUtils.getName(), LAUNCH, config.toString());

            Channel channel = initControlConnection(config, true);
            ControlSessionManager.initControlSession(config, channel);

            ProxySessionManager.initProxySessions();

            BigDecimal b = new BigDecimal((System.currentTimeMillis() - beginTime) / 1000);
            double time = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            logger.logInnerInfo(ConfigUtils.getName(), LAUNCH, "started protocol adapter sdk in " + time + " seconds");
            if (config.getEnableMetrics()) {
                // 启动统计
                metric.start(60, TimeUnit.SECONDS);
            }
        } catch (Exception e) {
            logger.logCtrlConnError(ConfigUtils.getName(), INIT, serviceId, instanceName, "failed", e);
            System.exit(1);
        }
    }

}
