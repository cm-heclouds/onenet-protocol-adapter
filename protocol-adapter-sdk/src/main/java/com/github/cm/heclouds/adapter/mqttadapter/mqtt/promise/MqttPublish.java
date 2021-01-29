package com.github.cm.heclouds.adapter.mqttadapter.mqtt.promise;

import com.github.cm.heclouds.adapter.mqttadapter.mqtt.MqttArticle;

public class MqttPublish {
    private boolean duplicate = false;
    private MqttArticle article;

    public MqttPublish() {
    }

    public MqttPublish(boolean duplicate, MqttArticle article) {
        this.duplicate = duplicate;
        this.article = article;
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }

    public MqttArticle getArticle() {
        return article;
    }

    public void setArticle(MqttArticle article) {
        this.article = article;
    }
}