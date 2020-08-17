package com.github.cm.heclouds.adapter.protocolhub.tcp.handler;

import com.github.cm.heclouds.adapter.protocolhub.tcp.session.TcpDeviceSessionNettyUtils;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * TCP协议接入中心Handler
 */
@ChannelHandler.Sharable
public final class TcpServerHandler extends SimpleChannelInboundHandler {

    public static final TcpServerHandler INSTANCE = new TcpServerHandler();

    private TcpServerHandler() {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 绑定设备连接的远程IP地址和协议站类型
        TcpDeviceSessionNettyUtils.setDeviceRemoteAddress(ctx.channel(), ctx.channel().remoteAddress().toString());
        TcpDeviceSessionNettyUtils.setDeviceProtocolType(ctx.channel(), "TCP");
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        ctx.fireChannelRead(msg);
    }
}
