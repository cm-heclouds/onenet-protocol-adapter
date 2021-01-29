package com.github.cm.heclouds.adapter.thing.schema;

/**
 * 常量
 */
public class Consts {

    /**
     * 功能类型
     */
    public static class FunctionType {
        /**
         * 用户自定义
         */
        public static final String USER_DEFINED = "u";
        /**
         * 系统功能点
         */
        public static final String SYSTEM_DEFINED = "s";
    }

    /**
     * 读写类型
     */
    public static class AccessMode {
        /**
         * 只读
         */
        public static final String READ = "r";
        /**
         * 读写
         */
        public static final String READ_AND_WRITE = "rw";
    }

    /**
     * 数据类型
     */
    public static class DataType {
        public static final String BOOL = "bool";
        public static final String INT32 = "int32";
        public static final String INT64 = "int64";
        public static final String FLOAT = "float";
        public static final String DOUBLE = "double";
        public static final String STRING = "string";
        public static final String ENUM = "enum";
        public static final String DATE = "date";
        public static final String BITMAP = "bitMap";
        public static final String STRUCT = "struct";
        public static final String ARRAY = "array";
    }

    /**
     * 事件类型
     */
    public static class EventType {
        /**
         * 信息
         */
        public static final String INFO = "info";
        /**
         * 告警
         */
        public static final String ALERT = "alert";
        /**
         * 故障
         */
        public static final String ERROR = "error";
    }
}
