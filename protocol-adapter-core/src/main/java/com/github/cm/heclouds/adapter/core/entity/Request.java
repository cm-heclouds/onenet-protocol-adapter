package com.github.cm.heclouds.adapter.core.entity;


import com.github.cm.heclouds.adapter.core.utils.GsonUtils;
import com.github.cm.heclouds.adapter.core.utils.IdUtils;

import java.nio.charset.StandardCharsets;

/**
 * 请求基类
 */
public class Request {

    private final String id = IdUtils.generateId();
    private final String version = "1.0";

    public Request() {
    }

    public Request(String jsonString) {
    }

    public static Request decode(String property) {
        return GsonUtils.GSON.fromJson(property, Request.class);
    }

    public static Request decode(byte[] property) {
        return decode(new String(property));
    }

    public String getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public byte[] encode() {
        return toJsonString().getBytes(StandardCharsets.UTF_8);
    }

    public String toJsonString() {
        return GsonUtils.GSON.toJson(this);
    }
}
