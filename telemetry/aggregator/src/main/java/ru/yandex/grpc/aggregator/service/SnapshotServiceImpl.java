package ru.yandex.grpc.aggregator.service;

import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SnapshotServiceImpl implements SnapshotService {
    private final Map<String, SensorsSnapshotAvro> snapshotsInMemory = new HashMap<>();

    @Override
    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        SensorsSnapshotAvro snapshot = snapshotsInMemory.getOrDefault(event.getHubId(), new SensorsSnapshotAvro());

        if (snapshot.getSensorsState() == null) {
            snapshot.setSensorsState(new HashMap<>());
        }

        SensorStateAvro oldState = snapshot.getSensorsState().get(event.getId());
        if (oldState != null &&
                (oldState.getTimestamp().isAfter(event.getTimestamp()) ||
                        oldState.getData().equals(event.getPayload()))) {
            return Optional.empty();
        }

        SensorStateAvro state = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();

        snapshot.getSensorsState().put(event.getId(), state);
        snapshot.setTimestamp(event.getTimestamp());
        snapshot.setHubId(event.getHubId());
        snapshotsInMemory.put(event.getHubId(), snapshot);

        return Optional.of(snapshot);
    }
}
