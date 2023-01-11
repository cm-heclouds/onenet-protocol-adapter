package com.github.cm.heclouds.adapter.core.logging;


import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.StringJoiner;

import static com.github.cm.heclouds.adapter.core.logging.LoggerFormat.Event.*;

public class FileLogger implements ILogger {

    private static Logger log;

    private static String configFilePath = "logback-default.xml";

    private volatile static FileLogger fileLogger = null;

    private FileLogger() {
    }

    /**
     * 返回使用指定位置的logback配置文件的FileLogger实例
     *
     * @param logFilePath logback配置文件位置
     * @return FileLogger实例
     */
    public static FileLogger getInstance(String logFilePath) {
        if (fileLogger == null) {
            synchronized (FileLogger.class) {
                if (fileLogger == null) {
                    fileLogger = new FileLogger();
                    log = LoggerFactory.getLogger(FileLogger.class);
                    loadLogbackConfig(logFilePath);
                }
            }
        }
        return fileLogger;
    }

    /**
     * 返回使用默认位置的logback配置文件的FileLogger实例
     * 如已先调用{@link FileLogger#getInstance(String)}，则返回使用指定位置的logback配置文件的FileLogger实例
     *
     * @return FileLogger实例
     */
    public static FileLogger getInstance() {
        if (fileLogger == null) {
            synchronized (FileLogger.class) {
                if (fileLogger == null) {
                    log = LoggerFactory.getLogger(FileLogger.class);
                    loadLogbackConfig(configFilePath);
                    fileLogger = new FileLogger();
                }
            }
        }
        return fileLogger;
    }


    @Override
    public void logInnerInfo(String svcName, LoggerFormat.Action action, String desc) {
        logInnerInfo(svcName, action, null, null, desc);
    }

    @Override
    public void logInnerInfo(String svcName, LoggerFormat.Action action, String svcId, String instName, String desc) {
        StringJoiner joiner = new StringJoiner(",");
        if (svcId != null) {
            joiner.add("svcId:" + svcId);
        }
        if (instName != null) {
            joiner.add("instName:" + instName);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logInfo(svcName, INNER, action, joiner.toString());
    }

    @Override
    public void logInnerWarn(String svcName, LoggerFormat.Action action, String desc) {
        logInnerWarn(svcName, action, null, null, desc);
    }

    @Override
    public void logInnerWarn(String svcName, LoggerFormat.Action action, String svcId, String instName, String desc) {
        StringJoiner joiner = new StringJoiner(",");
        if (svcId != null) {
            joiner.add("svcId:" + svcId);
        }
        if (instName != null) {
            joiner.add("instName:" + instName);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logWarn(svcName, INNER, action, joiner.toString());
    }

    @Override
    public void logInnerError(String svcName, LoggerFormat.Action action, String desc, Throwable e) {
        logInnerError(svcName, action, null, null, desc, e);
    }


    @Override
    public void logInnerError(String svcName, LoggerFormat.Action action, String svcId, String instName, String desc, Throwable e) {
        StringJoiner joiner = new StringJoiner(",");
        if (svcId != null) {
            joiner.add("svcId:" + svcId);
        }
        if (instName != null) {
            joiner.add("instName:" + instName);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logError(svcName, INNER, action, joiner.toString(), e);
    }

    @Override
    public void logProtocolHubInfo(String svcName, LoggerFormat.Action action, String desc) {
        logProtocolHubInfo(svcName, action, null, desc);
    }

    @Override
    public void logProtocolHubInfo(String svcName, LoggerFormat.Action action, String extras, String desc) {
        StringJoiner joiner = new StringJoiner(",");
        if (extras != null) {
            joiner.add(extras);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logInfo(svcName, PROTOCOL_HUB, action, joiner.toString());
    }

    @Override
    public void logProtocolHubWarn(String svcName, LoggerFormat.Action action, String desc) {
        logProtocolHubWarn(svcName, action, null, desc);
    }

    @Override
    public void logProtocolHubWarn(String svcName, LoggerFormat.Action action, String extras, String desc) {
        StringJoiner joiner = new StringJoiner(",");
        if (extras != null) {
            joiner.add(extras);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logWarn(svcName, PROTOCOL_HUB, action, joiner.toString());
    }

    @Override
    public void logProtocolHubError(String svcName, LoggerFormat.Action action, String desc, Throwable e) {
        logProtocolHubError(svcName, action, null, desc, e);
    }

    @Override
    public void logProtocolHubError(String svcName, LoggerFormat.Action action, String extras, String desc, Throwable e) {
        StringJoiner joiner = new StringJoiner(",");
        if (extras != null) {
            joiner.add(extras);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logError(svcName, PROTOCOL_HUB, action, joiner.toString(), e);
    }

    @Override
    public void logPxyConnInfo(String svcName, LoggerFormat.Action action, String desc, String proxyId) {
        logPxyConnInfo(svcName, action, desc, proxyId, null);
    }

    @Override
    public void logPxyConnInfo(String svcName, LoggerFormat.Action action, String desc, String proxyId, String extras) {
        StringJoiner joiner = new StringJoiner(",");
        if (proxyId != null) {
            joiner.add("proxyId:" + proxyId);
        }
        if (extras != null) {
            joiner.add(extras);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logInfo(svcName, GW_PROXY, action, joiner.toString());
    }

    @Override
    public void logPxyConnWarn(String svcName, LoggerFormat.Action action, String desc, String proxyId) {
        logPxyConnWarn(svcName, action, desc, proxyId, null);
    }

    @Override
    public void logPxyConnWarn(String svcName, LoggerFormat.Action action, String desc, String proxyId, String extras) {
        StringJoiner joiner = new StringJoiner(",");
        if (proxyId != null) {
            joiner.add("proxyId:" + proxyId);
        }
        if (extras != null) {
            joiner.add(extras);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logWarn(svcName, GW_PROXY, action, joiner.toString());
    }

    @Override
    public void logPxyConnError(String svcName, LoggerFormat.Action action, String desc, String proxyId, Throwable e) {
        logPxyConnError(svcName, action, desc, proxyId, null, e);
    }

    @Override
    public void logPxyConnError(String svcName, LoggerFormat.Action action, String desc, String proxyId, String extras, Throwable e) {
        StringJoiner joiner = new StringJoiner(",");
        if (proxyId != null) {
            joiner.add("proxyId:" + proxyId);
        }
        if (extras != null) {
            joiner.add(extras);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logError(svcName, GW_PROXY, action, joiner.toString(), e);
    }

    @Override
    public void logCtrlConnInfo(String svcName, LoggerFormat.Action action, String svcId, String instName, String desc) {
        logCtrlConnInfo(svcName, action, svcId, instName, desc, null);
    }

    @Override
    public void logCtrlConnInfo(String svcName, LoggerFormat.Action action, String svcId, String instName, String desc, String extras) {
        StringJoiner joiner = new StringJoiner(",");
        if (svcId != null) {
            joiner.add("svcId:" + svcId);
        }
        if (instName != null) {
            joiner.add("instName:" + instName);
        }
        if (extras != null) {
            joiner.add(extras);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logInfo(svcName, GW_CTRL, action, joiner.toString());
    }

    @Override
    public void logCtrlConnWarn(String svcName, LoggerFormat.Action action, String svcId, String instName, String desc) {
        logCtrlConnWarn(svcName, action, svcId, instName, desc, null);
    }

    @Override
    public void logCtrlConnWarn(String svcName, LoggerFormat.Action action, String svcId, String instName, String desc, String extras) {
        StringJoiner joiner = new StringJoiner(",");
        if (svcId != null) {
            joiner.add("svcId:" + svcId);
        }
        if (instName != null) {
            joiner.add("instName:" + instName);
        }
        if (extras != null) {
            joiner.add(extras);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logWarn(svcName, GW_CTRL, action, joiner.toString());
    }

    @Override
    public void logCtrlConnError(String svcName, LoggerFormat.Action action, String svcId, String instName, String desc, Throwable e) {
        logCtrlConnError(svcName, action, svcId, instName, desc, null, e);
    }

    @Override
    public void logCtrlConnError(String svcName, LoggerFormat.Action action, String svcId, String instName, String desc, String extras, Throwable e) {
        StringJoiner joiner = new StringJoiner(",");
        if (svcId != null) {
            joiner.add("svcId:" + svcId);
        }
        if (instName != null) {
            joiner.add("instName:" + instName);
        }
        if (extras != null) {
            joiner.add(extras);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logError(svcName, GW_CTRL, action, joiner.toString(), e);
    }

    @Override
    public void logDevInfo(String svcName, LoggerFormat.Action action, String pid, String devId, String desc) {
        logDevInfo(svcName, action, pid, devId, desc, null);
    }

    @Override
    public void logDevInfo(String svcName, LoggerFormat.Action action, String pid, String devId, String desc, String extras) {
        StringJoiner joiner = new StringJoiner(",");
        if (pid != null) {
            joiner.add("pid:" + pid);
        }
        if (devId != null) {
            joiner.add("devId:" + devId);
        }
        if (extras != null) {
            joiner.add(extras);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logInfo(svcName, DEV, action, joiner.toString());
    }


    @Override
    public void logDevWarn(String svcName, LoggerFormat.Action action, String pid, String devId, String desc) {
        logDevWarn(svcName, action, pid, devId, desc, null);
    }

    @Override
    public void logDevWarn(String svcName, LoggerFormat.Action action, String pid, String devId, String desc, String extras) {
        StringJoiner joiner = new StringJoiner(",");
        if (pid != null) {
            joiner.add("pid:" + pid);
        }
        if (devId != null) {
            joiner.add("devId:" + devId);
        }
        if (extras != null) {
            joiner.add(extras);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logWarn(svcName, DEV, action, joiner.toString());
    }

    @Override
    public void logDevError(String svcName, LoggerFormat.Action action, String pid, String devId, String desc, Throwable e) {
        logDevError(svcName, action, pid, devId, desc, null, e);
    }

    @Override
    public void logDevError(String svcName, LoggerFormat.Action action, String pid, String devId, String desc, String extras, Throwable e) {
        StringJoiner joiner = new StringJoiner(",");
        if (pid != null) {
            joiner.add("pid:" + pid);
        }
        if (devId != null) {
            joiner.add("devId:" + devId);
        }
        if (extras != null) {
            joiner.add(extras);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logError(svcName, DEV, action, joiner.toString(), e);
    }


    @Override
    public void logMetricInfo(String svcName, LoggerFormat.Action action, String svcId, String instName, String extras, String desc) {
        StringJoiner joiner = new StringJoiner(",");
        if (svcId != null) {
            joiner.add("svcId:" + svcId);
        }
        if (instName != null) {
            joiner.add("instName:" + instName);
        }
        if (extras != null) {
            joiner.add(extras);
        }
        if (desc != null) {
            joiner.add("desc:" + desc);
        }
        logInfo(svcName, METRIC, action, joiner.toString());
    }


    private void logInfo(String svcName, LoggerFormat.Event event, LoggerFormat.Action action, String content) {
        log.info("{} {} {} {}", svcName, event.get(), action.get(), content);
    }

    private void logWarn(String svcName, LoggerFormat.Event event, LoggerFormat.Action action, String content) {
        log.warn("{} {} {} {}", svcName, event.get(), action.get(), content);
    }

    private void logError(String svcName, LoggerFormat.Event event, LoggerFormat.Action action, String content, Throwable e) {
        if (e != null) {
            log.error("{} {} {} {} error:", svcName, event.get(), action.get(), content, e);
        } else {
            log.error("{} {} {} {}", svcName, event.get(), action.get(), content);
        }
    }

    public static void loadLogbackConfig(String logConfig) {

        try {
            logConfig = FileLogger.class.getClassLoader().getResource(logConfig).getPath();
        } catch (Exception e) {
            log.warn("Core {} {}", LoggerFormat.Action.INIT, "Log config file not find, use default config file.");
            logConfig = FileLogger.class.getClassLoader().getResource(configFilePath).getPath();
        }

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(logConfig);
        } catch (FileNotFoundException e) {
            log.error("Core {} {}", LoggerFormat.Action.INIT, "Log config file not find.");
            System.exit(1);
        }

        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(lc);
            lc.reset();
            configurator.doConfigure(inputStream);
            StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
        } catch (Exception e) {
            log.warn("Core {} {}", LoggerFormat.Action.INIT, "load log config file failed, use default config file. e:", e);
            loadLogbackConfig(configFilePath);
        }
        configFilePath = logConfig;
    }
}
