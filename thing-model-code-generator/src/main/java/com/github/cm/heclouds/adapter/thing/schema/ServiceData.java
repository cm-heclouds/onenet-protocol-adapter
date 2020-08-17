package com.github.cm.heclouds.adapter.thing.schema;

import lombok.Data;

import java.util.List;

/**
 *
 */
@Data
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
    private List<DataType> dataType;
}
