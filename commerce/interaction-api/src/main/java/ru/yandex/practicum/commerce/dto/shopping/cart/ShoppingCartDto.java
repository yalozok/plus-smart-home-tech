package ru.yandex.practicum.commerce.dto.shopping.cart;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class ShoppingCartDto {
    @NotNull
    private UUID id;

    @NotNull
    private Map<@NotNull UUID, @NotNull @Positive Long> products;
}
