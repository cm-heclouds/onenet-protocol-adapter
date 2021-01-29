package com.github.cm.heclouds.adapter.thing.schema;


import java.util.List;

/**
 * 事件
 */
public class Event {
    /**
     * 必填 标识符
     */
    private String identifier;
    /**
     * 必填 功能名称
     */
    private String name;
    /**
     * 必填 事件类型
     */
    private String eventType;
    /**
     * 必填 功能点（u代表自定义功能点/s代表系统功能点）
     */
    private String functionType;
    /**
     * 必填 输出数据类型列表
     */
    private List<Property> outputData;
    /**
     * 选填 描述 长度不超过255
     */
    private String desc;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    public List<Property> getOutputData() {
        return outputData;
    }

    public void setOutputData(List<Property> outputData) {
        this.outputData = outputData;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
