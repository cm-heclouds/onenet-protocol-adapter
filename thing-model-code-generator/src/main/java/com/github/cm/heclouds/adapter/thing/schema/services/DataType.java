package com.github.cm.heclouds.adapter.thing.schema.services;

import com.github.cm.heclouds.adapter.thing.schema.Specs;

/**
 * 数据类型
 */
public class DataType {

    /**
     * 必填 数据类型
     */
    private String type;
    /**
     * 详情
     */
    private Object specs;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getSpecs() {
        return specs;
    }

    public void setSpecs(Specs specs) {
        this.specs = specs;
    }
}
