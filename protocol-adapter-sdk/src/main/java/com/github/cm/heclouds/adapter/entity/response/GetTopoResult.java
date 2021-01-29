package com.github.cm.heclouds.adapter.entity.response;

import com.github.cm.heclouds.adapter.core.entity.ReturnCode;

/**
 * 设备响应结果
 */
public class GetTopoResult {

    private final boolean success;
    private final GetTopoResponse response;

    public GetTopoResult(boolean success, int code, String msg) {
        this.success = success;
        this.response = new GetTopoResponse("", code, msg);
    }

    public GetTopoResult(ReturnCode returnCode) {
        this.success = returnCode.getCode() == ReturnCode.SUCCESS.getCode();
        this.response = new GetTopoResponse("", returnCode.getCode(), returnCode.getMsg());
    }

    public GetTopoResult(String id, ReturnCode returnCode) {
        this.success = returnCode.getCode() == ReturnCode.SUCCESS.getCode();
        this.response = new GetTopoResponse(id, returnCode.getCode(), returnCode.getMsg());
    }

    public GetTopoResult(GetTopoResponse response) {
        this.success = response.getCode() == ReturnCode.SUCCESS.getCode();
        this.response = response;
    }

    public boolean isSuccess() {
        return success;
    }

    public GetTopoResponse getResponse() {
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
