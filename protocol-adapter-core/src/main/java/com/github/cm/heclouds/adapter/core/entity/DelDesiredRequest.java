package com.github.cm.heclouds.adapter.core.entity;


import com.github.cm.heclouds.adapter.core.utils.GsonUtil;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * 清除设备属性请求
 */
public class DelDesiredRequest extends Request {

    private List<Param> params;

    public DelDesiredRequest() {
    }

    public DelDesiredRequest(String version, List<Param> params) {
        super(version);
        this.params = params;
    }

    public DelDesiredRequest(List<Param> params) {
        this.params = params;
    }

    public DelDesiredRequest(String id, String version, List<Param> params) {
        super(id, version);
        this.params = params;
    }

    public static DelDesiredRequest decode(String property) {
        return GsonUtil.GSON.fromJson(property, DelDesiredRequest.class);
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

    @Override
    public String toJsonString() {
        JsonObject jsonObject = new JsonObject();
        for (Param param : params) {
            JsonObject versionJsonObject = new JsonObject();
            if (param.getVersion() != null) {
                versionJsonObject.addProperty("version", param.getVersion());
            }
            jsonObject.add(param.getProperty(), versionJsonObject);
        }
        return new OneJSONRequest(getId(), getVersion(), jsonObject).toJsonString();
    }

    public static class Param {
        private String property;
        private Integer version;

        public Param(String property) {
            this.property = property;
        }

        public Param(String property, Integer version) {
            this.property = property;
            this.version = version;
        }

        public String getProperty() {
            return property;
        }

        public void setProperty(String property) {
            this.property = property;
        }

        public Integer getVersion() {
            return version;
        }

        public void setVersion(Integer version) {
            this.version = version;
        }
    }
}
