package ru.yandex.practicum.delivery.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.commerce.contract.delivery.DeliveryOperation;
import ru.yandex.practicum.commerce.contract.delivery.exception.NoDeliveryFoundException;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.dto.order.OrderDto;
import ru.yandex.practicum.delivery.service.DeliveryService;
import ru.yandex.practicum.logging.Loggable;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/delivery")
public class DeliveryController implements DeliveryOperation {
    private final DeliveryService service;

    @Override
    @Loggable
    public DeliveryDto createDelivery(DeliveryDto deliveryDto) {
        return service.createDelivery(deliveryDto);
    }

    @Override
    @Loggable
    public void successDelivery(UUID deliveryId) throws NoDeliveryFoundException {
        service.successDelivery(deliveryId);
    }

    @Override
    @Loggable
    public void pickDelivery(UUID deliveryId) throws NoDeliveryFoundException {
        service.pickDelivery(deliveryId);
    }

    @Override
    @Loggable
    public void failedDelivery(UUID deliveryId) throws NoDeliveryFoundException {
        service.failedDelivery(deliveryId);
    }

    @Override
    @Loggable
    public BigDecimal calculateDeliveryCost(OrderDto orderDto) throws NoDeliveryFoundException {
        return service.calculateDeliveryCost(orderDto);
    }
}
