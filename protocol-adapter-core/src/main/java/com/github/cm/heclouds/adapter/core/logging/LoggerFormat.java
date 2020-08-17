package com.github.cm.heclouds.adapter.core.logging;


public class LoggerFormat {
    public enum SvcName {
        // SDK
        SDK("SDK");

        String str;

        SvcName(String str) {
            this.str = str;
        }

        public String get() {
            return str;
        }
    }

    public enum Event {
        //内部业务事件
        INNER("Inner"),
        //协议中心相关事件
        PROTOCOL_HUB("ProtocolHub"),
        //设备相关事件
        DEV("Dev"),
        //SDK控制连接相关事件
        GW_CTRL("Ctrl"),
        //SDK代理连接相关事件
        GW_PROXY("Pxy"),
        //监控相关事件
        METRIC("Metric");

        String str;

        Event(String str) {
            this.str = str;
        }

        public String get() {
            return str;
        }

    }

    public enum Action {
        //
        LAUNCH("Launch"),
        SHUTDOWN("Shutdown"),
        RUNTIME("Runtime"),

        INIT("Init"),

        DISCONNECT("Disconnect"),
        //平台下行数据
        PLATFORM_DOWN_LINK("PlatformDownLink"),
        //SDK下行数据
        GW_DOWN_LINK("SDKDownLink"),
        //网关上行数据
        GW_UP_LINK("SDKUpLink"),
        //设备上行数据
        DEV_UP_LINK("DevUpLink"),
        LOGOUT("Logout"),
        LOGIN("Login");


        String str;

        Action(String str) {
            this.str = str;
        }

        public String get() {
            return str;
        }
    }


}
