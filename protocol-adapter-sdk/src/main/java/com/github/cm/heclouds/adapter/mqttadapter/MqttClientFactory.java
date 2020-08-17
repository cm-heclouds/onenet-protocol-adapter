package com.github.cm.heclouds.adapter.mqttadapter;

import com.github.cm.heclouds.adapter.config.Config;
import com.github.cm.heclouds.adapter.extensions.metric.MetricHandler;
import com.github.cm.heclouds.adapter.mqttadapter.handler.ContextSSLFactory;
import com.github.cm.heclouds.adapter.mqttadapter.handler.MqttHandler;
import com.github.cm.heclouds.adapter.mqttadapter.handler.NettySocketSslHandler;
import com.github.cm.heclouds.adapter.mqttadapter.handler.ProtocolMessageHandler;
import com.github.cm.heclouds.adapter.mqttadapter.mqtt.MqttPingHandler;
import com.github.cm.heclouds.adapter.utils.InetSocketAddressUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetSocketAddress;

/**
 * Mqtt Client工厂类
 */
final class MqttClientFactory {

    /**
     * 初始化Netty Client
     *
     * @param config  配置
     * @return 连接channel
     */
    static Channel initializeNettyClient(Config config) {
        InetSocketAddress broker = InetSocketAddressUtils.getConnectionHost(config.getConnectionHost());
        EventLoopGroup eventLoopGroup;
        Class<? extends AbstractChannel> channelClass;
        if (Epoll.isAvailable()) {
            eventLoopGroup = new EpollEventLoopGroup();
            channelClass = EpollSocketChannel.class;
        } else {
            eventLoopGroup = new NioEventLoopGroup();
            channelClass = NioSocketChannel.class;
        }
        Bootstrap b = new Bootstrap();
        b.group(eventLoopGroup);
        b.channel(channelClass);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.option(ChannelOption.TCP_NODELAY, true);
        Boolean finalTlsSupport = config.getTlsSupport();
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
                ChannelPipeline p = ch.pipeline();
                p.addLast("metricHandler", MetricHandler.getInstance(config));
                p.addLast("idleStateHandler", new IdleStateHandler(60, 60, 0));
                p.addLast("mqttDecoder", new MqttDecoder());
                p.addLast("mqttPingHandler", new MqttPingHandler(60));
                p.addLast("mqttEncoder", MqttEncoder.INSTANCE);
                p.addLast("mqttHandler", new MqttHandler());
                p.addLast("protocolMessageHandler", ProtocolMessageHandler.INSTANCE);

                if (finalTlsSupport) {
                    p.addLast("sslHandler", new NettySocketSslHandler());
                    p.addFirst("ssl", ContextSSLFactory.getSslContext().newHandler(ch.alloc()));
                }
            }
        });

        try {
            return b.connect(broker.getAddress(), broker.getPort()).sync().channel();
        } catch (InterruptedException e) {
            System.exit(1);
            return null;
        }
    }
}
