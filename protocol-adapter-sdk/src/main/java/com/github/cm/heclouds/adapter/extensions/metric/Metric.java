package com.github.cm.heclouds.adapter.extensions.metric;

import com.github.cm.heclouds.adapter.api.ConfigUtils;
import com.github.cm.heclouds.adapter.config.Config;
import com.github.cm.heclouds.adapter.entity.DeviceSession;
import com.github.cm.heclouds.adapter.entity.ProxySession;
import com.github.cm.heclouds.adapter.mqttadapter.ProxySessionManager;
import com.github.cm.heclouds.adapter.core.logging.ILogger;
import com.github.cm.heclouds.adapter.mqttadapter.DeviceSessionManager;

import java.util.StringJoiner;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.cm.heclouds.adapter.core.logging.LoggerFormat.Action.RUNTIME;


/**
 * Metric
 */
public class Metric {

    public static final Metric INSTANCE = new Metric(ConfigUtils.getConfig());

    private final ILogger logger = ConfigUtils.getLogger();

    private static final String UP_FLOW_EACH_CYCLE = "upFlowEachCycle";
    private static final String DOWN_FLOW_EACH_CYCLE = "downFlowEachCycle";
    private static final String CURRENT_CONNECTION_COUNTS = "currentConnCounts";
    private static final String CURRENT_DEVICE_COUNTS = "currentDevCounts";

    private Metric() {
    }

    public Metric(Config config) {
        this.svcId = config.getServiceId();
        this.instName = config.getInstanceName();
    }

    private String svcId;
    private String instName;

    public void setSvcId(String svcId) {
        this.svcId = svcId;
    }

    public void setInstName(String instName) {
        this.instName = instName;
    }

    /**
     * 启动统计任务
     *
     * @param delay 统计周期时间数值
     * @param unit  统计周期时间单位
     */
    public void start(long delay, TimeUnit unit) {
        Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(this::stat, 0, delay, unit);
    }

    /**
     * 平台到SDK的下行流量统计/周期
     */
    private final AtomicInteger downFlowEachCycle = new AtomicInteger(0);

    /**
     * SDK到平台的上行流量统计/周期
     */
    private final AtomicInteger upFlowEachCycle = new AtomicInteger(0);

    void incrUpFlow(int bytesNum) {
        upFlowEachCycle.addAndGet(bytesNum);
    }

    void incrDownFlow(int bytesNum) {
        downFlowEachCycle.addAndGet(bytesNum);
    }


    /**
     * 获取当前SDK到平台的连接数量
     */
    private int getCurrentConnCounts() {
        ConcurrentMap<String, ProxySession> proxySessionPool = ProxySessionManager.getProxySessionPool();
        return proxySessionPool.size();
    }

    /**
     * 获取SDK当前代理的设备数量
     */
    private int getCurrentDevCounts() {
        ConcurrentMap<String, DeviceSession> devSessionPool = DeviceSessionManager.getDeviceSessionPool();
        return devSessionPool.size();
    }

    private void stat() {
        StringJoiner joiner = new StringJoiner(",");
        joiner.add(UP_FLOW_EACH_CYCLE + ":" + upFlowEachCycle.getAndSet(0));
        joiner.add(DOWN_FLOW_EACH_CYCLE + ":" + downFlowEachCycle.getAndSet(0));
        joiner.add(CURRENT_CONNECTION_COUNTS + ":" + getCurrentConnCounts());
        joiner.add(CURRENT_DEVICE_COUNTS + ":" + getCurrentDevCounts());
        logger.logMetricInfo(ConfigUtils.getName(), RUNTIME, svcId, instName, joiner.toString(), null);
    }
}
