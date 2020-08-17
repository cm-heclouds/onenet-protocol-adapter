package com.github.cm.heclouds.adapter.thing.schema;

import lombok.Data;

/**
 * 数据类型
 */
@Data
public class DataType {

    /**
     * 必填 数据类型
     */
    private String type;
    /**
     *
     */
    private Object specs;
}
