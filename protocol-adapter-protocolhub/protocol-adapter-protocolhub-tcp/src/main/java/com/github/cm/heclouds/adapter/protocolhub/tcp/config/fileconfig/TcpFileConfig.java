package com.github.cm.heclouds.adapter.protocolhub.tcp.config.fileconfig;

import com.github.cm.heclouds.adapter.core.utils.FileConfigUtils;
import com.github.cm.heclouds.adapter.protocolhub.tcp.config.ITcpConfig;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.netty.util.internal.StringUtil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 从配置文件中读取泛协议接入服务的相关参数，{@link ITcpConfig}的默认实现
 * <p>
 * 对应配置文件默认位置为resources/config/adapter.conf
 */
public final class TcpFileConfig implements ITcpConfig {

    private volatile static TcpFileConfig tcpConfig = null;
    private final ConcurrentMap<String, Object> configCache = new ConcurrentHashMap<>(100);
    private volatile Config config;
    private String configFilePath = "config/protocolhub-tcp.conf";

    private TcpFileConfig() {
    }

    public static TcpFileConfig getInstance(String configFilePath) {
        if (tcpConfig == null) {
            synchronized (TcpFileConfig.class) {
                if (tcpConfig == null) {
                    tcpConfig = new TcpFileConfig();
                    tcpConfig.configFilePath = configFilePath;
                    tcpConfig.initFileAdapterConfig();
                }
            }
        }
        return tcpConfig;
    }

    public static TcpFileConfig getInstance() {
        if (tcpConfig == null) {
            synchronized (TcpFileConfig.class) {
                if (tcpConfig == null) {
                    tcpConfig = new TcpFileConfig();
                    tcpConfig.initFileAdapterConfig();
                }
            }
        }
        return tcpConfig;
    }

    @Override
    public String getName() {
        return getString(ConfigConsts.PROTOCOL_HUB_TCP_NAME);
    }

    @Override
    public String getHost() {
        return getString(ConfigConsts.PROTOCOL_HUB_TCP_HOST);
    }

    @Override
    public Integer getPort() {
        return getInteger(ConfigConsts.PROTOCOL_HUB_TCP_PORT);
    }

    private String getString(String name) {
        if (configCache.containsKey(name)) {
            return (String) configCache.get(name);
        }

        String value = FileConfigUtils.getStringIfExists(config, name);
        if (!StringUtil.isNullOrEmpty(value)) {
            configCache.put(name, value);
        }
        return value;
    }


    private Integer getInteger(String name) {
        if (configCache.containsKey(name)) {
            return (Integer) configCache.get(name);
        }
        Integer value = FileConfigUtils.getIntegerIfExists(config, name);
        if (value != null) {
            configCache.put(name, value);
        }
        return value;
    }

    private void initFileAdapterConfig() {
        configCache.clear();
        config = ConfigFactory.load(configFilePath);
        config.checkValid(ConfigFactory.defaultReference());
    }
}
