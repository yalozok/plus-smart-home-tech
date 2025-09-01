package ru.yandex.practicum.telemetry.collector.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.collector.configuration.KafkaConfig;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;

@Component
@Slf4j
public class KafkaEventProducer implements AutoCloseable {
    protected final KafkaProducer<String, SpecificRecordBase> kafkaProducer;
    protected final EnumMap<KafkaConfig.TopicType, String> topics;

    public KafkaEventProducer(KafkaConfig kafkaConfig) {
        this.kafkaProducer = new KafkaProducer<>(kafkaConfig.getProducer().getProperties());
        this.topics = kafkaConfig.getProducer().getTopics();
    }

    public void send(SpecificRecordBase event, String hubId, Instant timestamp, KafkaConfig.TopicType topic) {
        String topicName = topics.get(topic);
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(
                topicName,
                null,
                timestamp.toEpochMilli(),
                hubId,
                event
        );

        log.info("<== Send message: {} to topic: {}", record, topic);
        kafkaProducer.send(record);
    }

    @Override
    public void close() {
        kafkaProducer.flush();
        kafkaProducer.close(Duration.ofSeconds(10));
    }
}
