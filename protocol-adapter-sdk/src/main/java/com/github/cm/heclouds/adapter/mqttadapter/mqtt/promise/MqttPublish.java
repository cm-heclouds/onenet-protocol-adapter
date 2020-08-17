package com.github.cm.heclouds.adapter.mqttadapter.mqtt.promise;

import com.github.cm.heclouds.adapter.mqttadapter.mqtt.MqttArticle;
import lombok.Data;

@Data
public class MqttPublish {
    private boolean duplicate = false;
    private MqttArticle article;

    public MqttPublish() {
    }

    public MqttPublish(boolean duplicate, MqttArticle article) {
        this.duplicate = duplicate;
        this.article = article;
    }
}