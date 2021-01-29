package com.github.cm.heclouds.adapter.entity.request;

import com.github.cm.heclouds.adapter.core.entity.Request;
import com.github.cm.heclouds.adapter.core.utils.GsonUtils;

import java.util.List;

/**
 * 获取设备属性请求
 */
public class GetDesiredRequest extends Request {

    private List<String> params;

    public GetDesiredRequest() {
    }

    public GetDesiredRequest(List<String> params) {
        this.params = params;
    }

    public static GetDesiredRequest decode(String property) {
        return GsonUtils.GSON.fromJson(property, GetDesiredRequest.class);
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
