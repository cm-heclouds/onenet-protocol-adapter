package com.github.cm.heclouds.adapter.api;

/**
 * 开放API工具类
 */
public class OpenApiUtils {

    private OpenApiUtils() {
    }

    /**
     * 获取默认消息id号，使用当前系统毫秒数的字符串形式
     *
     * @return 默认消息id号
     */
    public static String getDefaultId() {
        return String.valueOf(System.currentTimeMillis());
    }


    /**
     * 获取默认物模型版本号，默认为"1.0"
     *
     * @return 认物模型版本号
     */
    public static String getDefaultVersion() {
        return "1.0";
    }
}
