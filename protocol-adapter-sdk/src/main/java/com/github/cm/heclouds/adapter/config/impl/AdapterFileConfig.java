package com.github.cm.heclouds.adapter.config.impl;

import com.github.cm.heclouds.adapter.config.IAdapterConfig;
import com.github.cm.heclouds.adapter.core.utils.FileConfigUtil;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.netty.util.internal.StringUtil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 从配置文件中读取泛协议接入服务的相关参数，{@link IAdapterConfig}的默认实现
 * <p>
 * 对应配置文件默认位置为resources/config/adapter.conf
 */
public final class AdapterFileConfig implements IAdapterConfig {

    private volatile Config config;

    private String configFilePath = "config/adapter.conf";

    private volatile static AdapterFileConfig adapterFileConfig = null;
    private final ConcurrentMap<String, Object> configCache = new ConcurrentHashMap<>(100);

    private AdapterFileConfig() {
    }

    public static AdapterFileConfig getInstance(String configFilePath) {
        if (adapterFileConfig == null) {
            synchronized (AdapterFileConfig.class) {
                if (adapterFileConfig == null) {
                    adapterFileConfig = new AdapterFileConfig();
                    adapterFileConfig.configFilePath = configFilePath;
                    adapterFileConfig.initFileAdapterConfig();
                }
            }
        }
        return adapterFileConfig;
    }

    public static AdapterFileConfig getInstance() {
        if (adapterFileConfig == null) {
            synchronized (AdapterFileConfig.class) {
                if (adapterFileConfig == null) {
                    adapterFileConfig = new AdapterFileConfig();
                    adapterFileConfig.initFileAdapterConfig();
                }
            }
        }
        return adapterFileConfig;
    }

    @Override
    public String getName() {
        return getString(ConfigConsts.NAME);
    }

    @Override
    public String getConnectionHost() {
        return getString(ConfigConsts.CONNECTION_HOST);
    }

    @Override
    public String getServiceId() {
        return getString(ConfigConsts.ADAPTER_SERVICE_ID);
    }

    @Override
    public String getInstanceName() {
        return getString(ConfigConsts.ADAPTER_INSTANCE_NAME);
    }

    @Override
    public String getInstanceKey() {
        return getString(ConfigConsts.ADAPTER_INSTANCE_KEY);
    }

    @Override
    public Boolean tlsSupport() {
        return getBoolean(ConfigConsts.ADAPTER_TLS_SUPPORT);
    }

    @Override
    public Boolean ctrlReconnect() {
        return getBoolean(ConfigConsts.CTRL_RECONNECT);
    }

    @Override
    public Long getCtrlReconnectInterval() {
        return getLong(ConfigConsts.CTRL_RECONNECT_INTERVAL);
    }

    private String getString(String name) {
        if (configCache.containsKey(name)) {
            return (String) configCache.get(name);
        }

        String value = FileConfigUtil.getStringIfExists(config, name);
        if (!StringUtil.isNullOrEmpty(value)) {
            configCache.put(name, value);
        }
        return value;
    }

    private Boolean getBoolean(String name) {
        if (configCache.containsKey(name)) {
            return (Boolean) configCache.get(name);
        }
        Boolean value = FileConfigUtil.getBooleanIfExists(config, name);
        if (value != null) {
            configCache.put(name, value);
        }
        return value;
    }

    private Integer getInteger(String name) {
        if (configCache.containsKey(name)) {
            return (Integer) configCache.get(name);
        }
        Integer value = FileConfigUtil.getIntegerIfExists(config, name);
        if (value != null) {
            configCache.put(name, value);
        }
        return value;
    }

    private Long getLong(String name) {
        if (configCache.containsKey(name)) {
            return (Long) configCache.get(name);
        }
        Long value = FileConfigUtil.getLongIfExists(config, name);
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
