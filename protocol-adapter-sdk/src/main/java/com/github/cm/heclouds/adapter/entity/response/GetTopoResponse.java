package com.github.cm.heclouds.adapter.entity.response;

import com.github.cm.heclouds.adapter.core.utils.GsonUtils;

import java.util.List;

/**
 * 获取子设备拓扑关系响应
 */
public class GetTopoResponse {

    private String id;

    private Integer code;

    private String msg;

    private List<DevInfo> data;

    public GetTopoResponse(String id, Integer code, String msg) {
        this.id = id;
        this.code = code;
        this.msg = msg;
    }

    public GetTopoResponse(String id, Integer code, String msg, List<DevInfo> data) {
        this.id = id;
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static GetTopoResponse decode(byte[] data) {
        return GsonUtils.GSON.fromJson(new String(data), GetTopoResponse.class);
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

	public List getData() {
		return data;
	}

	public void setData(List data) {
		this.data = data;
	}

	static class DevInfo {
		String deviceName;
		String productID;

		public DevInfo(String deviceName, String productID) {
			this.deviceName = deviceName;
			this.productID = productID;
		}

		public String getDeviceName() {
			return deviceName;
		}

		public void setDeviceName(String deviceName) {
			this.deviceName = deviceName;
		}

		public String getProductID() {
			return productID;
		}

		public void setProductID(String productID) {
			this.productID = productID;
		}

		@Override
		public String toString() {
			return "DevInfo{" +
					"deviceName='" + deviceName + '\'' +
					", productID='" + productID + '\'' +
					'}';
		}
	}

	@Override
	public String toString() {
		return "GetSubTopoResponse{" +
				"id='" + id + '\'' +
				", code=" + code +
				", msg='" + msg + '\'' +
				", data=" + data +
				'}';
	}
}
