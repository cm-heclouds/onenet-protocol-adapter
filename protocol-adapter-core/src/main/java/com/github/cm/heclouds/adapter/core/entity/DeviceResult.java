package com.github.cm.heclouds.adapter.core.entity;

/**
 * 设备响应结果
 */
public class DeviceResult {

    private final boolean success;
    private final Response response;

    public DeviceResult(boolean success, int code, String msg) {
        this.success = success;
        this.response = new Response("", code, msg);
    }

    public DeviceResult(ReturnCode returnCode) {
        this.success = returnCode.getCode() == ReturnCode.SUCCESS.getCode();
        this.response = new Response("", returnCode.getCode(), returnCode.getMsg());
    }

    public DeviceResult(String id, ReturnCode returnCode) {
        this.success = returnCode.getCode() == ReturnCode.SUCCESS.getCode();
        this.response = new Response(id, returnCode.getCode(), returnCode.getMsg());
    }

    public DeviceResult(Response response) {
        this.success = response.getCode() == ReturnCode.SUCCESS.getCode();
        this.response = response;
    }

    public boolean isSuccess() {
        return success;
    }

    public Response getResponse() {
        return response;
    }

    @Override
    public String toString() {
        return "DeviceResult{" +
                "success=" + success +
                ", response=" + response +
                '}';
    }
}
