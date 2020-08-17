package com.github.cm.heclouds.adapter.exceptions;

/**
 * MQTT Topic无效异常
 */
public final class InvalidMqttTopicException extends RuntimeException {
    public InvalidMqttTopicException(Throwable cause) {
        super(cause);
    }

    public InvalidMqttTopicException(String message) {
        super(message);
    }

    public InvalidMqttTopicException(String message, Throwable cause) {
        super(message, cause);
    }
}
