package ru.yandex.practicum.telemetry.collector.service.handler.sensor;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProducer;

@Component
public class TemperatureSensorEventHandler extends BaseSensorEventHandler<TemperatureSensorAvro> {
    public TemperatureSensorEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public SensorEventProto.PayloadCase getType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR_EVENT;
    }

    @Override
    public TemperatureSensorAvro mapToAvro(SensorEventProto eventProto) {
        TemperatureSensorProto payload = eventProto.getTemperatureSensorEvent();
        return TemperatureSensorAvro.newBuilder()
                .setTemperatureC(payload.getTemperatureC())
                .setTemperatureF(payload.getTemperatureF())
                .build();
    }
}
