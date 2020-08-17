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

package com.github.cm.heclouds.adapter.mqttadapter.handler;

import com.github.cm.heclouds.adapter.exceptions.MqttUnexpectedPacketException;
import com.github.cm.heclouds.adapter.mqttadapter.mqtt.MqttFixedHeaders;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessage;
import io.netty.handler.codec.mqtt.MqttMessageType;


/**
 * Mqtt UnexpectedPacket Handler
 */
public class MqttUnexpectedPacketHandler {

    void connAck(ChannelHandlerContext ctx, Throwable cause) {
        ctx.fireExceptionCaught(
                new MqttUnexpectedPacketException(MqttMessageType.CONNACK, cause));
    }

    void pubAck(ChannelHandlerContext ctx, int packetId, Throwable cause) {
        ctx.fireExceptionCaught(
                new MqttUnexpectedPacketException(MqttMessageType.PUBACK, packetId, cause));
    }


    void subAck(ChannelHandlerContext ctx, int packetId, Throwable cause) {
        ctx.fireExceptionCaught(
                new MqttUnexpectedPacketException(MqttMessageType.SUBACK, packetId, cause));
    }

    void unsubAck(ChannelHandlerContext ctx, int packetId, Throwable cause) {
        ctx.fireExceptionCaught(
                new MqttUnexpectedPacketException(MqttMessageType.UNSUBACK, packetId, cause));
    }

    public void unsupported(ChannelHandlerContext ctx, MqttMessage msg) {
        final MqttMessageType type = msg.fixedHeader().messageType();
        ctx.fireExceptionCaught(new MqttUnexpectedPacketException(type));
        if (type == MqttMessageType.PINGREQ) {
            ctx.channel().writeAndFlush(new MqttMessage(MqttFixedHeaders.PINGRESP_HEADER));
        } else {
            ctx.close();
        }
    }
}
