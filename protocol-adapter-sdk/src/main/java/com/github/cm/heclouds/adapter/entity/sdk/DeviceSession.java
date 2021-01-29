package com.github.cm.heclouds.adapter.entity.sdk;

/**
 * 设备连接Session实体类，
 * <p>
 * 设备连接Session会在设备连接泛协议接入SDK后建立，设备与设备Session一对一映射
 */
public final class DeviceSession {
    /**
     * 产品ID
     */
    private String productId;
    /**
     * 设备名称
     */
    private String deviceName;
    /**
     * 设备是否已连接到平台接入机
     */
    private volatile boolean login = false;

    /**
     * 代理此设备的代理连接Session
     */
    private ProxySession proxySession;

    private DeviceSession(Builder builder) {
        setProductId(builder.productId);
        setDeviceName(builder.deviceName);
        setProxySession(builder.proxySession);
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

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public ProxySession getProxySession() {
        return proxySession;
    }

    public void setProxySession(ProxySession proxySession) {
        this.proxySession = proxySession;
    }

    public static final class Builder {
        private String productId;
        private String deviceName;
        private ProxySession proxySession;

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
        public Builder proxySession(ProxySession val) {
            proxySession = val;
            return this;
        }

        public DeviceSession build() {
            return new DeviceSession(this);
        }
    }
}
