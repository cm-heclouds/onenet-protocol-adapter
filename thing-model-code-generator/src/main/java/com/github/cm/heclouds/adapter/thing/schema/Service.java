package com.github.cm.heclouds.adapter.thing.schema;

import lombok.Data;

import java.util.List;

/**
 * 服务
 */
@Data
public class Service {
    /**
     * 必填 标识符
     */
    private String identifier;
    /**
     * 必填 功能名称
     */
    private String name;
    /**
     * 必填 Call类型
     */
    private String callType;
    /**
     * 必填 功能点（u代表自定义功能点/s代表系统功能点）
     */
    private String functionType;
    /**
     *
     */
    private String method;
    /**
     * 必填 输入数据类型列表
     */
    private List<ServiceData> inputData;
    /**
     * 必填 输出数据类型列表
     */
    private List<ServiceData> outputData;
    /**
     * 选填 描述 长度不超过255
     */
    private String desc;
}
