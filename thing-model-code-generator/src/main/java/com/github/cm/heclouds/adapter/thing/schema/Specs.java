package com.github.cm.heclouds.adapter.thing.schema;

/**
 * 详情
 */
public class Specs {

    /**
     * 参数最小值（int32、int64、float、double类型特有）
     */
    private String min;

    /**
     * 参数最大值（int32、int64、float、double类型特有）
     */
    private String max;

    /**
     * 属性单位
     */
    private String unit;

    /**
     * 步长，字符串类型
     */
    private String step;

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }
}
