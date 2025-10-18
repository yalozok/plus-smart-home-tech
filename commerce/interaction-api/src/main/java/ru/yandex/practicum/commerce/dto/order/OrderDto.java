package ru.yandex.practicum.commerce.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
public class OrderDto {
    private UUID orderId;
    @NotBlank
    String username;

    @NotNull
    private UUID shoppingCartId;

    @NotNull
    private Map<@NotNull UUID, @NotNull @Positive Long> products;

    private UUID paymentId;
    private UUID deliveryId;
    private OrderState orderState;
    @Positive
    private double deliveryWeight;
    @Positive
    private double deliveryVolume;
    private boolean fragile;
    @Positive
    private BigDecimal totalPrice;
    @Positive
    private BigDecimal deliveryPrice;
    @Positive
    private BigDecimal productPrice;
}
