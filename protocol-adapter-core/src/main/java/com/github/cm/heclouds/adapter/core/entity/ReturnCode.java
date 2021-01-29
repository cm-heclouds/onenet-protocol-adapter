package com.github.cm.heclouds.adapter.core.entity;

/**
 * 返回Code
 */
public enum ReturnCode {

    /**
     * 成功
     */
    SUCCESS(200, "success"),

    /**
     * 设备不在线
     */
    DEVICE_NOT_ONLINE(1024, "device not online"),

    /**
     * 非法数据
     */
    ILLEGAL_DATA(1051, "illegal data"),

    /**
     * 子设备用户名或密码错误
     */
    BAD_NAME_PASSWORD(1052, "bad user name or password"),

    /**
     * 服务不可用
     */
    SERVICE_UNAVAILABLE(1053, "server unavailable"),

    /**
     * 设备已被禁用
     */
    BANNED(1054, "device banned"),

    /**
     * 超过允许登录的代理设备数量
     */
    DEVICE_NUM_LIMIT(1055, "maximum proxied device number exceeded"),

    /**
     * 连接频率过快
     */
    CONNECTION_RATE_EXCEEDED(1056, "connection rate exceeded"),

    /**
     * 因设备key更新离线子设备
     */
    DEVICE_KEY_UPDATED(1057, "device key updated"),

    /**
     * 子设备重复登录
     */
    DUP_LOGIN(1058, "duplicated login"),

    /**
     * 子设备协议错误
     */
    PROTOCOL_ERR(1059, "protocol error"),

    /**
     * 子设备被删除
     */
    DEVICE_DELETED(1060, "device deleted"),

    /**
     * 协议类型不匹配
     */
    PROTOCOL_NOT_MATCH(1061, "protocol not match"),

    /**
     * SDK内部错误
     */
    SDK_INTERVAL_ERROR(1090, "SDK interval error"),

    /**
     * SDK内部错误
     */
    PROXY_DISCONNECTED(1091, "proxy connection disconnected");

    private final int code;
    private final String msg;

    ReturnCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
