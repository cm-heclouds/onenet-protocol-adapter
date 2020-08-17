package com.github.cm.heclouds.adapter.core.entity;

/**
 * 平台设备实体类，对应平台注册设备
 */
public final class Device {

    /**
     * 产品ID
     */
    private String productId;

    /**
     * 设备Key，登录时不可为空
     * 当设备为子设备时，设备key为注册码
     */
    private String deviceName;

    /**
     * 设备Key，登录时不可为空
     */
    private String deviceKey;

    private Device(Builder builder) {
        setProductId(builder.productId);
        setDeviceName(builder.deviceName);
        setDeviceKey(builder.deviceKey);
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

    public String getDeviceKey() {
        return deviceKey;
    }

    public void setDeviceKey(String deviceKey) {
        this.deviceKey = deviceKey;
    }

    public static final class Builder {
        private String productId;
        private String deviceName;
        private String deviceKey;

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

        public Builder deviceKey(String val) {
            deviceKey = val;
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
}
