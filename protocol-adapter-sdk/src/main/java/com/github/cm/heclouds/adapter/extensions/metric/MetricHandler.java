package com.github.cm.heclouds.adapter.extensions.metric;

import com.github.cm.heclouds.adapter.config.Config;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;


/**
 * 出入流量监控处理Handler
 */
@ChannelHandler.Sharable
public class MetricHandler extends ChannelDuplexHandler {
    private Metric metric;

    private MetricHandler() {
    }

    public static MetricHandler getInstance(Config config) {
        MetricHandler handler = MetricHandler.Inner.INSTANCE;
        handler.setMetric(Metric.INSTANCE);
        return handler;
    }

    public void setMetric(Metric metric) {
        this.metric = metric;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;
        metric.incrDownFlow(byteBuf.readableBytes());
        ctx.fireChannelRead(msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        ByteBuf byteBuf = (ByteBuf) msg;
        metric.incrUpFlow(byteBuf.readableBytes());
        ctx.write(msg, promise);
    }

    private static class Inner {
        private static final MetricHandler INSTANCE = new MetricHandler();
    }
}
