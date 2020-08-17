package com.github.cm.heclouds.adapter.protocolhub.tcp.config;

import com.github.cm.heclouds.adapter.protocolhub.tcp.TcpProtocolHubService;
import com.github.cm.heclouds.adapter.protocolhub.tcp.custom.TcpDeviceUpLinkHandler;
import com.github.cm.heclouds.adapter.core.config.CoreConfig;
import com.github.cm.heclouds.adapter.core.logging.ILogger;
import com.github.cm.heclouds.adapter.core.logging.LoggerFormat;
import com.github.cm.heclouds.adapter.core.utils.CoreConfigUtils;
import io.netty.channel.ChannelPipeline;
import io.netty.util.internal.StringUtil;

/**
 * TCP协议站配置类
 */
public abstract class TcpProtocolHubConfig {
    /**
     * TCP协议站默认名称
     */
    public static final String DEFAULT_NAME = "TCPHub";

    /**
     * 默认TCP协议站启动的端口号
     */
    private static final Integer DEFAULT_PORT = 10086;

    /**
     * 日志
     */
    private final ILogger logger;
    /**
     * TCP协议站名称，非必填
     */
    private String name = DEFAULT_NAME;
    /**
     * TCP协议站启动地址，非必填
     */
    private String host;
    /**
     * TCP协议站启动端口号，非必填
     */
    private Integer port = DEFAULT_PORT;
    /**
     * 上行数据处理，必填
     */
    private TcpDeviceUpLinkHandler tcpDeviceUpLinkHandler;

    /**
     * 添加泛协议编解码handler
     *
     * @param pipeline ChannelPipeline
     */
    public abstract void addChannelHandlers(ChannelPipeline pipeline);

    public TcpProtocolHubConfig(ILogger logger) {
        this.logger = logger;
        if (CoreConfigUtils.getCoreConfig() == null) {
            CoreConfigUtils.setCoreConfig(new CoreConfig(this.logger));
        }
        TcpProtocolHubConfigUtils.setConfig(this);
    }

    public void init() {
        new TcpProtocolHubService(this).start();
    }

    public TcpProtocolHubConfig tcpProtocolHubConfig(ITcpConfig tcpConfig) {
        this.name = StringUtil.isNullOrEmpty(tcpConfig.getName()) ? this.name : tcpConfig.getName();
        this.host = tcpConfig.getHost();
        this.port = tcpConfig.getPort();
        return this;
    }


    public TcpProtocolHubConfig name(String name) {
        this.name = name;
        return this;
    }

    public TcpProtocolHubConfig host(String host) {
        this.host = host;
        return this;
    }

    public TcpProtocolHubConfig port(Integer port) {
        this.port = port;
        return this;
    }

    public TcpProtocolHubConfig tcpDeviceUpLinkHandler(TcpDeviceUpLinkHandler tcpDeviceUpLinkHandler) {
        this.tcpDeviceUpLinkHandler = tcpDeviceUpLinkHandler;
        return this;
    }

    public ILogger getLogger() {
        return logger;
    }

    public String getName() {
        return name;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        if (port == null) {
            port = DEFAULT_PORT;
            logger.logInnerWarn(name, LoggerFormat.Action.INIT, "config 'port' is not set, using default value: " + DEFAULT_PORT);
        }
        return port;
    }

    public TcpDeviceUpLinkHandler getTcpDeviceUpLinkHandler() {
        if (tcpDeviceUpLinkHandler == null) {
            String desc = "TcpDeviceUpLinkHandler is not configured!";
            logger.logInnerError(name, LoggerFormat.Action.INIT, desc, null);
            System.exit(0);
        }
        return tcpDeviceUpLinkHandler;
    }

    @Override
    public String toString() {
        return "TcpProtocolHubConfig{" +
                "name='" + name + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
