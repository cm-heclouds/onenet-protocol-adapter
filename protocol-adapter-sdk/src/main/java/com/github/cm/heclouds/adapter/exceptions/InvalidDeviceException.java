package com.github.cm.heclouds.adapter.exceptions;

/**
 * 设备无效异常
 */
public final class InvalidDeviceException extends RuntimeException {
    public InvalidDeviceException(Throwable cause) {
        super(cause);
    }

    public InvalidDeviceException(String message) {
        super(message);
    }

    public InvalidDeviceException(String message, Throwable cause) {
        super(message, cause);
    }
}
