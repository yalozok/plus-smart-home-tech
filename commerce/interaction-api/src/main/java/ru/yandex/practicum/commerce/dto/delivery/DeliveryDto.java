package ru.yandex.practicum.commerce.dto.delivery;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.commerce.dto.AddressDto;

import java.util.UUID;

@Data
public class DeliveryDto {
    private UUID deliveryId;

    @NotNull
    private AddressDto from;

    @NotNull
    private AddressDto to;

    @NotNull
    private UUID orderId;

    @NotNull
    private DeliveryState deliveryState;
}
