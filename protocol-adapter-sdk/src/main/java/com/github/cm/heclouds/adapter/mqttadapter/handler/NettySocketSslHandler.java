package com.github.cm.heclouds.adapter.mqttadapter.handler;

import com.github.cm.heclouds.adapter.api.ConfigUtils;
import com.github.cm.heclouds.adapter.exceptions.InternalException;
import com.github.cm.heclouds.adapter.core.logging.ILogger;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.nio.ByteBuffer;

import static com.github.cm.heclouds.adapter.core.logging.LoggerFormat.Action.RUNTIME;


/**
 * TLS加密Handler
 */
public class NettySocketSslHandler extends SimpleChannelInboundHandler<ByteBuffer> {

    private final ILogger logger = ConfigUtils.getLogger();

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        // Once session is secured, send a greeting and register the channel to the global channel
        // list so the channel received the messages from others.
        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
                (GenericFutureListener<Future<Channel>>) future -> {
                    if (future.isSuccess()) {
                        byte[] array = new byte[]{(byte) 7d, 04};
                        ByteBuffer bu = ByteBuffer.wrap(array);
                        ctx.channel().writeAndFlush(bu);
                    } else {
                        logger.logInnerError(ConfigUtils.getName(), RUNTIME, "Handshake failed, is the certificate correct?", null);
                        throw new InternalException("Handshake failed");
                    }
                });
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.logInnerError(ConfigUtils.getName(), RUNTIME, "Unexpected exception from downstream. cause: ", cause);
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuffer msg) throws Exception {
        byte[] array = new byte[]{00, 01, 00, 00, 00, 06, 05, 03, (byte) 7d, 00, 00, 07};
        ByteBuffer bu = ByteBuffer.wrap(array);
        ctx.channel().writeAndFlush(bu);
    }
}
