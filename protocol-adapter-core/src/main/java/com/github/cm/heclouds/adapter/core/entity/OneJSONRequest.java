package com.github.cm.heclouds.adapter.core.entity;


import com.github.cm.heclouds.adapter.core.utils.GsonUtils;
import com.google.gson.JsonObject;

/**
 * OneJSON请求
 */
public class OneJSONRequest extends Request {

    private JsonObject params;

    public OneJSONRequest() {
    }

    public OneJSONRequest(JsonObject params) {
        this.params = params;
    }


    public static OneJSONRequest decode(String property) {
        return GsonUtils.GSON.fromJson(property, OneJSONRequest.class);
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
