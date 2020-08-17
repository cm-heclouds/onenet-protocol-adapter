package com.github.cm.heclouds.adapter.protocolhub.tcp.custom;

import com.github.cm.heclouds.adapter.core.entity.Device;
import io.netty.channel.Channel;

/**
 *
 */
public interface TcpDeviceUpLinkHandler {

    /**
     * 初始化设备信息，设备连接TCP协议站后的第一条消息由此方法处理
     *
     * @param data    数据
     * @param channel 设备连接Channel
     * @return 设备
     */
    Device initDevice(Object data, Channel channel);

    /**
     * 处理自定义协议数据，在此方法中实现业务处理（数据上行），设备连接TCP协议站后除第一条以外的消息由此方法处理
     *
     * @param device  设备
     * @param data    自定义协议数据
     * @param channel 设备连接Channel
     */
    void processUpLinkData(Device device, Object data, Channel channel);

    /**
     * 处理设备断开连接时的业务
     *
     * @param device  设备
     * @param channel 设备连接Channel
     */
    void processConnectionLost(Device device, Channel channel);
}
