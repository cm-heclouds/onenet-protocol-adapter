package com.github.cm.heclouds.adapter.core.entity;

import com.github.cm.heclouds.adapter.core.utils.GsonUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;

/**
 * 响应
 */
public class Response {

    private String id;

    private Integer code;

    private String msg;

    private JsonObject data;

    public Response(String id, Integer code, String msg) {
        this.id = id;
        this.code = code;
        this.msg = msg;
    }

    public Response(String id, Integer code, String msg, JsonObject data) {
        this.id = id;
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static Response decode(byte[] data) {
        JsonObject jsonObject = GsonUtils.GSON.fromJson(new String(data), JsonObject.class);
        JsonObject jsonData = null;
        JsonElement msgElement = jsonObject.get("data");
        if (msgElement != null && msgElement.isJsonObject()) {
            jsonData = msgElement.getAsJsonObject();
        }

        return new Response((JsonNull.INSTANCE.equals(jsonObject.get("id")) || jsonObject.get("id") == null) ? null : jsonObject.get("id").getAsString(),
                jsonObject.get("code") == null ? null : jsonObject.get("code").getAsInt(),
                jsonObject.get("msg") == null ? null : jsonObject.get("msg").getAsString(),
                jsonData);
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

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }

    public byte[] encode() {
        return GsonUtils.GSON.toJson(this).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Response{" +
                "id='" + id + '\'' +
                ", code=" + code +
                ", msg='" + msg + '\'');
        if (data != null) {
            stringBuilder.append(", data=")
                    .append(data);
        }
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
