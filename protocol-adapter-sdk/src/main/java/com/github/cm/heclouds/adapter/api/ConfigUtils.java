package com.github.cm.heclouds.adapter.api;

import com.github.cm.heclouds.adapter.config.Config;
import com.github.cm.heclouds.adapter.config.IDeviceConfig;
import com.github.cm.heclouds.adapter.exceptions.IllegalConfigException;
import com.github.cm.heclouds.adapter.core.logging.ILogger;
import com.github.cm.heclouds.adapter.core.utils.CoreConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 配置工具类
 * <p>
 * 注意，调用get方法获取配置前，请首先实例化{@link Config}类，并配置对应的配置，否则将停止服务
 */
public class ConfigUtils {

    private static final Logger log = LoggerFactory.getLogger(ConfigUtils.class);

    private static final ILogger LOGGER = null;

    /**
     * 配置类
     */
    private static Config config;

    private ConfigUtils() {
    }

    public static Config getConfig() {
        if (config == null) {
            throw new IllegalConfigException("Config is null, please init Config first");
        }
        return config;
    }
    public static void setConfig(Config config) {
        ConfigUtils.config = config;
    }

    /**
     * 获取SDK名称
     *
     * @return SDK名称
     */
    public static String getName() {
        try {
            return config.getName();
        } catch (Exception e) {
            log.error("cannot get name", e);
            System.exit(1);
        }
        return null;
    }

    /**
     * 获取设备配置
     *
     * @return 设备配置
     */
    public static IDeviceConfig getDeviceConfig() {
        try {
            if (config == null) {
                log.error("cannot get IDeviceConfig, please initialize 'com.github.cm.heclouds.adapter.config.Config' and configure DeviceConfig first");
                System.exit(1);
            }
            return config.getDeviceConfig();
        } catch (Exception e) {
            log.error("cannot get IDeviceConfig", e);
            System.exit(1);
        }
        return null;
    }

    /**
     * 获取ILogger扩展工具类
     *
     * @return ILogger扩展工具类
     */
    public static ILogger getLogger() {
        try {
            return CoreConfigUtils.getLogger();
        } catch (Exception e) {
            log.error("cannot get ILogger", e);
            System.exit(1);
        }
        return null;
    }
}
