package com.github.cm.heclouds.adapter.protocolhub.tcp.handler;

import com.github.cm.heclouds.adapter.core.consts.CloseReason;
import com.github.cm.heclouds.adapter.core.entity.Device;
import com.github.cm.heclouds.adapter.core.logging.ILogger;
import com.github.cm.heclouds.adapter.core.logging.LoggerFormat;
import com.github.cm.heclouds.adapter.core.utils.DeviceUtils;
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
            // 移除设备连接关闭原因，防止调用OpenApi主动登出设备后设备连接关闭原因一直被认为是主动登出
            DeviceUtils.removeDeviceCloseReason(device);
            // 解码
            tcpDeviceUpLinkHandler.processUpLinkData(device, msg, channel);
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
        TcpDeviceSession devSession = TcpDeviceSessionNettyUtils.deviceSession(channel);
        Device device = TcpDeviceSessionNettyUtils.device(channel);
        if (devSession == null || device == null) {
            return;
        }
        if (DeviceUtils.getDeviceCloseReason(device) != CloseReason.CLOSE_BY_DEVICE_OFFLINE) {
            tcpDeviceUpLinkHandler.processConnectionLost(device, channel);
        }
        TcpDeviceSessionManager.handleConnectionLost(devSession);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        Device device = TcpDeviceSessionNettyUtils.device(ctx.channel());
        String pid = device == null ? null : device.getProductId();
        String deviceName = device == null ? null : device.getDeviceName();
        logger.logInnerWarn(TcpProtocolHubConfigUtils.getName(), RUNTIME, pid, deviceName, "exceptionCaught: " + cause);
        if (!(cause instanceof IOException)) {
            if (device != null) {
                DeviceUtils.setDeviceCloseReason(device, CloseReason.CLOSE_DUE_TO_UNKNOWN_EXCEPTION);
            }
        }
        if (ctx.channel().isActive()) {
            ctx.close();
        }
    }
}
