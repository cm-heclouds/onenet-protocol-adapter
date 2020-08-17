package com.github.cm.heclouds.adapter.config;

import com.github.cm.heclouds.adapter.ProtocolAdapterService;
import com.github.cm.heclouds.adapter.api.ConfigUtils;
import com.github.cm.heclouds.adapter.config.impl.DeviceFileConfig;
import com.github.cm.heclouds.adapter.custom.DeviceDownLinkHandler;
import com.github.cm.heclouds.adapter.utils.InetSocketAddressUtils;
import com.github.cm.heclouds.adapter.core.config.CoreConfig;
import com.github.cm.heclouds.adapter.core.logging.ILogger;
import com.github.cm.heclouds.adapter.core.logging.LoggerFormat;
import com.github.cm.heclouds.adapter.core.utils.CoreConfigUtils;
import io.netty.util.internal.StringUtil;

import java.net.InetSocketAddress;

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
    private static final String DEFAULT_CONNECTION_HOST_NO_TLS = "218.201.45.7:1883";
    /**
     * 默认平台接入机连接地址，非加密
     */
    private static final String DEFAULT_CONNECTION_HOST_TLS = "183.230.102.116:8883";
    /**
     * 默认SDK和平台接入机之间的控制连接断开后不重连
     */
    private static final Boolean DEFAULT_CTRL_RECONNECT = Boolean.FALSE;
    /**
     * 默认SDK和平台接入机之间不使用加密传输
     */
    private static final Boolean DEFAULT_TLS_SUPPORT = Boolean.FALSE;
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
    private String name = DEFAULT_NAME;
    /**
     * 平台接入机连接地址，非必填
     */
    private String connectionHost;
    /**
     * 泛协议接入服务ID，必填
     */
    private String serviceId;
    /**
     * 泛协议接入服务实例名，必填
     */
    private String instanceName;
    /**
     * 泛协议接入服务实例Key，必填
     */
    private String instanceKey;
    /**
     * 和平台接入机之间是否使用加密传输，默认值为false
     */
    private Boolean tlsSupport;
    /**
     * 和平台接入机之间的控制连接异常断开后的初始重连等待时间，默认值为30秒
     */
    private Boolean ctrlReconnect;
    /**
     * 和平台接入机之间的控制连接断开后重连次数，非必填，默认值为3
     */
    private Long ctrlReconnectInterval;
    /**
     * 指数退避触发条件，默认2
     */
    private Integer backoffReachTimes;
    /**
     * 退避倍数，默认4
     */
    private Integer backoffExp;
    /**
     * 设备配置类，非必填，默认为{@link DeviceFileConfig}
     */
    private IDeviceConfig deviceConfig;
    /**
     * 下行数据处理，必填
     */
    private DeviceDownLinkHandler deviceDownLinkHandler;

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
        new ProtocolAdapterService(this).start();
        if (this.deviceConfig != null && this.deviceConfig instanceof DeviceFileConfig) {
            ((DeviceFileConfig) this.deviceConfig).initFileDeviceConfig();
        }
    }

    public Config adapterConfig(IAdapterConfig adapterConfig) {
        this.name = StringUtil.isNullOrEmpty(adapterConfig.getName()) ? this.name : adapterConfig.getName();
        this.connectionHost = adapterConfig.getConnectionHost();
        this.serviceId = adapterConfig.getServiceId();
        this.instanceKey = adapterConfig.getInstanceKey();
        this.instanceName = adapterConfig.getInstanceName();
        this.tlsSupport = adapterConfig.tlsSupport();
        this.ctrlReconnect = adapterConfig.ctrlReconnect();
        this.ctrlReconnectInterval = adapterConfig.getCtrlReconnectInterval();
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

    public Config tlsSupport(Boolean tlsSupport) {
        this.tlsSupport = tlsSupport;
        return this;
    }

    public Config ctrlReconnect(Boolean ctrlReconnect) {
        this.ctrlReconnect = ctrlReconnect;
        return this;
    }

    public void ctrlReconnectInterval(Long ctrlReconnectInterval) {
        this.ctrlReconnectInterval = ctrlReconnectInterval;
    }

    public void backoffReachTimes(Integer backoffReachTimes) {
        this.backoffReachTimes = backoffReachTimes;
    }

    public void backoffExp(Integer backoffExp) {
        this.backoffExp = backoffExp;
    }

    public Config deviceConfig(IDeviceConfig deviceConfig) {
        this.deviceConfig = deviceConfig;
        return this;
    }

    public Config deviceDownLinkHandler(DeviceDownLinkHandler deviceDownLinkHandler) {
        this.deviceDownLinkHandler = deviceDownLinkHandler;
        return this;
    }

    public ILogger getLogger() {
        return logger;
    }

    public String getName() {
        return name;
    }

    public String getConnectionHost() {
        String desc;
        if (StringUtil.isNullOrEmpty(connectionHost)) {
            if (tlsSupport != null && tlsSupport) {
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

    public Boolean getTlsSupport() {
        if (!StringUtil.isNullOrEmpty(connectionHost)) {
            InetSocketAddress address = InetSocketAddressUtils.getConnectionHost(connectionHost);
            int port = address.getPort();
            String desc = null;
            if (port == 1883) {
                if (tlsSupport == null) {
                    desc = "config 'tlsSupport' is not set, using default value: false";
                } else if (tlsSupport) {
                    desc = "config 'tlsSupport' is not matched with 'connectionHost', using matched value: false";
                }
                tlsSupport = false;
            } else if (port == 8883) {
                if (tlsSupport == null) {
                    desc = "config 'tlsSupport' is not set, using default value: true";
                } else if (!tlsSupport) {
                    desc = "config 'tlsSupport' is not matched with 'connectionHost', using matched value: true";
                }
                tlsSupport = true;
            }
            if (desc != null) {
                getLogger().logInnerWarn(name, LoggerFormat.Action.INIT, desc);
            }
            return tlsSupport;
        } else {
            if (tlsSupport == null) {
                String desc = "config 'tlsSupport' is not set, using default value: " + DEFAULT_TLS_SUPPORT;
                getLogger().logInnerWarn(name, LoggerFormat.Action.INIT, desc);
                tlsSupport = DEFAULT_TLS_SUPPORT;
            }
            return tlsSupport;
        }
    }

    public Boolean getCtrlReconnect() {
        if (ctrlReconnect == null) {
            String desc = "config 'ctrlReconnect' is not set, using default value: " + DEFAULT_CTRL_RECONNECT;
            getLogger().logInnerWarn(name, LoggerFormat.Action.INIT, desc);
            ctrlReconnect = DEFAULT_CTRL_RECONNECT;
        }
        return ctrlReconnect;
    }

    public Long getCtrlReconnectInterval() {
        if (!getCtrlReconnect()) {
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

    public DeviceDownLinkHandler getDeviceDownLinkHandler() {
        if (deviceDownLinkHandler == null) {
            String desc = "DeviceDownLinkHandler is not configured!";
            getLogger().logInnerError(name, LoggerFormat.Action.INIT, desc, null);
            System.exit(0);
        }
        return deviceDownLinkHandler;
    }

    @Override
    public String toString() {
        return "Config{" +
                "name='" + name + '\'' +
                ", connectionHost='" + connectionHost + '\'' +
                ", serviceId='" + serviceId + '\'' +
                ", instanceName='" + instanceName + '\'' +
                ", instanceKey='" + instanceKey + '\'' +
                ", tlsSupport=" + tlsSupport +
                ", ctrlReconnect=" + ctrlReconnect +
                ", ctrlReconnectInterval=" + ctrlReconnectInterval +
                ", backoffReachTimes=" + backoffReachTimes +
                ", backoffExp=" + backoffExp +
                '}';
    }
}
