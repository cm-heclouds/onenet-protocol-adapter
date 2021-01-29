package com.github.cm.heclouds.adapter.thing.schema.services;

/**
 *
 */
public class ServiceData {
    /**
     * 必填 标识符
     */
    private String identifier;
    /**
     * 必填 功能名称
     */
    private String name;
    /**
     * 必填 数据类型列表
     */
    private DataType dataType;

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

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }
}
