package ru.yandex.practicum.commerce.request.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.Map;
import java.util.UUID;

public class ProductReturnRequest {
    private UUID orderId;
    @NotNull
    private Map<@NotNull UUID, @NotNull @Positive Long> products;
}
