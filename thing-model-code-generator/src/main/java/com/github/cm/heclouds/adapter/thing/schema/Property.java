package com.github.cm.heclouds.adapter.thing.schema;

import com.github.cm.heclouds.adapter.thing.schema.services.DataType;

/**
 * 属性
 */
public class Property {
    /**
     * 必填 标识符
     */
    private String identifier;
    /**
     * 必填 功能名称
     */
    private String name;
    /**
     * 必填 读写属性
     */
    private String accessMode;
    /**
     * 必填 功能点（u代表自定义功能点/s代表系统功能点）
     */
    private String functionType;
    /**
     * 必填 数据类型
     */
    private DataType dataType;
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

    public String getAccessMode() {
        return accessMode;
    }

    public void setAccessMode(String accessMode) {
        this.accessMode = accessMode;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
