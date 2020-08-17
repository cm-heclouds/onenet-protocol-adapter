package com.github.cm.heclouds.adapter.protocolhub.tcp;

import com.github.cm.heclouds.adapter.core.logging.ILogger;
import com.github.cm.heclouds.adapter.protocolhub.tcp.config.TcpProtocolHubConfig;
import com.github.cm.heclouds.adapter.protocolhub.tcp.config.TcpProtocolHubConfigUtils;

import static com.github.cm.heclouds.adapter.core.logging.LoggerFormat.Action.INIT;

/**
 * 协议接入中心服务
 */
public final class TcpProtocolHubService {

    private final ILogger logger;
    private final NettyAcceptor acceptor;

    public TcpProtocolHubService(TcpProtocolHubConfig config) {
        this.logger = config.getLogger();
        this.acceptor = new NettyAcceptor(config);
    }

    /**
     * 启动协议接入中心服务
     */
    public void start() {
        try {
            acceptor.initialize();
        } catch (Exception e) {
            logger.logProtocolHubError(TcpProtocolHubConfigUtils.getName(), INIT, "failed", e);
            System.exit(1);
        }
    }

    /**
     * 停止协议接入中心服务
     */
    public void stop() {
        if (acceptor != null) {
            acceptor.close();
        }
    }
}
