package com.github.cm.heclouds.adapter.api;

import com.github.cm.heclouds.adapter.core.entity.*;
import com.github.cm.heclouds.adapter.entity.request.UploadPackRequest;
import com.github.cm.heclouds.adapter.entity.response.GetTopoResult;
import com.github.cm.heclouds.adapter.mqttadapter.handler.UpLinkChannelHandler;
import com.github.cm.heclouds.adapter.utils.SasTokenGenerator;
import com.google.gson.JsonObject;

import static com.github.cm.heclouds.adapter.mqttadapter.handler.UpLinkChannelHandler.UploadType;

/**
 * 子设备相关API，线程安全，可实现子设备的登陆登出、物模型功能点上报、物模型命令回复操作
 * <p>
 * 默认提供的通用API有：
 * {@link #subDeviceOnline(Device, Device)}                     子设备上线
 * {@link #subDeviceOffline(Device, Device)}                    子设备下线
 * <p>
 * {@link #replySubDevicePropertyGetRequest(Device, Response)}  响应子设备获取设备属性请求
 * {@link #replySubDevicePropertySetRequest(Device, Response)}  响应子设备获取设备属性请求
 * {@link #replySubDeviceServiceInvokeRequest(Device, Response)}响应子设备获取设备属性请求
 * <p>
 * {@link #getTopo(Device)}                                     获取子设备拓扑关系
 * {@link #addTopo(Device, Device)}                             新增子设备拓扑关系
 * {@link #deleteTopo(Device, Device)}                          删除子设备拓扑关系
 * {@link #replyTopoGetResult(Device, Response)}                响应网关拓扑关系同步结果
 * {@link #replyTopoChange(Device, Response)}                   响应网关备拓扑关系改变
 *
 * <p>
 * 子设备数据上传采用批量上送数据通道，具体参考
 * {@link AdapterApi#uploadPackData(Device, UploadPackRequest)}
 * {@link AdapterApi#uploadHistoryData(Device, UploadPackRequest)}
 */
public final class SubDeviceAdapterApi {

    /**
     * 设备数据上行处理器
     */
    private final UpLinkChannelHandler upLinkChannelHandler = UpLinkChannelHandler.INSTANCE;

    /**
     * 子设备上线
     *
     * @param device    网关设备
     * @param subDevice 子设备
     * @return 返回结果
     */
    public CallableFuture<DeviceResult> subDeviceOnline(Device device, Device subDevice) {
        return deviceOnOffline(device, subDevice, UploadType.SUB_LOGIN);
    }

    /**
     * 子设备下线
     *
     * @param device    网关设备
     * @param subDevice 子设备
     * @return 返回结果
     */
    public CallableFuture<DeviceResult> subDeviceOffline(Device device, Device subDevice) {
        return deviceOnOffline(device, subDevice, UploadType.SUB_LOGOUT);
    }

    /**
     * 响应获取子设备属性请求
     *
     * @param device   网关设备
     * @param response 响应
     * @return 消息ID
     */
    public String replySubDevicePropertyGetRequest(Device device, Response response) {
        return upLinkChannelHandler.doReply(device, response, null, UpLinkChannelHandler.ReplyType.SUB_PROPERTY_GET);
    }

    /**
     * 响应设置子设备属性请求
     *
     * @param device   网关设备
     * @param response 响应
     * @return 消息ID
     */
    public String replySubDevicePropertySetRequest(Device device, Response response) {
        return upLinkChannelHandler.doReply(device, response, null, UpLinkChannelHandler.ReplyType.SUB_PROPERTY_SET);
    }

    /**
     * 响应调用子设备服务请求
     *
     * @param device   网关设备
     * @param response 响应
     * @return 消息ID
     */
    public String replySubDeviceServiceInvokeRequest(Device device, Response response) {
        return upLinkChannelHandler.doReply(device, response, null, UpLinkChannelHandler.ReplyType.SUB_SERVICE_INVOKE);
    }

    /**
     * 新增子设备拓扑关系
     *
     * @param device    网关设备
     * @param subDevice 子设备
     * @return 返回结果
     */
    public CallableFuture<DeviceResult> addTopo(Device device, Device subDevice) {
        return topoOperation(device, subDevice, UploadType.SUB_DEV_TOPO_ADD);
    }

    /**
     * 删除子设备拓扑关系
     *
     * @param device    网关设备
     * @param subDevice 子设备
     * @return 返回结果
     */
    public CallableFuture<DeviceResult> deleteTopo(Device device, Device subDevice) {
        return topoOperation(device, subDevice, UploadType.SUB_DEV_TOPO_DELETE);
    }

    /**
     * 获取子设备拓扑关系
     *
     * @param device 网关设备
     * @return 返回结果
     */
    public CallableFuture<GetTopoResult> getTopo(Device device) {
        return upLinkChannelHandler.doGetTopo(device);
    }

    /**
     * 响应网关拓扑关系同步结果
     *
     * @param device   网关设备
     * @param response 响应
     * @return 消息ID
     */
    public String replyTopoGetResult(Device device, Response response) {
        return upLinkChannelHandler.doReply(device, response, null, UpLinkChannelHandler.ReplyType.SUB_DEV_TOPO_GET_RESULT);
    }

    /**
     * 响应子设备拓扑关系改变
     *
     * @param device   网关设备
     * @param response 响应
     * @return 消息ID
     */
    public String replyTopoChange(Device device, Response response) {
        return upLinkChannelHandler.doReply(device, response, null, UpLinkChannelHandler.ReplyType.SUB_DEV_TOPO_CHANGE_REPLY);
    }

    /**
     * 子设备上下线操作
     *
     * @param device     网关设备
     * @param subDevice  子设备
     * @param uploadType 操作类型
     * @return 响应
     */
    private CallableFuture<DeviceResult> deviceOnOffline(Device device, Device subDevice, UploadType uploadType) {
        OneJSONRequest request = new OneJSONRequest();
        JsonObject params = new JsonObject();
        params.addProperty("productID", subDevice.getProductId());
        params.addProperty("deviceName", subDevice.getDeviceName());
        request.setParams(params);
        return upLinkChannelHandler.doThingPublish(device, request, uploadType);
    }

    /**
     * 子设备Topo操作
     *
     * @param device     网关设备
     * @param subDevice  子设备
     * @param uploadType 操作类型
     * @return 响应
     */
    private CallableFuture<DeviceResult> topoOperation(Device device, Device subDevice, UploadType uploadType) {
        OneJSONRequest request = new OneJSONRequest();
        JsonObject params = new JsonObject();
        String productId = subDevice.getProductId();
        String deviceName = subDevice.getDeviceName();
        params.addProperty("productID", productId);
        params.addProperty("deviceName", deviceName);
        params.addProperty("sasToken", SasTokenGenerator.deviceSasToken(productId, deviceName, subDevice.getKey()));
        request.setParams(params);
        return upLinkChannelHandler.doUpload(device, request, uploadType);
    }
}
