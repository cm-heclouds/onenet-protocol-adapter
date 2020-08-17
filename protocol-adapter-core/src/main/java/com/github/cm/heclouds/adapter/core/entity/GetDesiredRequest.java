package com.github.cm.heclouds.adapter.core.entity;

import com.github.cm.heclouds.adapter.core.utils.GsonUtil;

import java.util.List;

/**
 * 获取设备属性请求
 */
public class GetDesiredRequest extends Request {

    private List<String> params;

    public GetDesiredRequest() {
    }

    public GetDesiredRequest(String version, List<String> params) {
        super(version);
        this.params = params;
    }

    public GetDesiredRequest(List<String> params) {
        this.params = params;
    }

    public GetDesiredRequest(String id, String version, List<String> params) {
        super(id, version);
        this.params = params;
    }

    public static GetDesiredRequest decode(String property) {
        return GsonUtil.GSON.fromJson(property, GetDesiredRequest.class);
    }

    public static GetDesiredRequest decode(byte[] property) {
        return decode(new String(property));
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }
}
