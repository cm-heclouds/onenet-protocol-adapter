package com.github.cm.heclouds.adapter.mqttadapter;

import com.github.cm.heclouds.adapter.config.Config;
import com.github.cm.heclouds.adapter.mqttadapter.mqtt.MqttSubscribeFuture;
import com.github.cm.heclouds.adapter.mqttadapter.mqtt.MqttSubscription;
import com.github.cm.heclouds.adapter.mqttadapter.mqtt.PromiseCanceller;
import com.github.cm.heclouds.adapter.mqttadapter.mqtt.promise.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttUnsubAckMessage;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.github.cm.heclouds.adapter.mqttadapter.mqtt.MqttFixedHeaders.DISCONNECT_HEADER;


/**
 * Mqtt Client，用于与平台接入机的通信
 */
public final class MqttClient {

    private Channel channel;

    public MqttClient(Config config) {
        this.channel = MqttClientFactory.initializeNettyClient(config);
    }

    public MqttConnectResult connect(String clientId, String userName, String psw) throws ExecutionException, InterruptedException {
        MqttConnect mqttConnect = new MqttConnect();
        mqttConnect.setClientId(clientId.getBytes());
        mqttConnect.setUsername(userName);
        mqttConnect.setPassword(psw);
        Future<MqttConnectResult> future = writeAndFlush(new MqttConnectPromise(mqttConnect, channel.eventLoop()));
        return future.get();
    }

    public MqttSubscribeFuture subscribe(List<MqttSubscription> subscriptions) {
        return writeAndFlush(new MqttSubscribePromise(channel.eventLoop(), subscriptions));
    }

    public Future<MqttUnsubAckMessage> unsubscribe(List<String> topicFilters) {
        return writeAndFlush(new MqttUnsubscribePromise(channel.eventLoop(), topicFilters));
    }

    public Future<Void> disconnect() throws InterruptedException {
        ChannelFuture channelFuture = channel.writeAndFlush(new MqttMessage(DISCONNECT_HEADER));
        channel.closeFuture().sync();
        channel.eventLoop().shutdownGracefully();
        return channelFuture;
    }

    private synchronized <P extends Promise<V>, V> P writeAndFlush(P promise) {
        channel.writeAndFlush(promise).addListener(new PromiseCanceller<>(promise));
        return promise;
    }

    public Channel getChannel() {
        return channel;
    }
}
