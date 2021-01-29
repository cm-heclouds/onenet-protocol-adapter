package com.github.cm.heclouds.adapter.thing.schema.services;

import java.util.List;

/**
 * 服务
 */
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
     * 选填 描述 长度不超过100
     */
    private String desc;
    /**
     * 必填 调用方式
     */
    private String callType;
    /**
     * 必填 功能点（u代表自定义功能点/s代表系统功能点）
     */
    private String functionType;
    /**
     * 必填 输入参数
     */
    private List<ServiceData> input;
    /**
     * 必填 输出参数
     */
    private List<ServiceData> output;

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

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    public List<ServiceData> getInput() {
        return input;
    }

    public void setInput(List<ServiceData> input) {
        this.input = input;
    }

    public List<ServiceData> getOutput() {
        return output;
    }

    public void setOutput(List<ServiceData> output) {
        this.output = output;
    }
}
