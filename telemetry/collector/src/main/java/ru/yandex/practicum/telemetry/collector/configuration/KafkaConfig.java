package ru.yandex.practicum.telemetry.collector.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;

@Getter
@Setter
@ConfigurationProperties("collector.kafka")
public class KafkaConfig {
    private ProducerConfig producer;

    @Getter
    @Setter
    public static class ProducerConfig {
        private final Properties properties;
        private final EnumMap<TopicType, String> topics = new EnumMap<>(TopicType.class);

        public ProducerConfig(Properties properties, Map<String, String> topics) {
            this.properties = properties;
            for (Map.Entry<String, String> entry : topics.entrySet()) {
                this.topics.put(TopicType.from(entry.getKey()), entry.getValue());
            }
        }
    }

    public enum TopicType {
        SENSOR_EVENTS("sensors-events"),
        HUB_EVENTS("hubs-events");

        private final String topicName;

        TopicType(String topicName) {
            this.topicName = topicName;
        }

        public static TopicType from(String type) {
            for (TopicType value : values()) {
                if (value.topicName.equalsIgnoreCase(type)) {
                    return value;
                }
            }
            throw new IllegalArgumentException("Unknown topic type: " + type);
        }
    }
}
