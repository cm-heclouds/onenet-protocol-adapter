package com.github.cm.heclouds.adapter.protocolhub.tcp.handler;

import com.github.cm.heclouds.adapter.core.entity.Device;
import com.github.cm.heclouds.adapter.core.logging.ILogger;
import com.github.cm.heclouds.adapter.core.logging.LoggerFormat;
import com.github.cm.heclouds.adapter.protocolhub.tcp.config.TcpProtocolHubConfig;
import com.github.cm.heclouds.adapter.protocolhub.tcp.config.TcpProtocolHubConfigUtils;
import com.github.cm.heclouds.adapter.protocolhub.tcp.custom.TcpDeviceUpLinkHandler;
import com.github.cm.heclouds.adapter.protocolhub.tcp.session.TcpDeviceSession;
import com.github.cm.heclouds.adapter.protocolhub.tcp.session.TcpDeviceSessionManager;
import com.github.cm.heclouds.adapter.protocolhub.tcp.session.TcpDeviceSessionNettyUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import static com.github.cm.heclouds.adapter.core.logging.LoggerFormat.Action.RUNTIME;

/**
 * TCP协议接入中心核心Handler，连接自定义数据的编解码、设备消息的接收和发送
 */
public final class TcpProtocolHubHandler extends ChannelDuplexHandler {

    private final Integer port;
    private final ILogger logger;
    private final TcpDeviceUpLinkHandler tcpDeviceUpLinkHandler;

    public TcpProtocolHubHandler(TcpProtocolHubConfig config) {
        this.port = config.getPort();
        logger = config.getLogger();
        tcpDeviceUpLinkHandler = config.getTcpDeviceUpLinkHandler();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.logInnerInfo(TcpProtocolHubConfigUtils.getName(), RUNTIME, "address " + ctx.channel().remoteAddress().toString() + " is connected, tcp hub port: " + port);
        ctx.channel().read();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        Channel channel = ctx.channel();
        Device device = TcpDeviceSessionNettyUtils.device(channel);
        if (device == null) {
            device = tcpDeviceUpLinkHandler.initDevice(msg, channel);
            if (device == null) {
                logger.logDevError(TcpProtocolHubConfigUtils.getName(), LoggerFormat.Action.DEV_UP_LINK, "init device failed", null, null, null);
            } else {
                logger.logInnerInfo(TcpProtocolHubConfigUtils.getName(), RUNTIME, device + " is initialized, tcp hub port: " + port);
                TcpDeviceSession deviceSession = TcpDeviceSessionManager.createDeviceSession(device.getProductId(), device.getDeviceName(), channel);
                TcpDeviceSessionNettyUtils.setDeviceSession(channel, deviceSession);
                TcpDeviceSessionNettyUtils.setDevice(channel, device);
                TcpDeviceSessionManager.putDeviceSession(deviceSession);
            }
        } else {
            // 解码
            tcpDeviceUpLinkHandler.processUpLinkData(device, msg, channel);
        }
        channel.read();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        TcpDeviceSession devSession = TcpDeviceSessionNettyUtils.deviceSession(channel);
        if (devSession == null) {
            return;
        }
        tcpDeviceUpLinkHandler.processConnectionLost(TcpDeviceSessionNettyUtils.device(channel), channel);
        TcpDeviceSessionManager.handleConnectionLost(devSession);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.logInnerError(TcpProtocolHubConfigUtils.getName(), RUNTIME, "exceptionCaught", cause);
        if (!(cause instanceof IOException)) {
            String reason = cause.getLocalizedMessage();
            TcpDeviceSessionNettyUtils.setDeviceCloseReason(ctx.channel(), reason);
        }
        if (ctx.channel().isActive()) {
            ctx.close();
        }
    }
}
