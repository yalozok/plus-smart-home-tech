package ru.yandex.grpc.analyzer.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Optional;
import java.util.Properties;

@Getter
@Setter
@ConfigurationProperties("analyzer.kafka")
public class KafkaConfig {
    private ConsumerProperties hubConsumer;
    private ConsumerProperties snapshotConsumer;
    private Map<String, String> topics;

    @Getter
    @Setter
    public static class ConsumerProperties {
        private Properties properties;
    }

    public enum TopicType {
        HUB_EVENTS("hub-events"),
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
        return Optional.ofNullable(topics.get(type.getTopicName()))
                .filter(topic -> !topic.isBlank())
                .orElseThrow(() -> new IllegalArgumentException("Unknown topic type: " + type));
    }
}
