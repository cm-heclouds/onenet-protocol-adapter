package com.github.cm.heclouds.adapter.protocolhub.tcp.exceptions;

/**
 * 配置非法异常
 */
public class IllegalTcpProtocolHubConfigException extends RuntimeException {
    public IllegalTcpProtocolHubConfigException(String msg) {
        super(msg);
    }

    @Override
    public void printStackTrace() {
    }
}
