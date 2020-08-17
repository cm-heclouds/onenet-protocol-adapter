package com.github.cm.heclouds.adapter.config.impl;

import com.github.cm.heclouds.adapter.api.ConfigUtils;
import com.github.cm.heclouds.adapter.config.IDeviceConfig;
import com.github.cm.heclouds.adapter.core.entity.Device;
import com.github.cm.heclouds.adapter.core.logging.ILogger;
import com.github.cm.heclouds.adapter.core.logging.LoggerFormat;
import com.github.cm.heclouds.adapter.core.utils.FileConfigUtil;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import io.netty.util.internal.StringUtil;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import static com.github.cm.heclouds.adapter.core.logging.LoggerFormat.Action.RUNTIME;


/**
 * 从配置文件中读取设备的相关参数，{@link IDeviceConfig}的默认实现
 * <p>
 * 对应配置文件默认位置为resources/config/devices.conf
 */
public final class DeviceFileConfig implements IDeviceConfig {

    public static DeviceFileConfig deviceFile = null;
    private AtomicBoolean isMonitorStarted = new AtomicBoolean(false);

    private final ReentrantLock lock = new ReentrantLock();
    private final ConcurrentMap<String, Device> configCache = new ConcurrentHashMap<>(500000);
    private Config config;
    private String configFilePath = "config/devices.conf";
    private final ConcurrentMap<String, String> originalIdentityCache = new ConcurrentHashMap<>(500000);
    private ILogger logger;

    private DeviceFileConfig() {
    }

    public static DeviceFileConfig getInstance(String configFilePath) {
        if (deviceFile == null) {
            synchronized (AdapterFileConfig.class) {
                if (deviceFile == null) {
                    deviceFile = new DeviceFileConfig();
                    deviceFile.configFilePath = configFilePath;
                }
            }
        }
        return deviceFile;
    }

    public static DeviceFileConfig getInstance() {
        if (deviceFile == null) {
            synchronized (AdapterFileConfig.class) {
                if (deviceFile == null) {
                    deviceFile = new DeviceFileConfig();
                }
            }
        }
        return deviceFile;
    }

    @Override
    public Device getDeviceEntity(String originalIdentity) {
        Device device = null;
        lock.lock();
        try {
            if (StringUtil.isNullOrEmpty(originalIdentity) || !config.hasPath(originalIdentity)) {
                logger.logInnerWarn(ConfigUtils.getName(), RUNTIME, "cannot find device entity, originalIdentity=" + originalIdentity);
                return null;
            }

            device = configCache.get(originalIdentity);
            if (device != null) {
                return device;
            }

            Config deviceConfig = config.getConfig(originalIdentity);
            String productId = FileConfigUtil.getStringIfExists(deviceConfig, ConfigConsts.PRODUCT_ID);
            String deviceName = FileConfigUtil.getStringIfExists(deviceConfig, ConfigConsts.DEVICE_NAME);
            String deviceKey = FileConfigUtil.getStringIfExists(deviceConfig, ConfigConsts.DEVICE_KEY);

            if (!StringUtil.isNullOrEmpty(deviceKey) && productId != null) {
                deviceName = URLEncoder.encode(deviceName, "utf-8");
                device = Device.newBuilder()
                        .productId(productId)
                        .deviceName(deviceName)
                        .deviceKey(deviceKey)
                        .build();
                configCache.put(originalIdentity, device);
                originalIdentityCache.put(productId + "-" + deviceName, originalIdentity);
            } else {
                logger.logInnerWarn(ConfigUtils.getName(), RUNTIME, "illegal device config, productId and deviceName must be present");
            }
        } catch (Exception e) {
            logger.logInnerError(ConfigUtils.getName(), RUNTIME, "can not get device entity from device config file", e);
        } finally {
            lock.unlock();
        }

        return device;
    }

    @Override
    public String getOriginalIdentity(String productId, String deviceName) {
        lock.lock();
        try {
            return originalIdentityCache.get(productId + "-" + deviceName);
        } finally {
            lock.unlock();
        }
    }

    public void initFileDeviceConfig() {
        this.logger = ConfigUtils.getLogger();
        this.config = ConfigFactory.load(configFilePath);
        this.config.checkValid(ConfigFactory.defaultReference());
        URL fileUrl = this.getClass().getClassLoader().getResource(configFilePath);
        if (fileUrl == null) {
            logger.logInnerError(ConfigUtils.getName(), LoggerFormat.Action.LAUNCH, "device config file does not exist", null);
            System.exit(0);
        }
        String absFilePath = fileUrl.getPath();
        monitorAdapterFile(new File(absFilePath).getParent());
    }

    private synchronized void reloadFileDeviceConfig() {
        Config config;
        lock.lock();
        try {
            config = ConfigFactory.load(configFilePath);
            config.checkValid(ConfigFactory.defaultReference());
            configCache.clear();
            originalIdentityCache.clear();
            this.config = config;
            logger.logInnerInfo(ConfigUtils.getName(), RUNTIME, "reloaded device config file");
        } catch (ConfigException.Parse e) {
            logger.logInnerError(ConfigUtils.getName(), RUNTIME, "reload device config file failed", e);
        } finally {
            lock.unlock();
        }

    }

    private void monitorAdapterFile(String directoryName) {
        if (!isMonitorStarted.compareAndSet(false, true)) {
            return;
        }
        String[] split = configFilePath.split("/");
        String file = split[split.length - 1];
        // 轮询间隔1秒
        long interval = TimeUnit.SECONDS.toMillis(1);
        // 创建一个文件观察器用于处理文件的格式
        FileAlterationObserver observer = new FileAlterationObserver(directoryName, FileFilterUtils.and(
                FileFilterUtils.fileFileFilter(),
                FileFilterUtils.nameFileFilter(file)));
        //设置文件变化监听器
        observer.addListener(new FileAlterationListenerAdaptor() {
            @Override
            public void onFileChange(File file) {
                logger.logInnerInfo(ConfigUtils.getName(), RUNTIME, "detected device config file changed, reloading");
                reloadFileDeviceConfig();
            }
        });
        //创建文件变化监听器
        FileAlterationMonitor monitor = new FileAlterationMonitor(interval, observer);
        // 开始监控
        try {
            monitor.start();
            logger.logInnerInfo(ConfigUtils.getName(), LoggerFormat.Action.LAUNCH, "started monitor device config");
        } catch (Exception e) {
            logger.logInnerError(ConfigUtils.getName(), LoggerFormat.Action.LAUNCH, "start monitor device config file failed", e);
        }
    }
}
