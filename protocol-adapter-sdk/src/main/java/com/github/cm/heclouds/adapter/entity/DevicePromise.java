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

package com.github.cm.heclouds.adapter.entity;

import com.github.cm.heclouds.adapter.core.entity.CallableFuture;
import com.github.cm.heclouds.adapter.entity.sdk.MessageType;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;


public class DevicePromise<V> extends DefaultPromise<V> implements CallableFuture<V> {

    private String packetId;
    private String productId;
    private String deviceName;
    private MqttMessage mqttMessage;
    private MessageType messageType;

    public DevicePromise() {
    }

    public DevicePromise(String packetId, String productId, String deviceName) {
        this.packetId = packetId;
        this.productId = productId;
        this.deviceName = deviceName;
    }

    public DevicePromise(String packetId, MqttMessage mqttMessage, String productId, String deviceName, MessageType messageType, EventExecutor executor) {
        super(executor);
        this.packetId = packetId;
        this.mqttMessage = mqttMessage;
        this.productId = productId;
        this.deviceName = deviceName;
        this.messageType = messageType;
    }

    @Override
    protected EventExecutor executor() {
        EventExecutor e = super.executor();
        if (e == null) {
            return new DefaultEventExecutor();
        } else {
            return e;
        }
    }

    public String getPacketId() {
        return packetId;
    }

    public String getProductId() {
        return productId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public MqttMessage getMqttMessage() {
        return mqttMessage;
    }

    public void setMqttMessage(MqttMessage mqttMessage) {
        this.mqttMessage = mqttMessage;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
}
