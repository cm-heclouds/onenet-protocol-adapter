package com.github.cm.heclouds.adapter.entity.sdk;

/**
 * 消息类型，用于MQTT Message (平台通信) 和 泛协议接入SDK内部消息之间的转换
 */
public enum MessageType {
    /**
     * 代理设备通用响应
     */
    COMMON_REQUEST,

    /**
     * 代理设备登陆
     */
    LOGIN_REQUEST,

    /**
     * 代理设备登出
     */
    LOGOUT_REQUEST,

    /**
     * 代理设备上传数据
     */
    UPLOAD_REQUEST,

    /**
     * 获取拓扑
     */
    GET_TOPO_REQUEST,

    /**
     * 代理设备登录响应
     */
    LOGIN_RESPONSE,

    /**
     * 代理设备登出响应
     */
    LOGOUT_RESPONSE,

    /**
     * 代理设备被平台主动登出的响应
     */
    LOGOUT_NOTIFY,

    /**
     * 代理设备上传设备属性响应
     */
    UPLOAD_PROPERTY_RESPONSE,

    /**
     * 代理设备上报设备事件响应
     */
    UPLOAD_EVENT_RESPONSE,

    /**
     * 代理设备获取期望值响应
     */
    GET_DESIRED_RESPONSE,

    /**
     * 代理设备删除期望值响应
     */
    DELETE_DESIRED_RESPONSE,

    /**
     * 平台获取代理设备属性
     */
    GET_PROPERTY_REQUEST,

    /**
     * 平台下发设备属性
     */
    SET_THING_PROPERTY_REQUEST,

    /**
     * 平台调用设备服务
     */
    INVOKE_SERVICE_REQUEST,

    /**
     * 设备批量上报数据响应
     */
    UPLOAD_PACK_DATA_RESPONSE,

    /**
     * 设备上报历史数据响应
     */
    UPLOAD_HISTORY_DATA_RESPONSE,

    /**
     * 子设备登录响应
     */
    SUB_LOGIN_RESPONSE,

    /**
     * 子设备登出响应
     */
    SUB_LOGOUT_RESPONSE,

    /**
     * 子设备新增拓扑关系
     */
    SUB_DEV_TOPO_ADD_RESPONSE,
    /**
     * 获取拓扑关系
     */
    SUB_DEV_TOPO_GET_RESPONSE,

    /**
     * 删除拓扑关系
     */
    SUB_DEV_TOPO_DELETE_RESPONSE,

    /**
     * 子设备topo变更
     */
    SUB_DEV_TOPO_CHANGE,

    /**
     * 平台设置子设备属性响应
     */
    SUB_SET_THING_PROPERTY_REQUEST,

    /**
     * 平台获取子设备属性
     */
    SUB_GET_PROPERTY_REQUEST,

    /**
     * 子设备调用设备服务
     */
    SUB_INVOKE_SERVICE_REQUEST,

    /**
     * 未知类型
     */
    UNKNOWN
}
