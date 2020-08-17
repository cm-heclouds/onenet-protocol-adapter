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

package com.github.cm.heclouds.adapter.mqttadapter.mqtt;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.IllegalReferenceCountException;
import lombok.Data;

import static java.util.Objects.requireNonNull;


@Data
public class MqttArticle implements ByteBufHolder {

    private MqttQoS qos;
    private boolean retain = false;
    private String topic;
    private ByteBuf payload;
    private boolean isWill = false;

    public MqttArticle() {
    }

    public MqttArticle(MqttQoS qos, boolean retain, String topic, ByteBuf payload) {
        this.qos = qos;
        this.retain = retain;
        this.topic = topic;
        this.payload = payload;
    }

    public MqttArticle(MqttQoS qos, boolean retain, String topic, ByteBuf payload, boolean isWill) {
        this.qos = requireNonNull(qos, "qos");
        this.retain = retain;
        this.topic = topic;
        this.payload = requireNonNull(payload, "payload");
        this.isWill = isWill;
    }

    public MqttQoS qos() {
        return qos;
    }

    public boolean isRetain() {
        return retain;
    }

    public String topic() {
        return topic;
    }

    public ByteBuf payload() {
        return content();
    }

    public byte[] payloadAsBytes() {
        if (payload == null) {
            return null;
        }
        ByteBuf payload = content().duplicate();
        byte[] bytes = new byte[payload.capacity()];
        payload.readBytes(bytes);
        return bytes;
    }

    @Override
    public int refCnt() {
        return payload.refCnt();
    }

    @Override
    public boolean release() {
        if (payload == null) {
            return true;
        }
        return payload.release();
    }

    @Override
    public boolean release(int decrement) {
        return payload.release(decrement);
    }

    @Override
    public ByteBuf content() {
        final int refCnt = refCnt();
        if (refCnt > 0) {
            return payload;
        }
        throw new IllegalReferenceCountException(refCnt);
    }

    @Override
    public MqttArticle copy() {
        return replace(content().copy());
    }

    @Override
    public MqttArticle duplicate() {
        return replace(content().duplicate());
    }

    @Override
    public MqttArticle retainedDuplicate() {
        return replace(content().retainedDuplicate());
    }

    @Override
    public MqttArticle replace(ByteBuf content) {
        return new MqttArticle(qos, retain, topic, content, isWill);
    }

    @Override
    public MqttArticle retain() {
        content().retain();
        return this;
    }

    @Override
    public MqttArticle retain(int increment) {
        content().retain(increment);
        return this;
    }

    @Override
    public MqttArticle touch() {
        content().touch();
        return this;
    }

    @Override
    public MqttArticle touch(Object hint) {
        content().touch(hint);
        return this;
    }
}
