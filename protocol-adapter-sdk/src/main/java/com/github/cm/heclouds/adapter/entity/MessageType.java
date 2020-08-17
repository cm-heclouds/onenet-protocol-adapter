package com.github.cm.heclouds.adapter.entity;

/**
 * 消息类型，用于MQTT Message (平台通信) 和 泛协议接入SDK内部消息之间的转换
 */
public enum MessageType {

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
    LOGOUT_NOTIFY_RESPONSE,

    /**
     * 代理设备上传设备属性响应
     */
    UPLOAD_PROPERTY_RESPONSE,

    /**
     * 代理设备上报设备事件响应
     */
    UPLOAD_EVENT_RESPONSE,

    /**
     * 平台下发设备设备属性
     */
    SET_THING_PROPERTY,

    /**
     * 代理设备获取期望值响应
     */
    GET_DESIRED_RESPONSE,

    /**
     * 代理设备删除期望值响应
     */
    DELETE_DESIRED_RESPONSE,

    /**
     * 未知类型
     */
    UNKNOWN
}
