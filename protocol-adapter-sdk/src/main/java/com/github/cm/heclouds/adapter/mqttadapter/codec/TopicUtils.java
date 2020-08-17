package com.github.cm.heclouds.adapter.mqttadapter.codec;


import com.github.cm.heclouds.adapter.entity.MessageType;

/**
 * 平台接入机Topic相关工具类
 */
public final class TopicUtils {
    private final static String LOGIN_TOPIC_FORMAT = "$gw-proxy/%s/%s/login";
    private final static String LOGOUT_TOPIC_FORMAT = "$gw-proxy/%s/%s/logout";
    private final static String THING_PROPERTY_UPLOAD_TOPIC_FORMAT = "$gw-proxy/%s/%s/thing/property/post";
    private final static String THING_EVENT_UPLOAD_TOPIC_FORMAT = "$gw-proxy/%s/%s/thing/event/post";
    private final static String THING_RESPOND_TOPIC_FORMAT = "$gw-proxy/%s/%s/thing/property/set_reply";
    private final static String THING_DESIRED_GET_TOPIC_FORMAT = "$gw-proxy/%s/%s/thing/property/desired/get";
    private final static String THING_DESIRED_DELETE_TOPIC_FORMAT = "$gw-proxy/%s/%s/thing/property/desired/delete";

    public final static String LOGIN = "login";
    public final static String LOGOUT = "logout";
    public final static String THING = "thing";
    private final static String NOTIFY = "notify";
    private final static String PROPERTY = "property";
    private final static String EVENT = "event";
    private final static String POST = "post";
    private final static String SET = "set";
    private final static String DESIRED = "desired";
    private final static String GET = "get";
    private final static String DELETE = "delete";
    private final static String REPLY = "reply";

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
     * @return 上行物模型回复的MQTT topic
     */
    static String createThingRespondTopic(String pid, String deviceName) {
        return String.format(THING_RESPOND_TOPIC_FORMAT, pid, deviceName);
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
     * @param topic MQTT topic
     * @return 用"/"分割后的tokens
     */
    public static String[] splitTopic(String topic) {
        String[] tokens = topic.split("/");

        if (topic.endsWith("/")) {
            String[] newSplitted = new String[tokens.length + 1];
            System.arraycopy(tokens, 0, newSplitted, 0, tokens.length);
            newSplitted[tokens.length] = "";
            tokens = newSplitted;
        }
        return tokens;
    }

    /**
     * 校验下行login响应topic
     *
     * @param tokens 用"/"分割后MQTT topic后的tokens
     * @return 是否合法
     */
    public static boolean validateDownLinkLoginTopic(String[] tokens) {
        if (tokens.length != LOGIN_RESPONSE_TOPIC_LEN) {
            return false;
        }
        return REPLY.equals(tokens[4]);
    }

    /**
     * 校验下行logout响应或notify的topic
     *
     * @param tokens 用"/"分割后MQTT topic后的tokens
     * @return 是否合法
     */
    public static boolean validateDownLinkLogoutTopic(String[] tokens) {
        if (tokens.length != LOGOUT_RESPONSE_TOPIC_LEN) {
            return false;
        }
        return REPLY.equals(tokens[4]) || NOTIFY.equals(tokens[4]);
    }

    /**
     * 校验下行物模型数据响应topic
     *
     * @param tokens 用"/"分割后MQTT topic后的tokens
     * @return 是否合法
     */
    public static boolean validateDownLinkThingTopic(String[] tokens) {
        if (tokens.length != THING_DOWN_LINK_TOPIC_LEN && tokens.length != THING_DOWN_LINK_RESPONSE_TOPIC_LEN && tokens.length != THING_DESIRED_DOWN_LINK_RESPONSE_TOPIC_LEN) {
            return false;
        }
        if (!THING.equals(tokens[3])) {
            return false;
        }
        if (!PROPERTY.equals(tokens[4]) && !EVENT.equals(tokens[4])) {
            return false;
        }
        if (!(POST.equals(tokens[5]) || SET.equals(tokens[5]) || DESIRED.equals(tokens[5]))) {
            return false;
        }

        if (tokens.length == THING_DOWN_LINK_TOPIC_LEN) {
            return true;
        } else if (tokens.length == THING_DOWN_LINK_RESPONSE_TOPIC_LEN) {
            return REPLY.equals(tokens[6]);
        } else {
            return (GET.equals(tokens[6]) || DELETE.equals(tokens[6])) && REPLY.equals(tokens[7]);
        }
    }


    /**
     * @param tokens 用"/"分割后MQTT topic后的tokens
     * @return 下行logout topic中对应到adapter内部消息的MessageType
     */
    public static MessageType getDownLinkLogoutMessageType(String[] tokens) {
        return REPLY.equals(tokens[4]) ? MessageType.LOGOUT_RESPONSE : MessageType.LOGOUT_NOTIFY_RESPONSE;
    }

    /**
     * @param tokens 用"/"分割后MQTT topic后的tokens
     * @return 下行topic中对应到adapter内部消息的MessageType
     */
    public static MessageType getDownLinkThingMessageType(String[] tokens) {
        switch (tokens[5]) {
            case SET:
                return MessageType.SET_THING_PROPERTY;
            case POST:
                return PROPERTY.equals(tokens[4]) ? MessageType.UPLOAD_PROPERTY_RESPONSE : MessageType.UPLOAD_EVENT_RESPONSE;
            case DESIRED:
                return GET.equals(tokens[6]) ? MessageType.GET_DESIRED_RESPONSE : MessageType.DELETE_DESIRED_RESPONSE;
            default:
                return MessageType.UNKNOWN;
        }
    }
}
