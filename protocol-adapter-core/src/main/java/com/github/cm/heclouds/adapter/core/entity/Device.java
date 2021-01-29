package com.github.cm.heclouds.adapter.core.entity;

import java.util.Objects;

/**
 * 平台设备实体类，对应平台注册设备
 */
public final class Device {

    /**
     * 产品ID
     */
    private String productId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备key/产品key，登录时不可为空
     */
    private String key;

    private Device(Builder builder) {
        setProductId(builder.productId);
        setDeviceName(builder.deviceName);
        setKey(builder.key);
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public static final class Builder {
        private String productId;
        private String deviceName;
        private String key;

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

        public Builder key(String val) {
            key = val;
            return this;
        }

        public Device build() {
            return new Device(this);
        }
    }

    @Override
    public String toString() {
        return "Device{" +
                "productId='" + productId + '\'' +
                ", deviceName='" + deviceName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Device device = (Device) o;
        return Objects.equals(productId, device.productId) &&
                Objects.equals(deviceName, device.deviceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, deviceName);
    }
}
