package ru.yandex.practicum.commerce.contract.delivery;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.commerce.contract.delivery.exception.NoDeliveryFoundException;
import ru.yandex.practicum.commerce.dto.delivery.DeliveryDto;
import ru.yandex.practicum.commerce.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

@RequestMapping("/api/v1/delivery")
public interface DeliveryOperation {
    @PutMapping
    DeliveryDto createDelivery(@RequestBody @NotNull @Valid DeliveryDto deliveryDto);

    @PostMapping("/successful")
    void successDelivery(@RequestParam @NotNull UUID deliveryId) throws NoDeliveryFoundException;

    @PostMapping("/picked")
    void pickDelivery(@RequestParam @NotNull UUID deliveryId) throws NoDeliveryFoundException;

    @PostMapping("/failed")
    void failedDelivery(@RequestParam @NotNull UUID deliveryId) throws NoDeliveryFoundException;

    @PostMapping("/cost")
    BigDecimal calculateDeliveryCost(@RequestBody @NotNull @Valid OrderDto orderDto) throws NoDeliveryFoundException;
}
