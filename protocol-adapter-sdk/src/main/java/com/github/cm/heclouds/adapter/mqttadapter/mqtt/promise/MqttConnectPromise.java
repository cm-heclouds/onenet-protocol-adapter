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
import io.netty.util.Timeout;
import io.netty.util.concurrent.EventExecutor;

import java.util.concurrent.TimeoutException;

public class MqttConnectPromise extends MqttPromise<MqttConnectResult> {

    private byte[] clientId;
    private String username;
    private byte[] password;

    public MqttConnectPromise(MqttConnect connect, EventExecutor executor) {
        super(executor);
        this.clientId = connect.getClientId();
        this.username = connect.getUsername();
        this.password = connect.getPassword() == null ? null : connect.getPassword().getBytes();
    }

    public String clientId() {
        return new String(clientId);
    }

    public String username() {
        return username(false);
    }

    public String username(boolean remove) {
        if (remove) {
            try {
                return username;
            } finally {
                username = null;
            }
        } else {
            return username;
        }
    }


    public byte[] password() {
        return password(false);
    }

    public byte[] password(boolean remove) {
        if (remove) {
            try {
                return password;
            } finally {
                password = null;
            }
        } else {
            return password.clone();
        }
    }


    @Override
    public final MqttMessageType messageType() {
        return MqttMessageType.CONNECT;
    }

    @Override
    public void run(Timeout timeout) {
        tryFailure(new TimeoutException("No response message: expected=CONNACK"));
    }

    public byte[] getClientId() {
        return clientId;
    }

    public void setClientId(byte[] clientId) {
        this.clientId = clientId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }
}
