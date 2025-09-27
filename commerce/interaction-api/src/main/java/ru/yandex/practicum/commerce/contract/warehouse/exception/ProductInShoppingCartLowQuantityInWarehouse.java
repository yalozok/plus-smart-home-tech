package ru.yandex.practicum.commerce.contract.warehouse.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProductInShoppingCartLowQuantityInWarehouse extends RuntimeException {
    public ProductInShoppingCartLowQuantityInWarehouse(UUID productId) {
        super("Product " + productId + " has low quantity in warehouse");
    }
}
