package com.github.cm.heclouds.adapter.core.entity;

import com.github.cm.heclouds.adapter.core.utils.GsonUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.nio.charset.Charset;

/**
 * 响应
 */
public class Response {

    private String id;

    private Integer code;

    private Object msg;

    public Response(String id, Integer code, Object msg) {
        this.id = id;
        this.code = code;
        this.msg = msg;
    }

    public static Response decode(byte[] data) {
        JsonObject jsonObject = GsonUtil.GSON.fromJson(new String(data), JsonObject.class);
        Object msg = null;
        JsonElement msgElement = jsonObject.get("msg");
        if (msgElement != null) {
            if (msgElement.isJsonPrimitive()) {
                msg = msgElement.getAsString();
            } else if (msgElement.isJsonObject()) {
                msg = msgElement.getAsJsonObject();
            } else if (msgElement.isJsonArray()) {
                msg = msgElement.getAsJsonArray();
            }
        }
        return new Response(jsonObject.get("id") == null ? null : jsonObject.get("id").getAsString(),
                jsonObject.get("code") == null ? null : jsonObject.get("code").getAsInt(),
                msg);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public byte[] encode() {
        return GsonUtil.GSON.toJson(this).getBytes(Charset.defaultCharset());
    }

    @Override
    public String toString() {
        return "Response{" +
                "id=" + id +
                ", code=" + code +
                ", msg=" + msg +
                '}';
    }
}
