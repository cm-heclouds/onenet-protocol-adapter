package com.github.cm.heclouds.adapter.core.entity;


import com.github.cm.heclouds.adapter.core.utils.GsonUtil;
import com.google.gson.JsonObject;

/**
 * OneJSON请求
 */
public class OneJSONRequest extends Request {

    private JsonObject params;

    public OneJSONRequest() {
    }

    public OneJSONRequest(String version, JsonObject params) {
        super(version);
        this.params = params;
    }

    public OneJSONRequest(JsonObject params) {
        this.params = params;
    }

    public OneJSONRequest(String id, String version, JsonObject params) {
        super(id, version);
        this.params = params;
    }

    public static OneJSONRequest decode(String property) {
        return GsonUtil.GSON.fromJson(property, OneJSONRequest.class);
    }

    public static OneJSONRequest decode(byte[] property) {
        return decode(new String(property));
    }

    public JsonObject getParams() {
        return params;
    }

    public void setParams(JsonObject params) {
        this.params = params;
    }
}
