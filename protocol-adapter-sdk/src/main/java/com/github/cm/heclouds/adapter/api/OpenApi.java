package com.github.cm.heclouds.adapter.api;

import com.github.cm.heclouds.adapter.core.entity.*;
import com.github.cm.heclouds.adapter.mqttadapter.handler.UpLinkChannelHandler;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import static com.github.cm.heclouds.adapter.mqttadapter.handler.UpLinkChannelHandler.UploadType;

/**
 * 开放API，线程安全，可实现设备的登陆登出、物模型功能点上报、物模型命令回复操作
 * 可通过物模型代码生成工具的注解@ThingModelConfiguration 配置物模型功能点，
 * 自动生成{com.cmiot.adapter.api.OpenApiExtention+pid}类，包含生成设备功能点、上报设备功能点、接收平台下发功能点相关方法。
 * <p>
 * 生成方式为使用Maven Compile，生成前建议首先Clean，生成代码文件位置为 target/generated-sources/annotations
 * <p>
 * 默认提供的通用API有：
 * {@link #deviceOnline(Device)}                       设备登陆
 * {@link #deviceOffline(Device)}                               设备登出
 * {@link #uploadProperty(Device, String, String, JsonObject)}  上传设备属性
 * {@link #uploadProperty(Device, List)}
 * {@link #uploadProperty(Device, OneJSONRequest)}
 * {@link #uploadProperty(Device, String, String, List)}
 * {@link #uploadEvent(Device, String, String, JsonObject)}     上报设备事件
 * {@link #uploadEvent(Device, List)}
 * {@link #uploadEvent(Device, OneJSONRequest)}
 * {@link #uploadEvent(Device, String, String, List)}
 * {@link #replyPropertySetRequest(Device, Response)}           响应设备属性设置命令
 * {@link #getDesiredProperty(Device, String, String, List)}    获取设备属性期望值
 * {@link #getDesiredProperty(Device, GetDesiredRequest)}
 * {@link #deleteDesiredProperty(Device, String, String, List)} 清除设备属性期望值
 * {@link #deleteDesiredProperty(Device, DelDesiredRequest)}
 */
public class OpenApi {

    /**
     * 设备数据上行处理器
     */
    private final UpLinkChannelHandler upLinkChannelHandler = UpLinkChannelHandler.INSTANCE;

    /**
     * 登录设备
     *
     * @param device 设备
     */
    public void deviceOnline(Device device) {
        upLinkChannelHandler.doDeviceOnline(device);
    }

    /**
     * 登出设备
     *
     * @param device 设备
     */
    public void deviceOffline(Device device) {
        upLinkChannelHandler.doDeviceOffline(device);
    }

    /**
     * 上传设备属性
     *
     * @param device  设备
     * @param id      消息ID
     * @param version 物模型版本
     * @param params  请求参数，用户自定义，JsonObject格式
     * @return 消息ID
     */
    public String uploadProperty(Device device, String id, String version, JsonObject params) {
        return uploadProperty(device, new OneJSONRequest(id, version, params));
    }

    /**
     * 上传设备属性列表
     *
     * @param device  设备
     * @param id      消息ID
     * @param version 物模型版本
     * @param params  请求参数，用户自定义，JsonObject格式
     * @return 消息ID
     */
    public String uploadProperty(Device device, String id, String version, List<JsonObject> params) {
        JsonObject jsonObject = new JsonObject();
        for (JsonObject param : params) {
            for (Map.Entry<String, JsonElement> element : param.entrySet()) {
                jsonObject.add(element.getKey(), element.getValue());
            }
        }
        return uploadProperty(device, new OneJSONRequest(id, version, jsonObject));
    }

    /**
     * 上传设备属性
     *
     * @param device  设备
     * @param request 设备属性上传请求，格式为OneJSON请求
     * @return 消息ID
     */
    public String uploadProperty(Device device, OneJSONRequest request) {
        return doUpload(device, request, UploadType.PROPERTY);
    }

    /**
     * 上传设备属性列表
     *
     * @param device   设备
     * @param requests 设备属性上传请求列表
     * @return 消息ID
     */
    public String uploadProperty(Device device, List<OneJSONRequest> requests) {
        if (requests.size() < 1) {
            return null;
        }

        String id = requests.get(0).getId();
        String version = requests.get(0).getVersion();
        JsonObject params = new JsonObject();
        for (OneJSONRequest jsonRequest : requests) {
            JsonObject jsonObject = jsonRequest.getParams();
            for (Map.Entry<String, JsonElement> element : jsonObject.entrySet()) {
                params.add(element.getKey(), element.getValue());
            }
        }
        return doUpload(device, new OneJSONRequest(id, version, params), UploadType.PROPERTY);
    }

    /**
     * 上报设备事件
     *
     * @param device  设备
     * @param id      消息ID
     * @param version 物模型版本
     * @param params  请求参数，用户自定义，JsonObject格式
     * @return 消息ID
     */
    public String uploadEvent(Device device, String id, String version, JsonObject params) {
        return uploadEvent(device, new OneJSONRequest(id, version, params));
    }

    /**
     * 上报设备事件
     *
     * @param device  设备
     * @param id      消息ID
     * @param version 物模型版本
     * @param params  请求参数列表
     * @return 消息ID
     */
    public String uploadEvent(Device device, String id, String version, List<JsonObject> params) {
        JsonObject jsonObject = new JsonObject();
        for (JsonObject param : params) {
            for (Map.Entry<String, JsonElement> element : param.entrySet()) {
                jsonObject.add(element.getKey(), element.getValue());
            }
        }
        return uploadEvent(device, new OneJSONRequest(id, version, jsonObject));
    }

    /**
     * 上报设备事件
     *
     * @param device  设备
     * @param request 设备事件上报请求，格式为OneJSON请求
     * @return 消息ID
     */
    public String uploadEvent(Device device, OneJSONRequest request) {
        return doUpload(device, request, UploadType.EVENT);
    }


    /**
     * 上报设备事件列表
     *
     * @param device   设备
     * @param requests 设备事件上报请求列表
     * @return 消息ID
     */
    public String uploadEvent(Device device, List<OneJSONRequest> requests) {
        if (requests.size() < 1) {
            return null;
        }

        String id = requests.get(0).getId();
        String version = requests.get(0).getVersion();
        JsonObject params = new JsonObject();
        for (OneJSONRequest jsonRequest : requests) {
            JsonObject jsonObject = jsonRequest.getParams();
            for (Map.Entry<String, JsonElement> element : jsonObject.entrySet()) {
                params.add(element.getKey(), element.getValue());
            }
        }
        return doUpload(device, new OneJSONRequest(id, version, params), UploadType.EVENT);
    }


    /**
     * 响应设备属性设置命令
     *
     * @param device   设备
     * @param response 响应
     */
    public void replyPropertySetRequest(Device device, Response response) {
        upLinkChannelHandler.doReplyThingRequest(device, response.encode());
    }


    /**
     * 获取设备属性期望值
     *
     * @param device  设备
     * @param id      消息ID
     * @param version 物模型版本
     * @param params  期望获取的设备属性
     * @return 消息ID
     */
    public String getDesiredProperty(Device device, String id, String version, List<String> params) {
        return getDesiredProperty(device, new GetDesiredRequest(id, version, params));
    }

    /**
     * 获取设备属性期望值
     *
     * @param device  设备
     * @param request 获取设备属性期望值请求，格式为DesiredRequest请求
     * @return 消息ID
     */
    public String getDesiredProperty(Device device, GetDesiredRequest request) {
        return doUpload(device, request, UploadType.DESIRED_GET);
    }

    /**
     * 清除设备属性期望值
     *
     * @param device  设备
     * @param id      消息ID
     * @param version 物模型版本
     * @param params  请求参数，用户自定义，JsonObject格式
     * @return 消息ID
     */
    public String deleteDesiredProperty(Device device, String id, String version, List<DelDesiredRequest.Param> params) {
        return deleteDesiredProperty(device, new DelDesiredRequest(id, version, params));
    }

    /**
     * 清除设备属性期望值
     *
     * @param device  设备
     * @param request 获取设备属性期望值请求，格式为OneJSON请求
     * @return 消息ID
     */
    public String deleteDesiredProperty(Device device, DelDesiredRequest request) {
        return doUpload(device, request, UploadType.DESIRED_DELETE);
    }

    /**
     * 上传OneJSON数据
     *
     * @param device     设备
     * @param request    请求数据
     * @param uploadType 上传数据类型
     * @return 消息ID
     */
    private String doUpload(Device device, Request request, UploadType uploadType) {
        upLinkChannelHandler.doThingPublish(device, request.encode(), uploadType);
        return request.getId();
    }
}
