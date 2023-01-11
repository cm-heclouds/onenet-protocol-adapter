package com.github.cm.heclouds.adapter.config;

import com.github.cm.heclouds.adapter.ProtocolAdapterService;
import com.github.cm.heclouds.adapter.api.ConfigUtils;
import com.github.cm.heclouds.adapter.config.impl.AdapterFileConfig;
import com.github.cm.heclouds.adapter.config.impl.DeviceFileConfig;
import com.github.cm.heclouds.adapter.core.config.CoreConfig;
import com.github.cm.heclouds.adapter.core.logging.ILogger;
import com.github.cm.heclouds.adapter.core.logging.LoggerFormat;
import com.github.cm.heclouds.adapter.core.utils.CoreConfigUtils;
import com.github.cm.heclouds.adapter.handler.DefaultDeviceDownLinkHandler;
import com.github.cm.heclouds.adapter.handler.DownLinkRequestHandler;
import com.github.cm.heclouds.adapter.handler.subdev.DefaultSubDeviceDownLinkRequestHandler;
import com.github.cm.heclouds.adapter.handler.subdev.SubDeviceDownLinkRequestHandler;
import com.github.cm.heclouds.adapter.utils.InetSocketAddressUtils;
import io.netty.util.internal.StringUtil;

import java.net.InetSocketAddress;

import static com.github.cm.heclouds.adapter.config.impl.ConfigConsts.*;

/**
 * SDK配置类
 */
public class Config {

    /**
     * SDK默认名称
     */
    public static final String DEFAULT_NAME = "SDK";
    /**
     * 默认SDK和平台接入机之间的控制连接异常断开后的初始重连等待时间
     */
    private static final Long DEFAULT_CTRL_RECONNECT_INTERVAL = 30L;

    /**
     * 默认平台接入机连接地址，非加密
     */
    private static final String DEFAULT_CONNECTION_HOST_NO_TLS = "studio-mqtt.heclouds.com:1883";
    /**
     * 默认平台接入机连接地址，加密
     */
    private static final String DEFAULT_CONNECTION_HOST_TLS = "studio-mqtt.heclouds.com:8883";
    /**
     * 默认SDK和平台接入机之间不使用加密传输
     */
    private static final Boolean DEFAULT_ENABLE_TLS = Boolean.FALSE;
    /**
     * 默认SDK打印统计日志
     */
    private static final Boolean DEFAULT_ENABLE_METRICS = Boolean.TRUE;
    /**
     * 默认SDK和平台接入机之间的控制连接断开后不重连
     */
    private static final Boolean DEFAULT_ENABLE_CTRL_RECONNECT = Boolean.FALSE;
    /**
     * 默认指数退避触发条件
     */
    private static final Integer DEFAULT_BACKOFF_REACH_TIMES = 2;
    /**
     * 默认退避倍数
     */
    private static final Integer DEFAULT_BACKOFF_EXP = 4;
    /**
     * 日志
     */
    private final ILogger logger;
    /**
     * SDK协议站名称，非必填
     */
    private String name = null;
    /**
     * 平台接入机连接地址，非必填
     */
    private String connectionHost = null;
    /**
     * 泛协议接入服务ID，必填
     */
    private String serviceId = null;
    /**
     * 泛协议接入服务实例名，必填
     */
    private String instanceName = null;
    /**
     * 泛协议接入服务实例Key，必填
     */
    private String instanceKey = null;
    /**
     * 和平台接入机之间是否使用加密传输，默认值为false
     */
    private Boolean enableTls = null;
    /**
     * 是否打印统计日志，默认值为true
     */
    private Boolean enableMetrics = null;
    /**
     * 和平台接入机之间的控制连接异常断开后的初始重连等待时间，默认值为30秒
     */
    private Boolean enableCtrlReconnect = null;
    /**
     * 和平台接入机之间的控制连接断开后重连次数，非必填，默认值为3
     */
    private Long ctrlReconnectInterval = null;
    /**
     * 指数退避触发条件，默认2
     */
    private Integer backoffReachTimes = null;
    /**
     * 退避倍数，默认4
     */
    private Integer backoffExp = null;
    /**
     * 设备配置类，非必填，默认为{@link DeviceFileConfig}
     */
    private IDeviceConfig deviceConfig = null;
    /**
     * 平台下行请求数据处理，必填
     */
    private DownLinkRequestHandler downLinkRequestHandler = null;
    /**
     * 平台子设备下行请求数据处理，必填
     */
    private SubDeviceDownLinkRequestHandler subDeviceDownLinkRequestHandler = null;

    public Config(ILogger logger) {
        this.logger = logger;
        if (CoreConfigUtils.getCoreConfig() == null) {
            CoreConfigUtils.setCoreConfig(new CoreConfig(this.logger));
        }
        ConfigUtils.setConfig(this);
    }

    /**
     * 初始化配置
     */
    public void init() {
        if (StringUtil.isNullOrEmpty(name)) {
            name = AdapterFileConfig.getInstance().getName();
        }
        if (StringUtil.isNullOrEmpty(connectionHost)) {
            connectionHost = AdapterFileConfig.getInstance().getConnectionHost();
        }
        if (StringUtil.isNullOrEmpty(serviceId)) {
            serviceId = AdapterFileConfig.getInstance().getServiceId();
        }
        if (StringUtil.isNullOrEmpty(instanceName)) {
            instanceName = AdapterFileConfig.getInstance().getInstanceName();
        }
        if (StringUtil.isNullOrEmpty(instanceKey)) {
            instanceKey = AdapterFileConfig.getInstance().getInstanceKey();
        }
        if (enableTls == null) {
            enableTls = AdapterFileConfig.getInstance().enableTls();
        }
        if (enableMetrics == null) {
            enableMetrics = AdapterFileConfig.getInstance().enableMetrics();
        }
        if (enableCtrlReconnect == null) {
            enableCtrlReconnect = AdapterFileConfig.getInstance().enableCtrlReconnect();
        }
        if (ctrlReconnectInterval == null) {
            ctrlReconnectInterval = AdapterFileConfig.getInstance().getCtrlReconnectInterval();
        }
        if (deviceConfig == null) {
            deviceConfig = DeviceFileConfig.getInstance();
        }
        new ProtocolAdapterService(this).start();
        if (deviceConfig != null && deviceConfig instanceof DeviceFileConfig) {
            ((DeviceFileConfig) deviceConfig).initFileDeviceConfig();
        }
    }

    public Config adapterConfig(IAdapterConfig adapterConfig) {
        name = StringUtil.isNullOrEmpty(adapterConfig.getName()) ? name : adapterConfig.getName();
        connectionHost = adapterConfig.getConnectionHost();
        serviceId = adapterConfig.getServiceId();
        instanceName = adapterConfig.getInstanceName();
        instanceKey = adapterConfig.getInstanceKey();
        enableTls = adapterConfig.enableTls();
        enableMetrics = adapterConfig.enableMetrics();
        enableCtrlReconnect = adapterConfig.enableCtrlReconnect();
        ctrlReconnectInterval = adapterConfig.getCtrlReconnectInterval();
        return this;
    }

    public Config name(String name) {
        this.name = name;
        return this;
    }

    public Config connectionHost(String connectionHost) {
        this.connectionHost = connectionHost;
        return this;
    }

    public Config serviceId(String serviceId) {
        this.serviceId = serviceId;
        return this;
    }

    public Config instanceName(String instanceName) {
        this.instanceName = instanceName;
        return this;
    }

    public Config instanceKey(String instanceKey) {
        this.instanceKey = instanceKey;
        return this;
    }

    public Config enableTls(Boolean enableTls) {
        this.enableTls = enableTls;
        return this;
    }

    public Config enableMetrics(Boolean enableMetrics) {
        this.enableMetrics = enableMetrics;
        return this;
    }

    public Config enableCtrlReconnect(Boolean enableCtrlReconnect) {
        this.enableCtrlReconnect = enableCtrlReconnect;
        return this;
    }

    public Config ctrlReconnectInterval(Long ctrlReconnectInterval) {
        this.ctrlReconnectInterval = ctrlReconnectInterval;
        return this;
    }

    public Config backoffReachTimes(Integer backoffReachTimes) {
        this.backoffReachTimes = backoffReachTimes;
        return this;
    }

    public Config backoffExp(Integer backoffExp) {
        this.backoffExp = backoffExp;
        return this;
    }

    public Config deviceConfig(IDeviceConfig deviceConfig) {
        this.deviceConfig = deviceConfig;
        return this;
    }

    public Config downLinkRequestHandler(DownLinkRequestHandler downLinkRequestHandler) {
        this.downLinkRequestHandler = downLinkRequestHandler;
        return this;
    }

    public Config subDeviceDownLinkRequestHandler(SubDeviceDownLinkRequestHandler subDeviceDownLinkRequestHandler) {
        this.subDeviceDownLinkRequestHandler = subDeviceDownLinkRequestHandler;
        return this;
    }

    public ILogger getLogger() {
        return logger;
    }

    public String getName() {
        if (StringUtil.isNullOrEmpty(name)) {
            name = DEFAULT_NAME;
        }
        return name;
    }

    public String getConnectionHost() {
        String desc;
        if (StringUtil.isNullOrEmpty(connectionHost)) {
            if (enableTls != null && enableTls) {
                desc = "config 'connectionHost' is not set, using default value: " + DEFAULT_CONNECTION_HOST_TLS;
                connectionHost = DEFAULT_CONNECTION_HOST_TLS;
            } else {
                connectionHost = DEFAULT_CONNECTION_HOST_NO_TLS;
                desc = "config 'connectionHost' is not set, using default value: " + DEFAULT_CONNECTION_HOST_NO_TLS;
            }
            getLogger().logInnerWarn(name, LoggerFormat.Action.INIT, desc);
        }
        return connectionHost;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public String getInstanceKey() {
        return instanceKey;
    }

    public Boolean getEnableTls() {
        if (!StringUtil.isNullOrEmpty(connectionHost)) {
            InetSocketAddress address = InetSocketAddressUtils.getConnectionHost(connectionHost);
            int port = address.getPort();
            String desc = null;
            if (port == 1883) {
                if (enableTls == null) {
                    desc = "config '" + ADAPTER_ENABLE_TLS + "' is not set, using default value: false";
                } else if (enableTls) {
                    desc = "config '" + ADAPTER_ENABLE_TLS + "' is not matched with '" + CONNECTION_HOST + "', using matched value: false";
                }
                enableTls = false;
            } else if (port == 8883) {
                if (enableTls == null) {
                    desc = "config '" + ADAPTER_ENABLE_TLS + "' is not set, using default value: true";
                } else if (!enableTls) {
                    desc = "config '" + ADAPTER_ENABLE_TLS + "' is not matched with '" + CONNECTION_HOST + "', using matched value: true";
                }
                enableTls = true;
            }
            if (desc != null) {
                getLogger().logInnerWarn(name, LoggerFormat.Action.INIT, desc);
            }
            return enableTls;
        } else {
            if (enableTls == null) {
                String desc = "config '" + ADAPTER_ENABLE_TLS + "' is not set, using default value: " + DEFAULT_ENABLE_TLS;
                getLogger().logInnerWarn(name, LoggerFormat.Action.INIT, desc);
                enableTls = DEFAULT_ENABLE_TLS;
            }
            return enableTls;
        }
    }

    public Boolean getEnableMetrics() {
        if (enableMetrics == null) {
            String desc = "config '" + ADAPTER_ENABLE_METRICS + "' is not set, using default value: " + DEFAULT_ENABLE_METRICS;
            getLogger().logInnerWarn(name, LoggerFormat.Action.INIT, desc);
            enableMetrics = DEFAULT_ENABLE_METRICS;
        }
        return enableMetrics;
    }

    public Boolean getEnableCtrlReconnect() {
        if (enableCtrlReconnect == null) {
            String desc = "config '" + ENABLE_CTRL_RECONNECT + "' is not set, using default value: " + DEFAULT_ENABLE_CTRL_RECONNECT;
            getLogger().logInnerWarn(name, LoggerFormat.Action.INIT, desc);
            enableCtrlReconnect = DEFAULT_ENABLE_CTRL_RECONNECT;
        }
        return enableCtrlReconnect;
    }

    public Long getCtrlReconnectInterval() {
        if (!getEnableCtrlReconnect()) {
            ctrlReconnectInterval = 0L;
            String desc = "config 'ctrlReconnectInterval' is set to 0 to match with config 'ctrlReconnect' = false";
            getLogger().logInnerWarn(name, LoggerFormat.Action.INIT, desc);
            return ctrlReconnectInterval;
        }
        if (ctrlReconnectInterval == null) {
            String desc = "config 'ctrlReconnectInterval' is not set, using default value: " + DEFAULT_CTRL_RECONNECT_INTERVAL;
            getLogger().logInnerWarn(name, LoggerFormat.Action.INIT, desc);
            ctrlReconnectInterval = DEFAULT_CTRL_RECONNECT_INTERVAL;
        }
        return ctrlReconnectInterval;
    }

    public Integer getBackoffReachTimes() {
        return backoffReachTimes == null ? DEFAULT_BACKOFF_REACH_TIMES : backoffReachTimes;
    }

    public Integer getBackoffExp() {
        return backoffExp == null ? DEFAULT_BACKOFF_EXP : backoffExp;
    }

    public IDeviceConfig getDeviceConfig() {
        return deviceConfig;
    }

    public DownLinkRequestHandler getDownLinkRequestHandler() {
        if (downLinkRequestHandler == null) {
            String desc = "DownLinkRequestHandler is not configured, use default.";
            downLinkRequestHandler = new DefaultDeviceDownLinkHandler();
            getLogger().logInnerWarn(name, LoggerFormat.Action.INIT, desc);
        }
        return downLinkRequestHandler;
    }

    public SubDeviceDownLinkRequestHandler getSubDeviceDownLinkRequestHandler() {
        if (subDeviceDownLinkRequestHandler == null) {
            String desc = "SubDeviceDownLinkRequestHandler is not configured, use default.";
            subDeviceDownLinkRequestHandler = new DefaultSubDeviceDownLinkRequestHandler();
            getLogger().logInnerWarn(name, LoggerFormat.Action.INIT, desc);
        }
        return subDeviceDownLinkRequestHandler;
    }

    @Override
    public String toString() {
        return "Config{" +
                "name='" + name + '\'' +
                ", connectionHost='" + connectionHost + '\'' +
                ", serviceId='" + serviceId + '\'' +
                ", instanceName='" + instanceName + '\'' +
                ", instanceKey='" + instanceKey + '\'' +
                ", enableTls=" + enableTls +
                ", enableMetrics=" + enableMetrics +
                ", enableCtrlReconnect=" + enableCtrlReconnect +
                ", ctrlReconnectInterval=" + ctrlReconnectInterval +
                ", backoffReachTimes=" + backoffReachTimes +
                ", backoffExp=" + backoffExp +
                '}';
    }
}
