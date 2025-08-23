package ru.yandex.practicum.telemetry.collector.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEventType;
import ru.yandex.practicum.telemetry.collector.service.handler.HubEventHandler;
import ru.yandex.practicum.telemetry.collector.service.handler.SensorEventHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
@Slf4j
public class EventController {
    private final Map<SensorEventType, SensorEventHandler> sensorEventHandlers;
    private final Map<HubEventType, HubEventHandler> hubEventHandlers;

    public EventController(Set<SensorEventHandler> sensorHandlers,
                           Set<HubEventHandler> hubHandlers) {
        this.sensorEventHandlers = sensorHandlers.stream()
                .collect(Collectors.toMap(SensorEventHandler::getType, Function.identity()));
        this.hubEventHandlers = hubHandlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getType, Function.identity()));
    }

    @PostMapping("/sensors")
    @ResponseStatus(HttpStatus.OK)
    public void addSensorEvent(@RequestBody SensorEvent event) {
        log.info("==> Add sensor event: {}", event);
        if (sensorEventHandlers.containsKey(event.getType())) {
            sensorEventHandlers.get(event.getType()).handle(event);
        } else {
            log.warn("No sensor handler for event type: {}", event.getType());
            throw new IllegalArgumentException("No handler for event type: " + event.getType());
        }
    }

    @PostMapping("/hubs")
    @ResponseStatus(HttpStatus.OK)
    public void addHubEvent(@RequestBody HubEvent event) {
        log.info("==> Add hub event: {}", event);
        if (hubEventHandlers.containsKey(event.getType())) {
            hubEventHandlers.get(event.getType()).handle(event);
        } else {
            log.warn("No hub handler for event type: {}", event.getType());
            throw new IllegalArgumentException("No handler for event type: " + event.getType());
        }
    }
}
