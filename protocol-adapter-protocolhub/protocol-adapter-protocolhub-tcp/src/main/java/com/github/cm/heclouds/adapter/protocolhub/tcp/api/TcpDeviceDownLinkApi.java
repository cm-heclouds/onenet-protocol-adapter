package com.github.cm.heclouds.adapter.protocolhub.tcp.api;

import com.github.cm.heclouds.adapter.protocolhub.tcp.session.TcpDeviceSession;
import com.github.cm.heclouds.adapter.protocolhub.tcp.session.TcpDeviceSessionManager;
import com.github.cm.heclouds.adapter.core.entity.Device;
import com.github.cm.heclouds.adapter.core.logging.ILogger;
import com.github.cm.heclouds.adapter.protocolhub.tcp.config.TcpProtocolHubConfigUtils;
import io.netty.channel.Channel;

import static com.github.cm.heclouds.adapter.core.logging.LoggerFormat.Action.GW_DOWN_LINK;


/**
 * 下行消息处理
 */
public final class TcpDeviceDownLinkApi {

    private final ILogger logger = TcpProtocolHubConfigUtils.getLogger();

    /**
     * 推送消息给设备
     *
     * @param device 设备
     * @param data   推送数据
     * @return 是否发送成功，仅关心发送本身，不关心发送结果
     */
    public boolean pushToDevice(Device device, Object data) {
        String productId = device.getProductId();
        String deviceName = device.getDeviceName();
        TcpDeviceSession deviceSession = TcpDeviceSessionManager.getDeviceSession(productId, deviceName);
        // 无效设备
        if (deviceSession == null || deviceSession.getChannel() == null) {
            logger.logDevWarn(TcpProtocolHubConfigUtils.getName(), GW_DOWN_LINK, productId, deviceName, "push to device failed dut to null device session");
            return false;
        }
        Channel channel = deviceSession.getChannel();
        if (!channel.isActive()) {
            logger.logDevWarn(TcpProtocolHubConfigUtils.getName(), GW_DOWN_LINK, productId, deviceName,
                    "push to device failed due to inactive channel");
            return false;
        } else {
            channel.writeAndFlush(data);
        }

        logger.logDevInfo(TcpProtocolHubConfigUtils.getName(), GW_DOWN_LINK, productId, deviceName, "push to device");
        return true;
    }


    /**
     * 主动断开设备连接
     *
     * @param device 设备
     * @return 是否发送成功，仅关心发送本身，不关心发送结果
     */
    public boolean logoutDevice(Device device) {
        String productId = device.getProductId();
        String deviceName = device.getDeviceName();
        TcpDeviceSession deviceSession = TcpDeviceSessionManager.getDeviceSession(productId, deviceName);
        if (deviceSession == null) {
            return true;
        }
        Channel channel = deviceSession.getChannel();
        if (channel == null || !channel.isActive()) {
            return true;
        }

        channel.close();

        return false;
    }
}
