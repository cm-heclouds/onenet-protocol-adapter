package com.github.cm.heclouds.adapter.utils;


import com.github.cm.heclouds.adapter.entity.sdk.MessageType;

/**
 * 平台接入机Topic相关工具类
 */
public final class TopicUtils {

    private final static String SPLIT_STR = "/";
    private final static String LOGIN_TOPIC_FORMAT = "$gw-proxy/%s/%s/login";
    private final static String LOGOUT_TOPIC_FORMAT = "$gw-proxy/%s/%s/logout";
    private final static String THING_PROPERTY_UPLOAD_TOPIC_FORMAT = "$gw-proxy/%s/%s/thing/property/post";
    private final static String THING_EVENT_UPLOAD_TOPIC_FORMAT = "$gw-proxy/%s/%s/thing/event/post";
    private final static String THING_REPLY_PROPERTY_GET_TOPIC_FORMAT = "$gw-proxy/%s/%s/thing/property/get_reply";
    private final static String THING_REPLY_PROPERTY_SET_TOPIC_FORMAT = "$gw-proxy/%s/%s/thing/property/set_reply";
    private final static String THING_REPLY_SERVICE_INVOKE_TOPIC_FORMAT = "$gw-proxy/%s/%s/thing/service/%s/invoke_reply";
    private final static String THING_DESIRED_GET_TOPIC_FORMAT = "$gw-proxy/%s/%s/thing/property/desired/get";
    private final static String THING_DESIRED_DELETE_TOPIC_FORMAT = "$gw-proxy/%s/%s/thing/property/desired/delete";
    private final static String THING_PACK_DATA_TOPIC_FORMAT = "$gw-proxy/%s/%s/thing/pack/post";
    private final static String THING_HISTORY_DATA_TOPIC_FORMAT = "$gw-proxy/%s/%s/thing/history/post";
    /**
     * 子设备相关
     */
    private final static String SUB_LOGIN_TOPIC_FORMAT = "$gw-proxy/%s/%s/thing/sub/login";
    private final static String SUB_LOGOUT_TOPIC_FORMAT = "$gw-proxy/%s/%s/thing/sub/logout";
    private final static String SUB_THING_REPLY_PROPERTY_SET_TOPIC_FORMAT = "$gw-proxy/%s/%s/thing/sub/property/set_reply";
    private final static String SUB_THING_REPLY_PROPERTY_GET_TOPIC_FORMAT = "$gw-proxy/%s/%s/thing/sub/property/get_reply";
    private final static String SUB_THING_REPLY_SERVICE_INVOKE_TOPIC_FORMAT = "$gw-proxy/%s/%s/thing/sub/service/invoke_reply";
    private final static String SUB_THING_TOPO_ADD_TOPIC_FORMAT = "$gw-proxy/%s/%s/thing/sub/topo/add";
    private final static String SUB_THING_TOPO_DELETE_TOPIC_FORMAT = "$gw-proxy/%s/%s/thing/sub/topo/delete";
    private final static String SUB_THING_TOPO_GET_TOPIC_FORMAT = "$gw-proxy/%s/%s/thing/sub/topo/get";
    private final static String SUB_THING_TOPO_GET_RESULT_TOPIC_FORMAT = "$gw-proxy/%s/%s/thing/sub/topo/get/result";
    private final static String SUB_THING_TOPO_CHANGE_TOPIC_FORMAT = "$gw-proxy/%s/%s/thing/sub/topo/change_reply";

    public final static String LOGIN = "login";
    public final static String LOGOUT = "logout";
    public final static String THING = "thing";
    public final static String SUB = "sub";
    private final static String NOTIFY = "notify";
    private final static String PROPERTY = "property";
    private final static String EVENT = "event";
    private final static String SERVICE = "service";
    private final static String POST = "post";
    private final static String SET = "set";
    private final static String DESIRED = "desired";
    private final static String GET = "get";
    private final static String GET_REPLY = "get_reply";
    private final static String DELETE = "delete";
    private final static String REPLY = "reply";
    private final static String INVOKE = "invoke";
    private final static String PACK = "pack";
    private final static String HISTORY = "history";

    private final static String TOPO = "topo";
    private final static String ADD = "add";
    private final static String CHANGE = "change";


    private final static int LOGIN_RESPONSE_TOPIC_LEN = 5;
    private final static int LOGOUT_RESPONSE_TOPIC_LEN = 5;
    private final static int THING_DOWN_LINK_TOPIC_LEN = 6;
    private final static int THING_DOWN_LINK_RESPONSE_TOPIC_LEN = 7;
    private final static int THING_DESIRED_DOWN_LINK_RESPONSE_TOPIC_LEN = 8;

    /**
     * @param pid        设备所属产品id
     * @param deviceName 设备名称
     * @return 上行login的MQTT topic
     */
    static String createLoginTopic(String pid, String deviceName) {
        return String.format(LOGIN_TOPIC_FORMAT, pid, deviceName);
    }

    /**
     * @param pid        设备所属产品id
     * @param deviceName 设备名称
     * @return 上行logout的MQTT topic
     */
    static String createLogoutTopic(String pid, String deviceName) {
        return String.format(LOGOUT_TOPIC_FORMAT, pid, deviceName);
    }

    /**
     * @param pid        设备所属产品id
     * @param deviceName 设备名称
     * @return 上行设备属性的MQTT topic
     */
    static String createMqttPropertyUploadTopic(String pid, String deviceName) {
        return String.format(THING_PROPERTY_UPLOAD_TOPIC_FORMAT, pid, deviceName);
    }

    /**
     * @param pid        设备所属产品id
     * @param deviceName 设备名称
     * @return 上行设备事件的MQTT topic
     */
    static String createMqttEventUploadTopic(String pid, String deviceName) {
        return String.format(THING_EVENT_UPLOAD_TOPIC_FORMAT, pid, deviceName);
    }

    /**
     * @param pid        设备所属产品id
     * @param deviceName 设备名称
     * @return 上行获取设备属性期望值的MQTT topic
     */
    static String createDesiredGetMsgTopic(String pid, String deviceName) {
        return String.format(THING_DESIRED_GET_TOPIC_FORMAT, pid, deviceName);
    }

    /**
     * @param pid        设备所属产品id
     * @param deviceName 设备名称
     * @return 上行清空设备属性期望值的MQTT topic
     */
    static String createDesiredDeleteMsgTopic(String pid, String deviceName) {
        return String.format(THING_DESIRED_DELETE_TOPIC_FORMAT, pid, deviceName);
    }

    /**
     * @param pid        设备所属产品id
     * @param deviceName 设备名称
     * @return 回复设置属性请求的上行MQTT topic
     */
    static String createMqttReplyPropertySetRequestTopic(String pid, String deviceName) {
        return String.format(THING_REPLY_PROPERTY_SET_TOPIC_FORMAT, pid, deviceName);
    }

    /**
     * @param pid        设备所属产品id
     * @param deviceName 设备名称
     * @return 回复获取属性请求的上行MQTT topic
     */
    static String createMqttReplyPropertyGetRequestTopic(String pid, String deviceName) {
        return String.format(THING_REPLY_PROPERTY_GET_TOPIC_FORMAT, pid, deviceName);
    }


    /**
     * @param pid        设备所属产品id
     * @param deviceName 设备名称
     * @param identifier 服务标识
     * @return 回复调用设备服务的MQTT topic
     */
    static String createMqttReplyServiceInvokeRequestTopic(String pid, String deviceName, String identifier) {
        return String.format(THING_REPLY_SERVICE_INVOKE_TOPIC_FORMAT, pid, deviceName, identifier);
    }

    /**
     * @param pid        设备所属产品id
     * @param deviceName 设备名称
     * @return 上行logout的MQTT topic
     */
    static String createDevicePackDataTopic(String pid, String deviceName) {
        return String.format(THING_PACK_DATA_TOPIC_FORMAT, pid, deviceName);
    }

    /**
     * @param pid        设备所属产品id
     * @param deviceName 设备名称
     * @return 上行logout的MQTT topic
     */
    static String createDeviceHistoryDataTopic(String pid, String deviceName) {
        return String.format(THING_HISTORY_DATA_TOPIC_FORMAT, pid, deviceName);
    }

    /**
     * @param pid        设备所属产品id
     * @param deviceName 设备名称
     * @return 上行login的MQTT topic
     */
    static String createSubDeviceLoginTopic(String pid, String deviceName) {
        return String.format(SUB_LOGIN_TOPIC_FORMAT, pid, deviceName);
    }

    /**
     * @param pid        设备所属产品id
     * @param deviceName 设备名称
     * @return 上行logout的MQTT topic
     */
    static String createSubDeviceLogoutTopic(String pid, String deviceName) {
        return String.format(SUB_LOGOUT_TOPIC_FORMAT, pid, deviceName);
    }

    /**
     * @param pid        设备所属产品id
     * @param deviceName 设备名称
     * @return 回复设置属性请求的上行MQTT topic
     */
    static String createMqttSubDeviceReplyPropertySetRequestTopic(String pid, String deviceName) {
        return String.format(SUB_THING_REPLY_PROPERTY_SET_TOPIC_FORMAT, pid, deviceName);
    }

    /**
     * @param pid        设备所属产品id
     * @param deviceName 设备名称
     * @return 回复获取属性请求的上行MQTT topic
     */
    static String createMqttSubDeviceReplyPropertyGetRequestTopic(String pid, String deviceName) {
        return String.format(SUB_THING_REPLY_PROPERTY_GET_TOPIC_FORMAT, pid, deviceName);
    }

    /**
     * @param pid        设备所属产品id
     * @param deviceName 设备名称
     * @return 回复调用设备服务的MQTT topic
     */
    static String createMqttSubDeviceReplyServiceInvokeRequestTopic(String pid, String deviceName) {
        return String.format(SUB_THING_REPLY_SERVICE_INVOKE_TOPIC_FORMAT, pid, deviceName);
    }

    /**
     * @param pid        设备所属产品id
     * @param deviceName 设备名称
     * @return 新增拓扑关系的MQTT topic
     */
    static String createSubDeviceTopoAddTopic(String pid, String deviceName) {
        return String.format(SUB_THING_TOPO_ADD_TOPIC_FORMAT, pid, deviceName);
    }

    /**
     * @param pid        设备所属产品id
     * @param deviceName 设备名称
     * @return 删除拓扑关系的MQTT topic
     */
    static String createSubDeviceTopoDeleteTopic(String pid, String deviceName) {
        return String.format(SUB_THING_TOPO_DELETE_TOPIC_FORMAT, pid, deviceName);
    }

    /**
     * @param pid        设备所属产品id
     * @param deviceName 设备名称
     * @return 获取拓扑关系的MQTT topic
     */
    static String createSubDeviceTopoGetTopic(String pid, String deviceName) {
        return String.format(SUB_THING_TOPO_GET_TOPIC_FORMAT, pid, deviceName);
    }

    public static MessageType getDownLinkThingSubMessageType(String[] tokens) {
        switch (tokens[5]) {
            case LOGIN:
                return (tokens.length == 7) ? MessageType.SUB_LOGIN_RESPONSE : MessageType.UNKNOWN;
            case LOGOUT:
                return (tokens.length == 7) ? MessageType.SUB_LOGOUT_RESPONSE : MessageType.UNKNOWN;
            case TOPO:
                switch (tokens[6]) {
                    case GET:
                        return (tokens.length == 8) ? MessageType.SUB_DEV_TOPO_GET_RESPONSE : MessageType.UNKNOWN;
                    case ADD:
                        return (tokens.length == 8) ? MessageType.SUB_DEV_TOPO_ADD_RESPONSE : MessageType.UNKNOWN;
                    case CHANGE:
                        return (tokens.length == 7) ? MessageType.SUB_DEV_TOPO_CHANGE : MessageType.UNKNOWN;
                    case DELETE:
                        return (tokens.length == 8) ? MessageType.SUB_DEV_TOPO_DELETE_RESPONSE : MessageType.UNKNOWN;
                    default:
                        return MessageType.UNKNOWN;
                }
            case SERVICE:
                return (tokens.length == 7) ? MessageType.SUB_INVOKE_SERVICE_REQUEST : MessageType.UNKNOWN;
            case PROPERTY:
                switch (tokens[6]) {
                    case GET:
                        return (tokens.length == 7) ? MessageType.SUB_GET_PROPERTY_REQUEST : MessageType.UNKNOWN;
                    case SET:
                        return (tokens.length == 7) ? MessageType.SUB_SET_THING_PROPERTY_REQUEST : MessageType.UNKNOWN;
                    default:
                        return MessageType.UNKNOWN;
                }
            default:
                return MessageType.UNKNOWN;
        }
    }

    /**
     * @param pid        设备所属产品id
     * @param deviceName 设备名称
     * @return 拓扑关系改变回复的MQTT topic
     */
    static String createSubDeviceTopoChangeReplyTopic(String pid, String deviceName) {
        return String.format(SUB_THING_TOPO_CHANGE_TOPIC_FORMAT, pid, deviceName);
    }

    /**
     * @param topic MQTT topic
     * @return 用"/"分割后的tokens
     */
    public static String[] splitTopic(String topic) {
        String[] tokens = topic.split(SPLIT_STR);

        if (topic.endsWith(SPLIT_STR)) {
            String[] newSplitted = new String[tokens.length + 1];
            System.arraycopy(tokens, 0, newSplitted, 0, tokens.length);
            newSplitted[tokens.length] = "";
            tokens = newSplitted;
        }
        return tokens;
    }

    /**
     * @param tokens 用"/"分割后MQTT topic后的tokens
     * @return 下行logout topic中对应到adapter内部消息的MessageType
     */
    public static MessageType getDownLinkLogoutMessageType(String[] tokens) {
        return REPLY.equals(tokens[4]) ? MessageType.LOGOUT_RESPONSE : MessageType.LOGOUT_NOTIFY;
    }

    /**
     * @param tokens 用"/"分割后MQTT topic后的tokens
     * @return 下行topic中对应到adapter内部消息的MessageType
     */
    public static MessageType getDownLinkThingMessageType(String[] tokens) {
        switch (tokens[4]) {
            case PROPERTY:
                return getDownLinkThingPropertyMessageType(tokens);
            case EVENT:
                return (tokens.length == 7 && POST.equals(tokens[5])) ? MessageType.UPLOAD_EVENT_RESPONSE : MessageType.UNKNOWN;
            case PACK:
                return (tokens.length == 7 && POST.equals(tokens[5])) ? MessageType.UPLOAD_PACK_DATA_RESPONSE : MessageType.UNKNOWN;
            case HISTORY:
                return (tokens.length == 7 && POST.equals(tokens[5])) ? MessageType.UPLOAD_HISTORY_DATA_RESPONSE : MessageType.UNKNOWN;
            case SERVICE:
                return (tokens.length == 7 && INVOKE.equals(tokens[6])) ? MessageType.INVOKE_SERVICE_REQUEST : MessageType.UNKNOWN;
            case SUB:
                return getDownLinkThingSubMessageType(tokens);
            default:
                return MessageType.UNKNOWN;
        }
    }

    public static MessageType getDownLinkThingPropertyMessageType(String[] tokens) {
        switch (tokens[5]) {
            case POST:
                return (tokens.length == 7) ? MessageType.UPLOAD_PROPERTY_RESPONSE : MessageType.UNKNOWN;
            case SET:
                return (tokens.length == 6) ? MessageType.SET_THING_PROPERTY_REQUEST : MessageType.UNKNOWN;
            case GET:
                return (tokens.length == 6) ? MessageType.GET_PROPERTY_REQUEST : MessageType.UNKNOWN;
            case DESIRED:
                if (GET.equals(tokens[6])) {
                    return MessageType.GET_DESIRED_RESPONSE;
                } else {
                    return MessageType.DELETE_DESIRED_RESPONSE;
                }
            default:
                return MessageType.UNKNOWN;
        }
    }

    /**
     * @param pid        设备所属产品id
     * @param deviceName 设备名称
     * @return 网关同步结果响应的MQTT topic
     */
    static String createSubDeviceTopoGetResultTopic(String pid, String deviceName) {
        return String.format(SUB_THING_TOPO_GET_RESULT_TOPIC_FORMAT, pid, deviceName);
    }
}
