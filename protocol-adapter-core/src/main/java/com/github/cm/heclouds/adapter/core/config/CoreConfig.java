package com.github.cm.heclouds.adapter.core.config;

import com.github.cm.heclouds.adapter.core.logging.ILogger;

/**
 * 泛协议接入核心配置
 */
public class CoreConfig {

    private final ILogger logger;

    public CoreConfig(ILogger logger) {
        this.logger = logger;
    }

    public ILogger getLogger() {
        return logger;
    }
}
