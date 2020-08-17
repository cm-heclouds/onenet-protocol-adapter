package com.github.cm.heclouds.adapter.exceptions;

/**
 * 内部异常
 */
public final class InternalException extends RuntimeException {
    public InternalException(Throwable cause) {
        super(cause);
    }

    public InternalException(String message) {
        super(message);
    }

    public InternalException(String message, Throwable cause) {
        super(message, cause);
    }
}
