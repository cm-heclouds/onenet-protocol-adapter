/*
 * Copyright (C) 2018 Issey Yamakoshi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.cm.heclouds.adapter.exceptions;


import io.netty.handler.codec.mqtt.MqttMessageType;

/**
 * MQTT包异常
 */
public class MqttPacketException extends IllegalStateException {

    private static final long serialVersionUID = 8859800976034950713L;

    private final MqttMessageType messageType;
    private final int packetId;

    MqttPacketException(String message, MqttMessageType type, int packetId) {
        super(message);
        this.messageType = type;
        this.packetId = packetId;
    }

    MqttPacketException(String message, MqttMessageType type, int packetId, Throwable cause) {
        super(message, cause);
        this.messageType = type;
        this.packetId = packetId;
    }

    public MqttMessageType messageType() {
        return messageType;
    }

    public int packetId() {
        return packetId;
    }
}
