package com.github.cm.heclouds.adapter.mqttadapter.mqtt;

import io.netty.handler.codec.mqtt.MqttMessageType;

public class MqttPublishResult {

    private final MqttMessageType messageType;
    private int packetId;

    public MqttPublishResult(MqttMessageType messageType, int packetId) {
        this.messageType = messageType;
        this.packetId = packetId;
    }

    public MqttMessageType getMessageType() {
        return messageType;
    }

    public int getPacketId() {
        return packetId;
    }

    public void setPacketId(int packetId) {
        this.packetId = packetId;
    }
}