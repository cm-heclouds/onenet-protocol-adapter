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

package com.github.cm.heclouds.adapter.mqttadapter.mqtt.promise;

import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;

public class MqttPingResult {

    private final MqttMessageType messageType;
    private final boolean isDup;
    private final MqttQoS qosLevel;
    private final boolean isRetain;
    private final int remainingLength;

    public MqttPingResult(MqttMessageType messageType, boolean isDup, MqttQoS qosLevel, boolean isRetain, int remainingLength) {
        this.messageType = messageType;
        this.isDup = isDup;
        this.qosLevel = qosLevel;
        this.isRetain = isRetain;
        this.remainingLength = remainingLength;
    }

    public MqttMessageType getMessageType() {
        return messageType;
    }

    public boolean isDup() {
        return isDup;
    }

    public MqttQoS getQosLevel() {
        return qosLevel;
    }

    public boolean isRetain() {
        return isRetain;
    }

    public int getRemainingLength() {
        return remainingLength;
    }
}
