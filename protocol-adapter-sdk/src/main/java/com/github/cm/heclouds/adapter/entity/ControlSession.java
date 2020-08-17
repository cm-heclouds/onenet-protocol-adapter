package com.github.cm.heclouds.adapter.entity;

import io.netty.channel.Channel;

/**
 * 控制连接Session实体类
 * <p>
 * 与平台接入机的控制连接会在泛协议接入服务实例启动时建立，且每个实例(以服务ID和服务实例名为标识)仅会保持一个控制连接
 */
public final class ControlSession {
    /**
     * 泛协议接入服务ID
     */
    private String serviceId;
    /**
     * 泛协议接入服务实例名称
     */
    private String instanceName;
    /**
     * 控制连接对应的Channel
     */
    private Channel channel;

    private ControlSession(Builder builder) {
        setServiceId(builder.serviceId);
        setInstanceName(builder.instanceName);
        setChannel(builder.channel);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }


    public static final class Builder {
        private String serviceId;
        private String instanceName;
        private Channel channel;

        private Builder() {
        }

        public Builder serviceId(String val) {
            serviceId = val;
            return this;
        }

        public Builder instanceName(String val) {
            instanceName = val;
            return this;
        }

        public Builder channel(Channel val) {
            channel = val;
            return this;
        }

        public ControlSession build() {
            return new ControlSession(this);
        }
    }
}
