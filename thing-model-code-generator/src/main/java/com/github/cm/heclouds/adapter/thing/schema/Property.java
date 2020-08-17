package com.github.cm.heclouds.adapter.thing.schema;

import lombok.Data;

/**
 * 属性
 */
@Data
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

}
