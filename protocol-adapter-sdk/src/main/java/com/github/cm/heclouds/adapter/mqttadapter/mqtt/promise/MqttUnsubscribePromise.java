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
import io.netty.handler.codec.mqtt.MqttUnsubAckMessage;
import io.netty.util.Timeout;
import io.netty.util.concurrent.EventExecutor;

import java.util.List;
import java.util.concurrent.TimeoutException;



public class MqttUnsubscribePromise extends MqttPromise<MqttUnsubAckMessage> {

    private final List<String> topicFilters;

    public MqttUnsubscribePromise(EventExecutor executor, List<String> topicFilters) {
        super(executor);
        this.topicFilters = topicFilters;
    }

    @Override
    public final MqttMessageType messageType() {
        return MqttMessageType.UNSUBSCRIBE;
    }

    public List<String> topicFilters() {
        return topicFilters;
    }

    @Override
    public void run(Timeout timeout) {
        tryFailure(new TimeoutException("No response message: expected=UNSUBACK"));
    }
}
