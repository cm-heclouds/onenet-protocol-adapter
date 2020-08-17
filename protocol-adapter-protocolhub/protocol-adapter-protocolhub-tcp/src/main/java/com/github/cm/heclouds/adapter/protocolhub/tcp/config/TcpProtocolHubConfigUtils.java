package com.github.cm.heclouds.adapter.protocolhub.tcp.config;

import com.github.cm.heclouds.adapter.protocolhub.tcp.custom.TcpDeviceUpLinkHandler;
import com.github.cm.heclouds.adapter.core.exceptions.IllegalConfigException;
import com.github.cm.heclouds.adapter.core.logging.ILogger;
import com.github.cm.heclouds.adapter.core.utils.CoreConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 配置工具类
 * <p>
 * 注意，调用get方法获取配置前，请首先实例化{@link TcpDeviceUpLinkHandler}类，并配置对应的配置，否则将停止服务
 */
public class TcpProtocolHubConfigUtils {

    private static final Logger log = LoggerFactory.getLogger(TcpProtocolHubConfigUtils.class);

    /**
     * 配置类
     */
    private static TcpProtocolHubConfig config;

    public static TcpProtocolHubConfig getConfig() {
        if (config == null) {
            throw new IllegalConfigException("Config is null, please init Config first");
        }
        return config;
    }

    public static void setConfig(TcpProtocolHubConfig config) {
        TcpProtocolHubConfigUtils.config = config;
    }

    /**
     * 获取TCP协议站名称
     *
     * @return TCP协议站名称
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
