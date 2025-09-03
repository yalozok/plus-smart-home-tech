package ru.yandex.grpc.aggregator.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.grpc.aggregator.configuration.KafkaConfig;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class AggregationStarter {
    private final SnapshotServiceImpl snapshotService = new SnapshotServiceImpl();
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new ConcurrentHashMap<>();
    private final EnumMap<KafkaConfig.TopicType, String> topics = new EnumMap<>(KafkaConfig.TopicType.class);

    private final KafkaConsumer<String, SpecificRecordBase> consumer;
    private final KafkaProducer<String, SpecificRecordBase> producer;

    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);

    public AggregationStarter(KafkaConfig config) {
        this.consumer = new KafkaConsumer<>(config.getConsumer().getProperties());
        this.producer = new KafkaProducer<>(config.getProducer().getProperties());
        for (KafkaConfig.TopicType type : KafkaConfig.TopicType.values()) {
            topics.put(type, config.getTopic(type));
        }
    }

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown hook triggered. Waking up consumer...");
            consumer.wakeup();
        }));

        try {
            consumer.subscribe(List.of(topics.get(KafkaConfig.TopicType.SENSOR_EVENTS)));

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    SensorEventAvro event = handleRecord(record);
                    Optional<SensorsSnapshotAvro> snapshot = snapshotService.updateState(event);
                    snapshot.ifPresent(this::sendSnapshot);

                    TopicPartition tp = new TopicPartition(record.topic(), record.partition());
                    currentOffsets.put(tp, new OffsetAndMetadata(record.offset() + 1));
                }

                if (!currentOffsets.isEmpty()) {
                    consumer.commitAsync(new HashMap<>(currentOffsets), (offsets, exception) -> {
                        if (exception != null) {
                            log.warn("Failed to commit offsets: {}", offsets, exception);
                        }
                    });
                }
            }
        } catch (WakeupException e) {
            log.info("Consumer shutdown detected.");
        } catch (Exception e) {
            log.error("Unexpected error in consumer loop", e);
        } finally {
            try {
                producer.flush();
                if (!currentOffsets.isEmpty()) {
                    consumer.commitSync(currentOffsets);
                }
            } finally {
                log.info("Closing consumer and producer");
                consumer.close();
                producer.close();
            }
        }
    }

    private SensorEventAvro handleRecord(ConsumerRecord<String, SpecificRecordBase> record) {
        log.info("Received record: topic={}, partition={}, offset={}, value={}",
                record.topic(), record.partition(), record.offset(), record.value());
        if (!(record.value() instanceof SensorEventAvro)) {
            throw new IllegalArgumentException("Unexpected record type: " + record.value().getClass());
        }
        return (SensorEventAvro) record.value();
    }

    private void sendSnapshot(SensorsSnapshotAvro snapshot) {
        String topic = topics.get(KafkaConfig.TopicType.SNAPSHOT_EVENTS);
        ProducerRecord<String, SpecificRecordBase> record = new ProducerRecord<>(topic, snapshot);

        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                log.error("Failed to send snapshot: {}", snapshot, exception);
            } else {
                log.info("Snapshot sent: topic={}, partition={}, offset={}",
                        metadata.topic(), metadata.partition(), metadata.offset());
            }
        });
    }
}
