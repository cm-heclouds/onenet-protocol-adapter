package com.github.cm.heclouds.adapter.core.entity;


import com.github.cm.heclouds.adapter.core.utils.GsonUtil;

import java.nio.charset.Charset;

/**
 *
 */
public class Request {

    private String id = String.valueOf(System.currentTimeMillis());
    private String version = "1.0";

    public Request() {
    }

    public Request(String version) {
        this.version = version;
    }

    public Request(String id, String version) {
        if (id != null) {
            this.id = id;
        }
        if (version != null) {
            this.version = version;
        }
    }

    public static Request decode(String property) {
        return GsonUtil.GSON.fromJson(property, Request.class);
    }

    public static Request decode(byte[] property) {
        return decode(new String(property));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public byte[] encode() {
        return toJsonString().getBytes(Charset.defaultCharset());
    }

    public String toJsonString() {
        return GsonUtil.GSON.toJson(this);
    }
}
