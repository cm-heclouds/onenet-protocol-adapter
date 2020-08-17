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


import com.github.cm.heclouds.adapter.mqttadapter.mqtt.MqttArticle;
import com.github.cm.heclouds.adapter.mqttadapter.mqtt.MqttPublishFuture;
import com.github.cm.heclouds.adapter.mqttadapter.mqtt.MqttPublishResult;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.Timeout;
import io.netty.util.concurrent.EventExecutor;

import java.util.concurrent.TimeoutException;

public class MqttPublishPromise extends MqttPromise<MqttPublishResult> implements MqttPublishFuture {

    private final MqttArticle article;
    private final boolean duplicate;

    private int packetId;

    public MqttPublishPromise(EventExecutor executor, MqttArticle article, int packetId) {
        super(executor);
        this.article = article;
        this.duplicate = packetId > 0;
        this.packetId = packetId;
    }


    @Override
    public final MqttMessageType messageType() {
        return MqttMessageType.PUBLISH;
    }

    @Override
    public MqttArticle article() {
        return article;
    }

    @Override
    public int packetId() {
        return packetId;
    }

    public void packetId(int packetId) {
        this.packetId = packetId;
    }

    @Override
    public boolean isDuplicate() {
        return duplicate;
    }

    @Override
    public boolean isReleasePending() {
        return (!isDone() || isSuccess()) && article.qos() == MqttQoS.EXACTLY_ONCE;
    }

    @Override
    public void run(Timeout timeout) {
        switch (article.qos()) {
            case AT_LEAST_ONCE:
                tryFailure(new TimeoutException("No response message: expected=PUBACK"));
                break;
            case EXACTLY_ONCE:
                tryFailure(new TimeoutException("No response message: expected=PUBREC"));
                break;
            default:
                tryFailure(new TimeoutException("Incomplete write message"));
                break;
        }
    }
}
