package com.github.cm.heclouds.adapter.core.exceptions;

/**
 * 配置非法异常
 */
public final class IllegalConfigException extends RuntimeException {

    public IllegalConfigException(String msg) {
        super(msg);
    }

    @Override
    public void printStackTrace() {
    }
}
