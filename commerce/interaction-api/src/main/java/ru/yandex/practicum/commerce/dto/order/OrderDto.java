package ru.yandex.practicum.commerce.dto.order;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
public class OrderDto {
    private UUID orderId;

    private UUID shoppingCartId;

    @NotNull
    private Map<@NotNull UUID, @NotNull @Positive Long> products;

    private UUID paymentId;
    private UUID deliveryId;
    private OrderState state;
    private double deliveryWeight;
    private double deliveryVolume;
    private boolean fragile;
    private BigDecimal totalPrice;
    private BigDecimal deliveryPrice;
    private BigDecimal productPrice;
}
