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


import com.github.cm.heclouds.adapter.mqttadapter.mqtt.MqttSubscribeFuture;
import com.github.cm.heclouds.adapter.mqttadapter.mqtt.MqttSubscription;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.util.Timeout;
import io.netty.util.concurrent.EventExecutor;

import java.util.List;
import java.util.concurrent.TimeoutException;

public class MqttSubscribePromise extends MqttPromise<MqttQoS[]> implements MqttSubscribeFuture {

    private final List<MqttSubscription> subscriptions;
    private Integer packetId;
    private MqttQoS qosLevel;

    private int successes = -1;
    private int downgrades = -1;
    private int failNum = -1;


    public MqttSubscribePromise(EventExecutor executor,
                                List<MqttSubscription> subscriptions) {
        super(executor);
        this.subscriptions = subscriptions;
    }

    public Integer getPacketId() {
        return packetId;
    }

    public void setPacketId(Integer packetId) {
        this.packetId = packetId;
    }

    public MqttQoS getQosLevel() {
        return qosLevel;
    }

    public void setQosLevel(MqttQoS qosLevel) {
        this.qosLevel = qosLevel;
    }

    @Override
    public final MqttMessageType messageType() {
        return MqttMessageType.SUBSCRIBE;
    }

    @Override
    public List<MqttSubscription> subscriptions() {
        return subscriptions;
    }

    @Override
    public boolean isAllSuccess() {
        if (successes < 0 && isDone()) {
            successes = downgrades = 0;
            if (isSuccess()) {
                final List<MqttSubscription> requests = subscriptions();
                final MqttQoS[] results = getNow();
                final int size = Math.min(requests.size(), results.length);
                for (int index = 0; index < size; index++) {
                    MqttQoS granted = results[index];
                    if (granted == MqttQoS.FAILURE) {
                        continue;
                    }
                    MqttQoS request = requests.get(index).qos();
                    if (granted.compareTo(request) < 0) {
                        downgrades++;
                    }
                    successes++;
                }
            }
        }
        return successes == subscriptions().size();
    }

    @Override
    public boolean isPartSuccess() {
        if (successes < 0 && isDone()) {
            successes = failNum = 0;
            if (isSuccess()) {
                final List<MqttSubscription> requests = subscriptions();
                final MqttQoS[] results = getNow();
                final int size = Math.min(requests.size(), results.length);
                for (int index = 0; index < size; index++) {
                    MqttQoS granted = results[index];
                    if (granted == MqttQoS.FAILURE) {
                        failNum++;
                    }
                    successes++;
                }
            }
        }
        return failNum > 0 && successes == subscriptions().size();
    }

    @Override
    public boolean isCompleteSuccess() {
        return isAllSuccess() && downgrades == 0;
    }

    @Override
    public void run(Timeout timeout) {
        tryFailure(new TimeoutException("No response message: expected=SUBACK"));
    }
}
