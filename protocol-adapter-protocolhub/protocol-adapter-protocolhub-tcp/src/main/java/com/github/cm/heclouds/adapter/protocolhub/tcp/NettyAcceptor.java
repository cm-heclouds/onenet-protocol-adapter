package com.github.cm.heclouds.adapter.protocolhub.tcp;

import com.github.cm.heclouds.adapter.protocolhub.tcp.handler.TcpProtocolHubHandler;
import com.github.cm.heclouds.adapter.protocolhub.tcp.handler.TcpServerHandler;
import com.github.cm.heclouds.adapter.core.logging.ILogger;
import com.github.cm.heclouds.adapter.protocolhub.tcp.config.TcpProtocolHubConfig;
import com.github.cm.heclouds.adapter.protocolhub.tcp.config.TcpProtocolHubConfigUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.StringUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.github.cm.heclouds.adapter.core.logging.LoggerFormat.Action.INIT;
import static com.github.cm.heclouds.adapter.core.logging.LoggerFormat.Action.LAUNCH;
import static io.netty.channel.ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE;

/**
 * 用于协议接入中心的初始化
 */
final class NettyAcceptor {

    private final ILogger logger;
    private final TcpProtocolHubConfig config;
    private final List<EventLoopGroup> eventLoopGroups = new ArrayList<>();

    NettyAcceptor(TcpProtocolHubConfig config) {
        this.config = config;
        this.logger = this.config.getLogger();
    }

    /**
     * 初始化
     */
    void initialize() {
        Executors.newSingleThreadExecutor().execute(() -> {
            long beginTime = System.currentTimeMillis();
            logger.logInnerInfo(TcpProtocolHubConfigUtils.getName(), LAUNCH, "starting tcp protocol hub");
            logger.logInnerInfo(TcpProtocolHubConfigUtils.getName(), LAUNCH, config.toString());
            try {
                initializeProtocolHub(config);
                BigDecimal b = new BigDecimal((System.currentTimeMillis() - beginTime) / 1000);
                double time = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                logger.logInnerInfo(TcpProtocolHubConfigUtils.getName(), LAUNCH, "started tcp protocol hub in " + time + " seconds, bind:" + config.getPort());
            } catch (Exception e) {
                logger.logProtocolHubError(TcpProtocolHubConfigUtils.getName(), INIT, "failed:", e);
                System.exit(0);
            }
        });
    }

    /**
     * 初始化protocol hub
     *
     * @param config TCP协议站配置
     * @throws InterruptedException exception
     */
    private void initializeProtocolHub(TcpProtocolHubConfig config) throws InterruptedException {
        initializeTcpHub(config);
    }

    /**
     * 初始化TCP协议接入站
     *
     * @param config TCP协议站配置
     * @throws InterruptedException exception
     */
    private void initializeTcpHub(TcpProtocolHubConfig config) throws InterruptedException {
        String host = config.getHost();
        Integer port = config.getPort();
        ServerBootstrap bootstrap = configureTcpServerBootstrap();
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
                ChannelPipeline pipeline = ch.pipeline();
                config.addChannelHandlers(pipeline);
                pipeline.addLast("tcpHandler", TcpServerHandler.INSTANCE);
                pipeline.addLast("protocolHandler", new TcpProtocolHubHandler(config));
            }
        });
        ChannelFuture f;
        if (StringUtil.isNullOrEmpty(host)) {
            f = bootstrap.bind(port);
        } else {
            f = bootstrap.bind(host, port);
        }
        f.sync().addListener(FIRE_EXCEPTION_ON_FAILURE);
    }

    /**
     * 配置基于TCP的ServerBootstrap
     *
     * @return ServerBootstrap
     */
    private ServerBootstrap configureTcpServerBootstrap() {
        EventLoopGroup bossGroup;
        EventLoopGroup workerGroup;
        Class<? extends ServerSocketChannel> channelClass;
        if (Epoll.isAvailable()) {
            bossGroup = new EpollEventLoopGroup();
            workerGroup = new EpollEventLoopGroup();
            channelClass = EpollServerSocketChannel.class;
        } else {
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            channelClass = NioServerSocketChannel.class;
        }
        eventLoopGroups.add(bossGroup);
        eventLoopGroups.add(workerGroup);
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(channelClass)
                .option(ChannelOption.SO_BACKLOG, 1000000)
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, false)
                .childOption(ChannelOption.AUTO_READ, false);
        return b;
    }

    /**
     * 关闭EventLoop
     */
    void close() {
        for (EventLoopGroup group : eventLoopGroups) {
            if (group == null) {
                throw new IllegalStateException("Invoked close on an Acceptor that wasn't initialized");
            }
            Future<?> waiter = group.shutdownGracefully();

            try {
                waiter.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException iex) {
            }

            if (!group.isTerminated()) {
                group.shutdownGracefully(0L, 0L, TimeUnit.MILLISECONDS);
            }

        }
    }
}
