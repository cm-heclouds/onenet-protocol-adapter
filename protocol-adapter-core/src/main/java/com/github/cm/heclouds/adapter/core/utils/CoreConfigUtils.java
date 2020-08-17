package com.github.cm.heclouds.adapter.core.utils;

import com.github.cm.heclouds.adapter.core.logging.LoggerFormat;
import com.github.cm.heclouds.adapter.core.config.CoreConfig;
import com.github.cm.heclouds.adapter.core.logging.ILogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 泛协议接入核心配置工具类
 */
public class CoreConfigUtils {

    private static final Logger LOG = LoggerFactory.getLogger(CoreConfigUtils.class);
    private static ILogger logger;
    private static CoreConfig coreConfig = null;

    private CoreConfigUtils() {
    }

    public static CoreConfig getCoreConfig() {
        return coreConfig;
    }

    public static synchronized void setCoreConfig(CoreConfig coreConfig) {
        CoreConfigUtils.coreConfig = coreConfig;
        logger = coreConfig.getLogger();
    }

    public static ILogger getLogger() {
        if (coreConfig == null) {
            LOG.error("{} {}", LoggerFormat.Action.INIT, "please configure ILogger and instantiate Config first");
            System.exit(0);
        }
        return logger;
    }


}
