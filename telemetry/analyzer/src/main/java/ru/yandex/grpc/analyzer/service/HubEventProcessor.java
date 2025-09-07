package ru.yandex.grpc.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.grpc.analyzer.configuration.KafkaConfig;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

import java.time.Duration;
import java.util.List;

@Component
@Slf4j
public class HubEventProcessor implements Runnable {
    private final KafkaConsumer<String, SpecificRecordBase> consumer;
    private final HubEventService hubEventService;
    private final String topic;

    private static final Duration CONSUME_ATTEMPT_TIMEOUT = Duration.ofMillis(1000);

    public HubEventProcessor(KafkaConfig config, HubEventService hubEventService) {
        this.consumer = new KafkaConsumer<>(config.getHubConsumer().getProperties());
        this.hubEventService = hubEventService;
        this.topic = config.getTopic(KafkaConfig.TopicType.HUB_EVENTS);
    }

    @Override
    public void run() {
        log.info("HubEventProcessor started");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown hook triggered. Waking up hubConsumer...");
            consumer.wakeup();
        }));
        try {
            consumer.subscribe(List.of(topic));
            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(CONSUME_ATTEMPT_TIMEOUT);
                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    HubEventAvro event = handleRecord(record);
                    hubEventService.handleEvent(event);
                }
            }
        } catch (WakeupException e) {
            log.info("hubConsumer shutdown detected.");
        } catch (Exception e) {
            log.error("Unexpected error in HubEventProcessor", e);
        } finally {
            log.info("Closing HubConsumer");
            consumer.close();
        }
    }

    private HubEventAvro handleRecord(ConsumerRecord<String, SpecificRecordBase> record) {
        log.info("Received hub-event record: topic={}, partition={}, offset={}, value={}",
                record.topic(), record.partition(), record.offset(), record.value());
        if (!(record.value() instanceof HubEventAvro)) {
            throw new IllegalArgumentException("Unexpected record type: " + record.value().getClass());
        }
        return (HubEventAvro) record.value();
    }
}
