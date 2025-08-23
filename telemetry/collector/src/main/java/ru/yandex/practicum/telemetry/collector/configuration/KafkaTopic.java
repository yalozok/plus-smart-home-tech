package ru.yandex.practicum.telemetry.collector.configuration;

import lombok.Getter;

@Getter
public enum KafkaTopic {
    SENSORS("telemetry.sensors.v1"),
    HUBS("telemetry.hubs.v1");

    private final String topicName;

    KafkaTopic(String topicName) {
        this.topicName = topicName;
    }
}
