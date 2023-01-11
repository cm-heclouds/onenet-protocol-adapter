package com.github.cm.heclouds.adapter.entity.sdk;

import com.github.cm.heclouds.adapter.mqttadapter.MqttClient;
import io.netty.channel.Channel;

import java.util.AbstractMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 代理连接Session
 * <p>
 * 一个代理连接代理多个设备连接，负责这些设备与平台之间的通信
 */
public final class ProxySession {

    /**
     * 此代理连接代理的DeviceInfo和DeviceSession的映射关系
     * 其中Pair<Long, String>中Long为产品ID，String为设备名称
     */
    private ConcurrentMap<AbstractMap.SimpleEntry<String, String>, DeviceSession> proxyDevAssociation = new ConcurrentHashMap<>();

    /**
     * MQTT Client
     */
    private MqttClient mqttClient;

    /**
     * 代理连接ID
     */
    private String proxyId;

    /**
     * 代理连接绑定的Netty Channel
     */
    private Channel channel;

    /**
     * 代理连接是否连接
     */
    private volatile boolean connected;

    /**
     * 代理的设备数量是否达到限制
     */
    private volatile boolean isDevicesReachedLimit;

    private ProxySession(Builder builder) {
        setProxyDevAssociation(builder.proxyDevAssociation);
        setMqttClient(builder.mqttClient);
        setProxyId(builder.proxyId);
        setChannel(builder.channel);
        setConnected(builder.connected);
        setDevicesReachedLimit(builder.isDevicesReachedLimit);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public ConcurrentMap<AbstractMap.SimpleEntry<String, String>, DeviceSession> getProxyDevAssociation() {
        return proxyDevAssociation;
    }

    public void setProxyDevAssociation(ConcurrentMap<AbstractMap.SimpleEntry<String, String>, DeviceSession> proxyDevAssociation) {
        this.proxyDevAssociation = proxyDevAssociation;
    }

    public MqttClient getMqttClient() {
        return mqttClient;
    }

    public void setMqttClient(MqttClient mqttClient) {
        this.mqttClient = mqttClient;
    }

    public String getProxyId() {
        return proxyId;
    }

    public void setProxyId(String proxyId) {
        this.proxyId = proxyId;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isDevicesReachedLimit() {
        return isDevicesReachedLimit;
    }

    public void setDevicesReachedLimit(boolean devicesReachedLimit) {
        isDevicesReachedLimit = devicesReachedLimit;
    }

    /**
     * 保存此代理连接代理的设备Session
     *
     * @param deviceSession 设备Session
     */
    public void putDeviceSession(DeviceSession deviceSession) {
        proxyDevAssociation.put(new AbstractMap.SimpleEntry<>(deviceSession.getProductId(), deviceSession.getDeviceName()), deviceSession);
    }

    /**
     * 获取此代理连接代理的设备Session
     *
     * @param productId  产品ID
     * @param deviceName 设备名称
     * @return 设备Session
     */
    public DeviceSession getDeviceSession(String productId, String deviceName) {
        return proxyDevAssociation.get(new AbstractMap.SimpleEntry<>(productId, deviceName));
    }

    /**
     * 移出此代理连接代理的设备Session
     *
     * @param productId  产品ID
     * @param deviceName 设备名称
     */
    public void removeDeviceSession(String productId, String deviceName) {
        proxyDevAssociation.remove(new AbstractMap.SimpleEntry<>(productId, deviceName));
    }

    /**
     * 获取此代理连接代理的设备数量
     *
     * @return 代理的设备数量
     */
    public int size() {
        return proxyDevAssociation.size();
    }

    public static final class Builder {
        private ConcurrentMap<AbstractMap.SimpleEntry<String, String>, DeviceSession> proxyDevAssociation;
        private MqttClient mqttClient;
        private String proxyId;
        private Channel channel;
        private boolean connected;
        private boolean isDevicesReachedLimit;

        private Builder() {
        }

        public Builder proxyDevAssociation(ConcurrentMap<AbstractMap.SimpleEntry<String, String>, DeviceSession> val) {
            proxyDevAssociation = val;
            return this;
        }

        public Builder mqttClient(MqttClient val) {
            mqttClient = val;
            return this;
        }

        public Builder proxyId(String val) {
            proxyId = val;
            return this;
        }

        public Builder channel(Channel val) {
            channel = val;
            return this;
        }


        public Builder connected(boolean val) {
            connected = val;
            return this;
        }

        public Builder isDevicesReachedLimit(boolean val) {
            isDevicesReachedLimit = val;
            return this;
        }

        public ProxySession build() {
            return new ProxySession(this);
        }
    }
}
