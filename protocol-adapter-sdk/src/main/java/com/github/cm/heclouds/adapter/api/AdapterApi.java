package com.github.cm.heclouds.adapter.api;

import com.github.cm.heclouds.adapter.core.entity.Device;
import com.github.cm.heclouds.adapter.entity.request.DelDesiredRequest;
import com.github.cm.heclouds.adapter.entity.request.GetDesiredRequest;
import com.github.cm.heclouds.adapter.core.entity.OneJSONRequest;
import com.github.cm.heclouds.adapter.entity.request.UploadPackRequest;
import com.github.cm.heclouds.adapter.core.entity.CallableFuture;
import com.github.cm.heclouds.adapter.core.entity.DeviceResult;
import com.github.cm.heclouds.adapter.core.entity.Response;
import com.github.cm.heclouds.adapter.mqttadapter.handler.UpLinkChannelHandler;

import static com.github.cm.heclouds.adapter.mqttadapter.handler.UpLinkChannelHandler.UploadType;

/**
 * 开放API，线程安全，可实现设备的登陆登出、物模型功能点上报、物模型命令回复操作
 * 可通过物模型代码生成工具的注解@ThingModelConfiguration 配置物模型功能点，
 * 自动生成{com.github.cm.heclouds.adapter.api.Thing+pid}类，包含生成设备功能点、上报设备功能点、接收平台下发功能点相关方法。
 * <p>
 * 生成方式为使用Maven Compile，生成前建议首先Clean，生成代码文件位置为 target/generated-sources/annotations
 * <p>
 * 默认提供的通用API有：
 * {@link #upload(Device, String, String)}                      上传数据
 * {@link #deviceOnline(Device)}                                设备上线
 * {@link #deviceOffline(Device)}                               设备下线
 * {@link #uploadProperty(Device, OneJSONRequest)}              上传设备属性
 * {@link #uploadEvent(Device, OneJSONRequest)}                 上报设备事件
 * {@link #getDesiredProperty(Device, GetDesiredRequest)}       获取设备属性期望值
 * {@link #deleteDesiredProperty(Device, DelDesiredRequest)}    删除设备属性期望值
 * {@link #uploadPackData(Device, UploadPackRequest)}           批量上传数据
 * {@link #uploadHistoryData(Device, UploadPackRequest)}        批量上传历史数据
 * <p>
 * {@link #replyPropertyGetRequest(Device, Response)}           响应获取设备属性请求
 * {@link #replyPropertySetRequest(Device, Response)}           响应设置设备属性请求
 * {@link #replyServiceInvokeRequest(Device, Response, String)} 响应调用设备服务请求
 */
public final class AdapterApi {

    /**
     * 设备数据上行处理器
     */
    private final UpLinkChannelHandler upLinkChannelHandler = UpLinkChannelHandler.INSTANCE;

    /**
     * 设备数据通用上行
     *
     * @param device  设备
     * @param topic   主题
     * @param request OneJSON请求
     * @return 返回结果
     */
    public CallableFuture<DeviceResult> upload(Device device, String topic, String request) {
        return upLinkChannelHandler.doPublish(device, topic, request);
    }

    /**
     * 设备上线
     *
     * @param device 设备
     * @return 返回结果
     */
    public CallableFuture<DeviceResult> deviceOnline(Device device) {
        return upLinkChannelHandler.doDeviceOnline(device);
    }

    /**
     * 设备下线
     *
     * @param device 设备
     * @return 返回结果
     */
    public CallableFuture<DeviceResult> deviceOffline(Device device) {
        return upLinkChannelHandler.doDeviceOffline(device);
    }

    /**
     * 上传设备属性
     *
     * @param device  设备
     * @param request 设备属性上传请求，格式为OneJSON请求
     * @return 返回结果
     */
    public CallableFuture<DeviceResult> uploadProperty(Device device, OneJSONRequest request) {
        return upLinkChannelHandler.doUpload(device, request, UploadType.PROPERTY);
    }

    /**
     * 上报设备事件
     *
     * @param device  设备
     * @param request 设备事件上报请求，格式为OneJSON请求
     * @return 返回结果
     */
    public CallableFuture<DeviceResult> uploadEvent(Device device, OneJSONRequest request) {
        return upLinkChannelHandler.doUpload(device, request, UploadType.EVENT);
    }

    /**
     * 获取设备属性期望值
     *
     * @param device  设备
     * @param request 获取设备属性期望值请求，格式为DesiredRequest请求
     * @return 返回结果
     */
    public CallableFuture<DeviceResult> getDesiredProperty(Device device, GetDesiredRequest request) {
        return upLinkChannelHandler.doUpload(device, request, UploadType.DESIRED_GET);
    }

    /**
     * 清除设备属性期望值
     *
     * @param device  设备
     * @param request 获取设备属性期望值请求，格式为OneJSON请求
     * @return 返回结果
     */
    public CallableFuture<DeviceResult> deleteDesiredProperty(Device device, DelDesiredRequest request) {
        return upLinkChannelHandler.doUpload(device, request, UploadType.DESIRED_DELETE);
    }

    /**
     * 响应获取设备属性请求
     *
     * @param device   设备
     * @param response 响应
     * @return 消息ID
     */
    public String replyPropertyGetRequest(Device device, Response response) {
        return upLinkChannelHandler.doReply(device, response, null, UpLinkChannelHandler.ReplyType.PROPERTY_GET);
    }

    /**
     * 响应设置设备属性请求
     *
     * @param device   设备
     * @param response 响应
     * @return 消息ID
     */
    public String replyPropertySetRequest(Device device, Response response) {
        return upLinkChannelHandler.doReply(device, response, null, UpLinkChannelHandler.ReplyType.PROPERTY_SET);
    }

    /**
     * 响应调用设备服务请求
     *
     * @param device     设备
     * @param response   响应
     * @param identifier 服务标识
     * @return 消息ID
     */
    public String replyServiceInvokeRequest(Device device, Response response, String identifier) {
        return upLinkChannelHandler.doReply(device, response, identifier, UpLinkChannelHandler.ReplyType.SERVICE_INVOKE);
    }

    /**
     * 批量上传数据
     *
     * @param device  设备
     * @param request 批量上传数据请求，格式为UploadPackRequest请求
     * @return 返回结果
     */
    public CallableFuture<DeviceResult> uploadPackData(Device device, UploadPackRequest request) {
        return upLinkChannelHandler.doUpload(device, request, UploadType.PACK_DATA);
    }

    /**
     * 上传历史数据
     *
     * @param device  设备
     * @param request 上传历史数据请求，格式为UploadPackRequest请求
     * @return 返回结果
     */
    public CallableFuture<DeviceResult> uploadHistoryData(Device device, UploadPackRequest request) {
        return upLinkChannelHandler.doUpload(device, request, UploadType.HISTORY_DATA);
    }
}
