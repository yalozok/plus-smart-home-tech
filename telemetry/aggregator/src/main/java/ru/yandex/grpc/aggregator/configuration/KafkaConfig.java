package ru.yandex.grpc.aggregator.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Properties;

@Getter
@Setter
@ConfigurationProperties("aggregator.kafka")
public class KafkaConfig {
    private Consumer consumer;
    private Producer producer;
    private Map<String, String> topics;

    @Getter
    @Setter
    public static class Consumer {
        private Properties properties;
    }

    @Getter
    @Setter
    public static class Producer {
        private Properties properties;
    }

    public enum TopicType {
        SENSOR_EVENTS("sensors-events"),
        SNAPSHOT_EVENTS("snapshots-events");

        private final String topicName;

        TopicType(String topicName) {
            this.topicName = topicName;
        }

        public String getTopicName() {
            return topicName;
        }
    }

    public String getTopic(TopicType type) {
        String topic = topics.get(type.getTopicName());
        if (topic == null || topic.isBlank()) {
            throw new IllegalArgumentException("Unknown topic type: " + type);
        }
        return topic;
    }
}
