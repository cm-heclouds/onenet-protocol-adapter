package com.github.cm.heclouds.adapter.protocolhub.tcp.session;

import io.netty.channel.Channel;

/**
 * 设备连接Session实体类，
 * <p>
 * 设备连接Session会在设备连接泛协议接入SDK后建立，设备与设备Session一对一映射
 */
public final class TcpDeviceSession {
    /**
     * 产品ID
     */
    private String productId;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 设备连接对应的Channel
     */
    private Channel channel;

    private TcpDeviceSession(Builder builder) {
        setProductId(builder.productId);
        setDeviceName(builder.deviceName);
        setChannel(builder.channel);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public static final class Builder {
        private String productId;
        private String deviceName;
        private Channel channel;

        private Builder() {
        }

        public Builder productId(String val) {
            productId = val;
            return this;
        }

        public Builder deviceName(String val) {
            deviceName = val;
            return this;
        }

        public Builder channel(Channel val) {
            channel = val;
            return this;
        }

        public TcpDeviceSession build() {
            return new TcpDeviceSession(this);
        }
    }
}
