package com.github.cm.heclouds.adapter.entity.request;

import com.github.cm.heclouds.adapter.core.entity.Request;
import com.github.cm.heclouds.adapter.core.utils.GsonUtils;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * 批量上传数据请求
 */
public class UploadPackRequest extends Request {

    private List<Param> params;

    public UploadPackRequest() {
    }

    public UploadPackRequest(List<Param> params) {
        this.params = params;
    }

    public static DelDesiredRequest decode(String property) {
        return GsonUtils.GSON.fromJson(property, DelDesiredRequest.class);
    }

    public static DelDesiredRequest decode(byte[] property) {
        return decode(new String(property));
    }

    public List<Param> getParams() {
        return params;
    }

    public void setParams(List<Param> params) {
        this.params = params;
    }

    public static class Param {
        private JsonObject identity;
        private JsonObject properties;
        private JsonObject events;

        public Param() {
        }

        public Param(JsonObject identity, JsonObject properties, JsonObject events) {
            this.identity = identity;
            this.properties = properties;
            this.events = events;
        }

        public JsonObject getIdentity() {
            return identity;
        }

        public void setIdentity(JsonObject identity) {
            this.identity = identity;
        }

        public JsonObject getProperties() {
            return properties;
        }

        public void setProperties(JsonObject properties) {
            this.properties = properties;
        }

        public JsonObject getEvents() {
            return events;
        }

        public void setEvents(JsonObject events) {
            this.events = events;
        }
    }
}
