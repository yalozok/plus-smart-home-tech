package ru.yandex.practicum.commerce.request.warehouse;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class AssemblyProductsForOrderRequest {
    @NotNull
    private Map<@NotNull UUID, @NotNull @Positive Long> products;

    @NotNull
    private UUID orderId;
}
