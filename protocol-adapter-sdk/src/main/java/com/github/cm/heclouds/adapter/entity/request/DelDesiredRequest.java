package com.github.cm.heclouds.adapter.entity.request;


import com.github.cm.heclouds.adapter.core.entity.OneJSONRequest;
import com.github.cm.heclouds.adapter.core.utils.GsonUtils;
import com.google.gson.JsonObject;

import java.util.List;

/**
 * 清除设备属性请求
 */
public class DelDesiredRequest extends OneJSONRequest {

    private transient List<Param> paramList;

    public DelDesiredRequest() {
    }

    public DelDesiredRequest(List<Param> paramList) {
        this.paramList = paramList;
    }

    public static DelDesiredRequest decode(String property) {
        return GsonUtils.GSON.fromJson(property, DelDesiredRequest.class);
    }

    public static DelDesiredRequest decode(byte[] property) {
        return decode(new String(property));
    }

    public List<Param> getParamList() {
        return paramList;
    }

    public void setParamList(List<Param> paramList) {
        this.paramList = paramList;
    }

    @Override
    public String toJsonString() {
        JsonObject jsonObject = new JsonObject();
        for (Param param : paramList) {
            JsonObject versionJsonObject = new JsonObject();
            if (param.getVersion() != null) {
                versionJsonObject.addProperty("version", param.getVersion());
            }
            jsonObject.add(param.getProperty(), versionJsonObject);
        }
        setParams(jsonObject);
        return GsonUtils.GSON.toJson(this);
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
