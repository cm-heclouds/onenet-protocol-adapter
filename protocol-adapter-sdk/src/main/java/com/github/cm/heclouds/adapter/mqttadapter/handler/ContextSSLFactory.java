package com.github.cm.heclouds.adapter.mqttadapter.handler;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;

import javax.net.ssl.SSLException;
import java.io.InputStream;

/**
 * 初始化sslcontext类
 */
public class ContextSSLFactory {

    private final static SslContext SSL_CONTEXT_S;

    static {
        SslContext sslContext = null;
        InputStream resourceAsStream = ContextSSLFactory.class.getResourceAsStream("/serverCert.pem");
        try {
            sslContext = SslContextBuilder.forClient().trustManager(resourceAsStream).build();
        } catch (SSLException e) {
            e.printStackTrace();
        }
        SSL_CONTEXT_S = sslContext;
    }

    public static SslContext getSslContext() {
        return SSL_CONTEXT_S;
    }

}
