package com.github.cm.heclouds.adapter.mqttadapter.mqtt;

import io.netty.handler.codec.mqtt.MqttMessageType;
import lombok.Data;

@Data
public class MqttPublishResult {

    private final MqttMessageType messageType;
    private int packetId;

    public MqttPublishResult(MqttMessageType messageType, int packetId) {
        this.messageType = messageType;
        this.packetId = packetId;
    }
}