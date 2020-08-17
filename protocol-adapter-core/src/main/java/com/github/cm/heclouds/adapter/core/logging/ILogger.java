package com.github.cm.heclouds.adapter.core.logging;


public interface ILogger {
    /**
     * Inner事件info日志
     *
     * @param svcName 服务名称
     * @param action  行为
     * @param desc    描述
     */
    void logInnerInfo(String svcName, LoggerFormat.Action action, String desc);

    /**
     * Inner事件info日志
     *
     * @param svcName  服务名称
     * @param action   行为
     * @param svcId    适配服务id
     * @param instName 适配服务实例名
     * @param desc     描述
     */
    void logInnerInfo(String svcName, LoggerFormat.Action action, String svcId, String instName, String desc);

    /**
     * Inner事件warn日志
     *
     * @param svcName 服务名称
     * @param action  行为
     * @param desc    描述
     */
    void logInnerWarn(String svcName, LoggerFormat.Action action, String desc);

    /**
     * Inner事件warn日志
     *
     * @param svcName  服务名称
     * @param action   行为
     * @param svcId    适配服务id
     * @param instName 适配服务实例名
     * @param desc     描述
     */
    void logInnerWarn(String svcName, LoggerFormat.Action action, String svcId, String instName, String desc);

    /**
     * Inner事件error日志
     *
     * @param svcName 服务名称
     * @param action  行为
     * @param desc    描述
     * @param e       e
     */
    void logInnerError(String svcName, LoggerFormat.Action action, String desc, Throwable e);

    /**
     * Inner事件error日志
     *
     * @param svcName  服务名称
     * @param action   行为
     * @param svcId    适配服务id
     * @param instName 适配服务实例名
     * @param desc     描述
     * @param e        e
     */
    void logInnerError(String svcName, LoggerFormat.Action action, String svcId, String instName, String desc, Throwable e);

    /**
     * ProtocoHub事件info日志
     *
     * @param svcName 服务名称
     * @param action  行为
     * @param desc    描述
     */
    void logProtocolHubInfo(String svcName, LoggerFormat.Action action, String desc);

    /**
     * ProtocoHub事件info日志
     *
     * @param svcName 服务名称
     * @param action  行为
     * @param extras  附加信息，以key:value,key:value的形式描述
     * @param desc    描述
     */
    void logProtocolHubInfo(String svcName, LoggerFormat.Action action, String extras, String desc);

    /**
     * ProtocoHub事件warn日志
     *
     * @param svcName 服务名称
     * @param action  行为
     * @param desc    描述
     */
    void logProtocolHubWarn(String svcName, LoggerFormat.Action action, String desc);

    /**
     * ProtocoHub事件warn日志
     *
     * @param svcName 服务名称
     * @param action  行为
     * @param extras  附加信息，以key:value,key:value的形式描述
     * @param desc    描述
     */
    void logProtocolHubWarn(String svcName, LoggerFormat.Action action, String extras, String desc);

    /**
     * ProtocoHub事件error日志
     *
     * @param svcName 服务名称
     * @param action  行为
     * @param desc    描述
     * @param e       e
     */
    void logProtocolHubError(String svcName, LoggerFormat.Action action, String desc, Throwable e);

    /**
     * ProtocoHub事件error日志
     *
     * @param svcName 服务名称
     * @param action  行为
     * @param extras  附加信息，以key:value,key:value的形式描述
     * @param desc    描述
     * @param e       e
     */
    void logProtocolHubError(String svcName, LoggerFormat.Action action, String extras, String desc, Throwable e);

    /**
     * PxyConn事件info日志
     *
     * @param action  行为
     * @param desc    描述
     * @param proxyId 代理连接接入mqtt接入机的clientId
     */
    void logPxyConnInfo(String svcName, LoggerFormat.Action action, String desc, String proxyId);

    /**
     * PxyConn事件info日志
     *
     * @param action  行为
     * @param desc    描述
     * @param proxyId 代理连接接入mqtt接入机的clientId
     * @param extras  附加信息，以key:value,key:value的形式描述
     */
    void logPxyConnInfo(String svcName, LoggerFormat.Action action, String desc, String proxyId, String extras);

    /**
     * PxyConn事件warn日志
     *
     * @param action  行为
     * @param desc    描述
     * @param proxyId 代理连接接入mqtt接入机的clientId
     */
    void logPxyConnWarn(String svcName, LoggerFormat.Action action, String desc, String proxyId);

    /**
     * PxyConn事件warn日志
     *
     * @param action  行为
     * @param desc    描述
     * @param proxyId 代理连接接入mqtt接入机的clientId
     * @param extras  附加信息，以key:value,key:value的形式描述
     */
    void logPxyConnWarn(String svcName, LoggerFormat.Action action, String desc, String proxyId, String extras);

    /**
     * PxyConn事件error日志
     *
     * @param action  行为
     * @param desc    描述
     * @param proxyId 代理连接接入mqtt接入机的clientId
     * @param e       e
     */
    void logPxyConnError(String svcName, LoggerFormat.Action action, String desc, String proxyId, Throwable e);

    /**
     * PxyConn事件error日志
     *
     * @param action  行为
     * @param desc    描述
     * @param proxyId 代理连接接入mqtt接入机的clientId
     * @param extras  附加信息，以key:value,key:value的形式描述
     * @param e       e
     */
    void logPxyConnError(String svcName, LoggerFormat.Action action, String desc, String proxyId, String extras, Throwable e);

    /**
     * CtrlConn事件info日志
     *
     * @param action   行为
     * @param svcId    适配服务id
     * @param instName 适配服务实例名
     * @param desc     描述
     */
    void logCtrlConnInfo(String svcName, LoggerFormat.Action action, String svcId, String instName, String desc);

    /**
     * CtrlConn事件info日志
     *
     * @param action   行为
     * @param svcId    适配服务id
     * @param instName 适配服务实例名
     * @param desc     描述
     * @param extras   附加信息，以key:value,key:value的形式描述
     */
    void logCtrlConnInfo(String svcName, LoggerFormat.Action action, String svcId, String instName, String desc, String extras);

    /**
     * CtrlConn事件warn日志
     *
     * @param action   行为
     * @param svcId    适配服务id
     * @param instName 适配服务实例名
     * @param desc     描述
     */
    void logCtrlConnWarn(String svcName, LoggerFormat.Action action, String svcId, String instName, String desc);

    /**
     * CtrlConn事件warn日志
     *
     * @param action   行为
     * @param svcId    适配服务id
     * @param instName 适配服务实例名
     * @param desc     描述
     * @param extras   附加信息，以key:value,key:value的形式描述
     */
    void logCtrlConnWarn(String svcName, LoggerFormat.Action action, String svcId, String instName, String desc, String extras);

    /**
     * CtrlConn事件error日志
     *
     * @param action   行为
     * @param svcId    适配服务id
     * @param instName 适配服务实例名
     * @param desc     描述
     * @param e        e
     */
    void logCtrlConnError(String svcName, LoggerFormat.Action action, String svcId, String instName, String desc, Throwable e);

    /**
     * CtrlConn事件error日志
     *
     * @param action   行为
     * @param svcId    适配服务id
     * @param instName 适配服务实例名
     * @param desc     描述
     * @param extras   附加信息，以key:value,key:value的形式描述
     * @param e        e
     */
    void logCtrlConnError(String svcName, LoggerFormat.Action action, String svcId, String instName, String desc, String extras, Throwable e);

    /**
     * Dev事件info日志
     *
     * @param svcName 服务名称
     * @param action  行为
     * @param pid     产品id
     * @param devId   设备名称
     * @param desc    描述
     */
    void logDevInfo(String svcName, LoggerFormat.Action action, String pid, String devId, String desc);

    /**
     * Dev事件info日志
     *
     * @param svcName 服务名称
     * @param action  行为
     * @param pid     产品id
     * @param devId   设备名称
     * @param desc    描述
     * @param extras  附加信息，以key:value,key:value的形式描述
     */
    void logDevInfo(String svcName, LoggerFormat.Action action, String pid, String devId, String desc, String extras);

    /**
     * Dev事件warn日志
     *
     * @param svcName 服务名称
     * @param action  行为
     * @param pid     产品id
     * @param devId   设备名称
     * @param desc    描述
     */
    void logDevWarn(String svcName, LoggerFormat.Action action, String pid, String devId, String desc);

    /**
     * Dev事件warn日志
     *
     * @param svcName 服务名称
     * @param action  行为
     * @param pid     产品id
     * @param devId   设备名称
     * @param desc    描述
     * @param extras  附加信息，以key:value,key:value的形式描述
     */
    void logDevWarn(String svcName, LoggerFormat.Action action, String pid, String devId, String desc, String extras);

    /**
     * Dev事件error日志
     *
     * @param svcName 服务名称
     * @param action  行为
     * @param pid     产品id
     * @param devId   设备名称
     * @param desc    描述
     * @param e       e
     */
    void logDevError(String svcName, LoggerFormat.Action action, String pid, String devId, String desc, Throwable e);

    /**
     * Dev事件error日志
     *
     * @param svcName 服务名称
     * @param action  行为
     * @param pid     产品id
     * @param devId   设备名称
     * @param desc    描述
     * @param extras  附加信息，以key:value,key:value的形式描述
     * @param e       e
     */
    void logDevError(String svcName, LoggerFormat.Action action, String pid, String devId, String desc, String extras, Throwable e);


    /**
     * Metric事件info日志
     *
     * @param action   行为
     * @param svcId    适配服务id
     * @param instName 适配服务实例名
     * @param extras   附加信息，以key:value,key:value的形式描述
     * @param desc     描述
     */
    void logMetricInfo(String svcName, LoggerFormat.Action action, String svcId, String instName, String extras, String desc);

}
