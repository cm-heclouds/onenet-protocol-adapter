package com.github.cm.heclouds.adapter.exceptions;

/**
 * 不支持的MQTT消息类型异常
 */
public final class UnsupportedMqttMessageTypeException extends RuntimeException {
    public UnsupportedMqttMessageTypeException(Throwable cause) {
        super(cause);
    }

    public UnsupportedMqttMessageTypeException(String message) {
        super(message);
    }

    public UnsupportedMqttMessageTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
